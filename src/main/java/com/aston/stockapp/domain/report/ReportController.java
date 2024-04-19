package com.aston.stockapp.domain.report;

import com.aston.stockapp.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ReportController {

    @Autowired private ReportService reportService;

    @GetMapping("/portfolio/generateReport")
    public void generatePortfolioReport(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        String date = dateFormat.format(new Date());

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=Portfolio_Report_%s.pdf", date);
        response.setHeader(headerKey, headerValue);

        reportService.generatePortfolioReport(userDetails.getUser().getId(), response);
    }
}