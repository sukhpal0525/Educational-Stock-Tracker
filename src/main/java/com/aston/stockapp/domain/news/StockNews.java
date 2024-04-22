package com.aston.stockapp.domain.news;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockNews {

    private String title;
    private String description;
    private String link;
    private String pubDate;
}