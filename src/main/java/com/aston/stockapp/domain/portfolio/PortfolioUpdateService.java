package com.aston.stockapp.domain.portfolio;

import com.aston.stockapp.api.YahooFinanceService;
import com.aston.stockapp.api.YahooStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PortfolioUpdateService {

    @Autowired private PortfolioStockRepository portfolioStockRepository;
    @Autowired private YahooFinanceService yahooFinanceService;

    // 86400000ms = 1 Day
    @Scheduled(fixedDelay = 86400000)
    @Transactional
    public void updateStockPrices() {
        List<PortfolioStock> allStocks = portfolioStockRepository.findAll();
        for (PortfolioStock stock : allStocks) {
            YahooStock yahooStock = yahooFinanceService.fetchStockData(stock.getTicker());
            stock.setCurrentPrice(yahooStock.getPrice().doubleValue());
            portfolioStockRepository.save(stock);
        }
    }

    public void manualUpdateStockPrices() {
        List<PortfolioStock> allStocks = portfolioStockRepository.findAll();
        for (PortfolioStock stock : allStocks) {
            YahooStock yahooStock = yahooFinanceService.fetchStockData(stock.getTicker());
            stock.setCurrentPrice(yahooStock.getPrice().doubleValue());
            portfolioStockRepository.save(stock);
        }
    }
}
