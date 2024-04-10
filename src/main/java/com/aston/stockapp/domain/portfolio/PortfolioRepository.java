package com.aston.stockapp.domain.portfolio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByUserId(Long userId);
    Page<Portfolio> findAll(Pageable pageable);

    @Query("SELECT p FROM Portfolio p WHERE EXISTS (SELECT 1 FROM User u WHERE u.id = p.user.id)")
    Page<Portfolio> findAllExisting(Pageable pageable);
}