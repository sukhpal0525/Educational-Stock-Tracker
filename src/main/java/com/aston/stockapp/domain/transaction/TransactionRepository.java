package com.aston.stockapp.domain.transaction;

import com.aston.stockapp.domain.portfolio.PortfolioItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    List<Transaction> findByUserIdAndStockTicker(Long userId, String stockTicker);
    Page<Transaction> findByUserIdAndStockTicker(Long userId, String stockTicker, Pageable pageable);
    Page<Transaction> findByUserIdAndTransactionType(Long userId, String transactionType, Pageable pageable);
    Page<Transaction> findByUserIdAndStockTickerAndTransactionType(Long userId, String stockTicker, String transactionType, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.transactionType IN ('Edit', 'Edit (Buy)', 'Edit (Sell)')")
    Page<Transaction> findByUserIdAndEditTypes(@Param("userId") Long userId, Pageable pageable);
}