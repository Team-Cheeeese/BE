package com.cheeeese.cheese4cut.infrastructure.persistence;

import com.cheeeese.cheese4cut.domain.Cheese4cutAiSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Cheese4cutAiSummaryRepository extends JpaRepository<Cheese4cutAiSummary, Long> {

    Optional<Cheese4cutAiSummary> findByCheese4cutId(Long cheese4cutId);
}
