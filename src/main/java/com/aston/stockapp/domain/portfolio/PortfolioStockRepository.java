package com.aston.stockapp.domain.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioStockRepository extends JpaRepository<PortfolioStock, String> {

    Optional<PortfolioStock> findByTicker(String ticker);
    Optional<PortfolioStock> findByNameContainingIgnoreCase(String name);
}