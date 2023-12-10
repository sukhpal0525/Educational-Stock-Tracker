package com.aston.stockapp.domain.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioStockRepository extends JpaRepository<PortfolioStock, String> {

    Optional<PortfolioStock> findByTicker(String ticker);
}
