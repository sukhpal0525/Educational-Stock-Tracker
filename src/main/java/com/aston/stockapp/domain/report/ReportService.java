package com.aston.stockapp.domain.report;

import com.aston.stockapp.domain.portfolio.Portfolio;
import com.aston.stockapp.domain.portfolio.PortfolioItem;
import com.aston.stockapp.domain.portfolio.PortfolioService;
import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
public class ReportService {

    private final PortfolioService portfolioService;

    public ReportService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    public void generatePortfolioReport(Long userId, HttpServletResponse response) {
        Portfolio portfolio = portfolioService.getPortfolio(userId);
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            document.add(new Paragraph("Portfolio Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph(" "));

            // Portfolio Summary
            document.add(new Paragraph("Portfolio Summary", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("Total Cost: $" + formatBigDecimal(portfolio.getTotalCost())));
            document.add(new Paragraph("Total Value: $" + formatBigDecimal(portfolio.getTotalValue())));
            document.add(new Paragraph("Total Change: " + formatBigDecimal(portfolio.getTotalChangePercent()) + "%"));
            document.add(new Paragraph(" "));

            // Table for Stock Details
            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Font for table content
            Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE);
            Font greenFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, new BaseColor(0, 128, 0));
            Font redFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.RED);

            // Define column headers
            String[] columnHeaders = { "Name", "Ticker", "Quantity", "Purchased At", "Cost $", "Value $", "Change $", "Change %", "Allocation %" };
            PdfPCell headerCell;
            for (String header : columnHeaders) {
                headerCell = new PdfPCell(new Phrase(header, tableHeaderFont));
                headerCell.setBackgroundColor(WebColors.getRGBColor("#4682b4"));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerCell.setPaddingTop(4);
                headerCell.setPaddingBottom(4);
                table.addCell(headerCell);
            }

            // Adding stock details to the table
            BigDecimal totalPortfolioValue = BigDecimal.valueOf(portfolio.getTotalValue());
            for (PortfolioItem item : portfolio.getItems()) {
                BigDecimal itemCost = new BigDecimal(item.getPurchasePrice() * item.getQuantity()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal itemValue = new BigDecimal(item.getStock().getCurrentPrice() * item.getQuantity()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal changeDollar = itemValue.subtract(itemCost).setScale(2, RoundingMode.HALF_UP);
                BigDecimal changePercent = itemCost.compareTo(BigDecimal.ZERO) > 0 ? changeDollar.divide(itemCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                BigDecimal allocationPercent = totalPortfolioValue.compareTo(BigDecimal.ZERO) > 0 ? itemValue.divide(totalPortfolioValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

                Font colorFont = changeDollar.compareTo(BigDecimal.ZERO) >= 0 ? greenFont : redFont;


                // Adding data cells
                addTableDataCell(table, item.getStock().getName(), tableFont);
                addTableDataCell(table, item.getStock().getTicker(), tableFont);
                addTableDataCell(table, String.valueOf(item.getQuantity()), tableFont);
                addTableDataCell(table, "$" + item.getPurchasePrice(), tableFont);
                addTableDataCell(table, itemCost.toPlainString(), tableFont);
                addTableDataCell(table, itemValue.toPlainString(), tableFont);
                addTableDataCell(table, changeDollar.toPlainString(), colorFont);
                addTableDataCell(table, changePercent.setScale(2, RoundingMode.HALF_UP) + "%", colorFont);
                addTableDataCell(table, allocationPercent.setScale(2, RoundingMode.HALF_UP) + "%", tableFont);
            }
            document.add(table);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private void addTableDataCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private String formatBigDecimal(double value) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(value);
    }
}