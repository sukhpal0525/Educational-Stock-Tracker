package com.aston.stockapp.domain.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioStockRepository extends JpaRepository<PortfolioStock, String> {

    @Query("SELECT DISTINCT ps.ticker FROM PortfolioStock ps")
    List<String> findUniqueTickers();

    Optional<PortfolioStock> findByTicker(String ticker);
}