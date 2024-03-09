package com.aston.stockapp.domain.portfolio;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class PortfolioStock {

    @Id
    @Column(name = "ticker")
    private String ticker;

    private String name;

    private double currentPrice;
}
