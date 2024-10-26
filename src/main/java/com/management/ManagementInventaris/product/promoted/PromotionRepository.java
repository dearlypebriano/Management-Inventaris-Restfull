package com.management.ManagementInventaris.product.promoted;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String>, JpaSpecificationExecutor<Promotion> {

    List<Promotion> findByEndDateBefore(LocalDateTime now);

    @Query(value = "SELECT * FROM promotions ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Promotion> findAllWithPagination(int offset, int size);
}