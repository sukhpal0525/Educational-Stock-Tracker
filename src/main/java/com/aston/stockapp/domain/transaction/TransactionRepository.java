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
    Page<Transaction> findByUserIdAndTransactionType(Long userId, String transactionType, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.transactionType IN ('Edit', 'Edit (Buy)', 'Edit (Sell)')")
    Page<Transaction> findByUserIdAndEditTypes(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE EXISTS (SELECT 1 FROM User u WHERE u.id = t.user.id)")
    Page<Transaction> findAllExisting(Pageable pageable);

    // For admin functionality
    Page<Transaction> findByStockTicker(String stockTicker, Pageable pageable);
    Page<Transaction> findByUserIdAndStockTicker(Long userId, String stockTicker, Pageable pageable);
    Page<Transaction> findByUserIdAndStockTickerAndTransactionType(Long userId, String stockTicker, String transactionType, Pageable pageable);
    // For filtering by user id and transaction type
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.transactionType = :transactionType")
    Page<Transaction> findByUserIdAndTransactionTypeFiltered(@Param("userId") Long userId, @Param("transactionType") String transactionType, Pageable pageable);

    // For filtering by stock ticker and transaction type
    @Query("SELECT t FROM Transaction t WHERE t.stockTicker = :stockTicker AND t.transactionType = :transactionType")
    Page<Transaction> findByStockTickerAndTransactionType(@Param("stockTicker") String stockTicker, @Param("transactionType") String transactionType, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:ticker IS NULL OR t.stockTicker = :ticker) AND " +
            "(:userId IS NULL OR (t.user.id = :userId AND t.user IS NOT NULL)) AND " +
            "(:transactionType IS NULL OR t.transactionType = :transactionType) AND " +
            "EXISTS (SELECT u FROM User u WHERE u.id = t.user.id)")
    Page<Transaction> findByFilters(@Param("ticker") String ticker,
                                    @Param("userId") Long userId,
                                    @Param("transactionType") String transactionType,
                                    Pageable pageable);

}