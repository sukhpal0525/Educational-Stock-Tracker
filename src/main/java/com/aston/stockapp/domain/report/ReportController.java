package com.aston.stockapp.domain.report;

import com.aston.stockapp.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ReportController {

    @Autowired private ReportService reportService;

    @GetMapping("/portfolio/generateReport")
    public void generatePortfolioReport(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=portfolio_report.pdf";
        response.setHeader(headerKey, headerValue);

        reportService.generatePortfolioReport(userDetails.getUser().getId(), response);
    }
}