package com.aston.stockapp.domain.portfolio;

import com.aston.stockapp.api.YahooFinanceService;
import com.aston.stockapp.domain.transaction.Transaction;
import com.aston.stockapp.domain.transaction.TransactionRepository;
import com.aston.stockapp.user.CustomUserDetails;
import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import com.aston.stockapp.util.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PortfolioController {

    @Autowired private PortfolioService portfolioService;
    @Autowired private YahooFinanceService yahooFinanceService;
    @Autowired private PortfolioItemRepository portfolioItemRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private PortfolioUpdateService portfolioUpdateService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/portfolio")
    public String viewPortfolio(Model model, Authentication authentication, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size, RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Use authentication to check and cast to CustomUserDetails
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                Long currentUserId = userDetails.getUser().getId();
                Portfolio portfolio = portfolioService.getPortfolio(currentUserId);
                List<PortfolioItem> portfolioItems = portfolio.getItems();
                portfolioService.calculatePortfolioStats(portfolio);

                Pageable pageable = PageRequest.of(page, size);
                Page<PortfolioItem> portfolioPage = portfolioItemRepository.findByPortfolioUserId(currentUserId, pageable);

                Map<String, BigDecimal> sectorDistribution = portfolioService.getPortfolioSectorDistribution(currentUserId);
                boolean hasSectorData = sectorDistribution != null && !sectorDistribution.isEmpty() && !portfolio.getItems().isEmpty();
                BigDecimal portfolioVolatility = portfolioService.calculatePortfolioVolatility(currentUserId);
                List<BigDecimal> historicalPerformanceData = portfolioService.calculateHistoricalPerformance(portfolioItems);
                List<Double> historicalPerformance = historicalPerformanceData.stream().map(BigDecimal::doubleValue).collect(Collectors.toList());

                model.addAttribute("itemsPage", portfolioPage);
                model.addAttribute("historicalPerformance", historicalPerformance);
                model.addAttribute("portfolio", portfolio);
                model.addAttribute("totalCost", portfolio.getTotalCost());
                model.addAttribute("totalValue", portfolio.getTotalValue());
                model.addAttribute("totalChangePercent", portfolio.getTotalChangePercent());
                model.addAttribute("isEditing", false);
                model.addAttribute("hasSectorData", hasSectorData);
                model.addAttribute("sectorDistribution", sectorDistribution);
                model.addAttribute("portfolioVolatility", portfolioVolatility);
                portfolioService.getCurrentUserBalance().ifPresent(balance -> model.addAttribute("balance", balance));
                return "portfolio";
            } else {
                // if the principal is not expected type for some reason, redirect to login
                redirectAttributes.addFlashAttribute("loginAlert", "Unexpected user details type. Please login again.");
                return "redirect:/login";
            }
        } else {
            // For unauthenticated users add attribute and redirect to login page
            redirectAttributes.addFlashAttribute("loginAlert", "You need to login to view your portfolio");
            return "redirect:/login";
        }
    }

    @PostMapping("/portfolio/add")
    public String addToPortfolio(@RequestParam String symbol, @RequestParam int quantity, @RequestParam(required = false) BigDecimal customPrice, @RequestParam String action, RedirectAttributes redirectAttributes) {
        try {
            BigDecimal finalPrice = customPrice != null && customPrice.compareTo(BigDecimal.ZERO) > 0 ? customPrice : yahooFinanceService.fetchStockData(symbol).getPrice();
            int adjustedQuantity = "sell".equalsIgnoreCase(action) ? -quantity : quantity;
            boolean isBuying = "buy".equalsIgnoreCase(action);

            portfolioService.addStockToPortfolio(symbol, adjustedQuantity, finalPrice, isBuying);

            String transactionType = isBuying ? "Bought" : "Sold";
            redirectAttributes.addFlashAttribute("transactionSuccess", "Success: " + transactionType + " " + quantity + " of this stock (" + symbol + ").");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("transactionFailed", "Error: " + e.getMessage());
        }
        return "redirect:/stocks/" + symbol;
    }


//    @PostMapping("/portfolio/add")
//    public String addToPortfolio(@RequestParam String symbol, @RequestParam int quantity, @RequestParam(required = false) BigDecimal customPrice, @RequestParam String action, RedirectAttributes redirectAttributes) {
//        try {
//            BigDecimal finalPrice = customPrice != null && customPrice.compareTo(BigDecimal.ZERO) > 0 ? customPrice : yahooFinanceService.fetchStockData(symbol).getPrice();
//            int adjustedQuantity = "sell".equalsIgnoreCase(action) ? -quantity : quantity;
//
//            CompletableFuture<Void> transactionResult = portfolioService.addStockToPortfolio(symbol, adjustedQuantity, finalPrice);
//            transactionResult.join();
//
//            if ("sell".equalsIgnoreCase(action)) {
//                redirectAttributes.addFlashAttribute("transactionSuccess", "Success: Sold " + quantity + " of " + symbol + ".");
//            } else {
//                redirectAttributes.addFlashAttribute("transactionSuccess", "Success: Bought " + quantity + " of " + symbol + ".");
//            }
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("transactionError", "Error processing transaction for " + symbol + ": " + e.getMessage());
//        }
//        return "redirect:/stocks/" + symbol;
//    }

    @GetMapping("/portfolio/edit")
    public String editPortfolio(@RequestParam("id") Long itemId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size, Model model) {
        try {
            Long userId = portfolioService.getCurrentUser();
            Portfolio portfolio = portfolioService.getPortfolio(userId);

            portfolioService.calculatePortfolioStats(portfolio);

            // Calculate sector distribution for the portfolio
            Map<String, BigDecimal> sectorDistribution = portfolioService.getPortfolioSectorDistribution(userId);
            boolean hasSectorData = sectorDistribution != null && !sectorDistribution.isEmpty() && !portfolio.getItems().isEmpty();
            BigDecimal portfolioVolatility = portfolioService.calculatePortfolioVolatility(userId);

            // Fetch the editable item
            PortfolioItem editableItem = portfolioItemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Edit item not found"));;
            List<PortfolioItem> editableItemList = Collections.singletonList(editableItem);

            // Setup pageable with the current page and size
            Pageable pageable = PageRequest.of(page, size);
            Page<PortfolioItem> singleItemPage = new PageImpl<>(editableItemList, pageable, editableItemList.size());

            model.addAttribute("itemsPage", singleItemPage);
            model.addAttribute("portfolio", portfolio);
            model.addAttribute("editingItemId", itemId);
            model.addAttribute("totalCost", portfolio.getTotalCost());
            model.addAttribute("totalValue", portfolio.getTotalValue());
            model.addAttribute("totalChangePercent", portfolio.getTotalChangePercent());
            model.addAttribute("hasSectorData", hasSectorData);
            model.addAttribute("sectorDistribution", sectorDistribution);
            model.addAttribute("portfolioVolatility", portfolioVolatility);
            model.addAttribute("isEditing", true);
            portfolioService.getCurrentUserBalance().ifPresent(balance -> model.addAttribute("balance", balance));

            return "portfolio";
        } catch (IllegalStateException e) {
            return "redirect:/login";
        }
    }

//    @GetMapping("/portfolio/edit")
//    public String editPortfolio(Model model) {
//        try {
//            Long userId = portfolioService.getCurrentUser();
//            Portfolio portfolio = portfolioService.getPortfolio(userId);
//            model.addAttribute("portfolio", portfolio);
//            model.addAttribute("totalCost", portfolio.getTotalCost());
//            model.addAttribute("totalValue", portfolio.getTotalValue());
//            model.addAttribute("totalChangePercent", portfolio.getTotalChangePercent());
//            model.addAttribute("isEditing", true);
//            return "portfolio";
//        } catch (IllegalStateException e) {
//            return "redirect:/login";
//        }
//    }

    @PostMapping("/portfolio/save")
    public String savePortfolioItem(@RequestParam("id") Long itemId, @RequestParam("newQuantity") int newQuantity, @RequestParam("newPurchasePrice") double newPurchasePrice, RedirectAttributes redirectAttributes) {
        try {
            PortfolioItem item = portfolioItemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));

            int quantityDifference = newQuantity - item.getQuantity();
            String transactionType;

            if (quantityDifference > 0) {
                transactionType = "Edit (Buy)";
            } else {
                transactionType = "Edit (Sell)";
                quantityDifference = -quantityDifference; // <-- Ensures quantity is positive for logging
            }

            double individualCost = item.getPurchasePrice();
            double totalCostDifference = individualCost * quantityDifference;

            User user = item.getPortfolio().getUser();
            BigDecimal newBalance;

            // Adjust balance based on whether the edit was effectively a buy or sell
            if ("Edit (Buy)".equals(transactionType)) {
                newBalance = user.getBalance().subtract(BigDecimal.valueOf(totalCostDifference));
            } else { // "Edit (Sell)"
                newBalance = user.getBalance().add(BigDecimal.valueOf(totalCostDifference));
            }

            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientBalanceException("Insufficient balance for the operation.");
            }

            // Proceed with updating the item and user's balance if sufficient funds are available
            user.setBalance(newBalance);
            item.setQuantity(newQuantity);
            item.setPurchasePrice(newPurchasePrice);
            portfolioItemRepository.save(item);
            userRepository.save(user);

            // Log the edit as a transaction with corrected quantity and cost
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setDateTime(LocalDateTime.now());
            transaction.setStockTicker(item.getStock().getTicker());
            transaction.setQuantity(quantityDifference);
            transaction.setPurchasePrice(BigDecimal.valueOf(newPurchasePrice));
            transaction.setTotalCost(BigDecimal.valueOf(totalCostDifference));
            transaction.setTransactionType(transactionType);
            transactionRepository.save(transaction);

            redirectAttributes.addFlashAttribute("successMessage", "Portfolio item updated successfully.");
            return "redirect:/portfolio";
        } catch (InsufficientBalanceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/portfolio";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating portfolio item: " + e.getMessage());
            return "redirect:/portfolio";
        }
    }


//    @PostMapping("/portfolio/save")
//    public String savePortfolioItem(@RequestParam("id") Long itemId, @RequestParam("newQuantity") int newQuantity, @RequestParam("newPurchasePrice") double newPurchasePrice, RedirectAttributes redirectAttributes) {
//        try {
//            PortfolioItem item = portfolioItemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));
//            double costDifference = (newPurchasePrice - item.getPurchasePrice()) * newQuantity;
//
//            // Retrieve the user and adjust balance based on the new price
//            User user = item.getPortfolio().getUser();
//            BigDecimal balanceAdjustment = BigDecimal.valueOf(costDifference);
//
//            if(costDifference > 0) {
//                // If cost difference is positive, the new purchase price is higher so subtract from balance
//                user.setBalance(user.getBalance().subtract(balanceAdjustment));
//            } else {
//                // If the cost difference is <= 0, the new purchase price is lower or equal so add the difference to balance
//                user.setBalance(user.getBalance().add(balanceAdjustment.abs()));
//            }
//
//            // Update item with new quantity and purchase price
//            item.setQuantity(newQuantity);
//            item.setPurchasePrice(newPurchasePrice);
//            portfolioItemRepository.save(item);
//
//            Transaction transaction = new Transaction();
//            transaction.setUser(user);
//            transaction.setDateTime(LocalDateTime.now());
//            transaction.setStockTicker(item.getStock().getTicker());
//            transaction.setQuantity(newQuantity);
//            transaction.setPurchasePrice(BigDecimal.valueOf(newPurchasePrice));
//            transaction.setTotalCost(BigDecimal.valueOf(costDifference).abs());
//            transaction.setTransactionType("Edit (Deleted)");
//            transactionRepository.save(transaction);
//
//            userRepository.save(user);
//
//            redirectAttributes.addFlashAttribute("successMessage", "Portfolio updated successfully.");
//            return "redirect:/portfolio";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Error updating portfolio item: " + e.getMessage());
//            return "redirect:/portfolio";
//        }
//    }

//    @PostMapping("/portfolio/transaction")
//    public String savePortfolio(@RequestParam String symbol, @RequestParam int quantity, @RequestParam String action, Model model, Authentication authentication) {
//        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
//            return "redirect:/login";
//        }
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Long userId = userDetails.getUser().getId();
//
//        boolean isBuying = "buy".equalsIgnoreCase(action);
//        try {
//            // Synchronously call the updateStockInPortfolio method
//            portfolioService.updateStockInPortfolio(userId, symbol, quantity, isBuying);
//
//            // Prepare the success message
//            String message = isBuying ? "Successfully purchased " : "Successfully sold ";
//            message += quantity + " units of " + symbol + ".";
//            model.addAttribute("transactionSuccess", message);
//        } catch (Exception e) {
//            model.addAttribute("transactionError", "Error processing transaction for " + symbol + ".");
//        }
//
//        return "redirect:/stocks/" + symbol;
//    }

    @PostMapping("/portfolio/updatePrices")
    public String updateStockPricesManually() {
        portfolioUpdateService.manualUpdateStockPrices();
        return "redirect:/portfolio";
    }

    @GetMapping("/portfolio/delete/{id}")
    public String deletePortfolioItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Deleting item with ID: " + id);
            portfolioService.deletePortfolioItem(id);
            redirectAttributes.addFlashAttribute("successMessage", "Stock sold successfully.");
        } catch (Exception e) {
            System.out.println("Error removing stock: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing stock: " + e.getMessage());
        }
        return "redirect:/portfolio";
    }

    @PostMapping("/portfolio/update")
    public String updatePortfolioItem(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity) {
        portfolioService.updatePortfolioItem(itemId, quantity);
        return "redirect:/portfolio";
    }
}