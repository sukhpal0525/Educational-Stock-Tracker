package com.aston.stockapp.domain.learn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningContentRepository extends JpaRepository<LearningContent, Long> {
    List<LearningContent> findByCategory(String category);
}
