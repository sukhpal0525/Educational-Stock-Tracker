package com.aston.stockapp.domain.news;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewsResponse {
    private List<StockNews> item;
}