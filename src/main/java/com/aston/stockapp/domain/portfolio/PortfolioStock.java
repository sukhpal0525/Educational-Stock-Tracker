package com.aston.stockapp.domain.portfolio;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class PortfolioStock {

    @Id
    @Column(name = "ticker")
    private String ticker;

    private String name;

    private double currentPrice;

    private String sector;

    private BigDecimal fiftyTwoWeekHigh;
    private BigDecimal fiftyTwoWeekLow;
    private BigDecimal regularMarketChangePercent;
}