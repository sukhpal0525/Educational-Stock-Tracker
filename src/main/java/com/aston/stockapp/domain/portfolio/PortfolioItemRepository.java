package com.aston.stockapp.domain.portfolio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    Optional<PortfolioItem> findById(Long id);
    Page<PortfolioItem> findByPortfolioUserId(Long userId, Pageable pageable);
}
