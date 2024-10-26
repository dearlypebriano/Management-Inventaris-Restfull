package com.management.ManagementInventaris.product.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

    @Query("SELECT c FROM Coupon c WHERE c.usesCount >= c.maxUses AND c.isActive = true")
    List<Coupon> findByMaxUsesReached();

    @Query(value = "SELECT * FROM coupons ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Coupon> findAllWithPagination(int offset, int size);

    List<Coupon> findByEndDateBefore(LocalDateTime now);

    Optional<Coupon> findByCode(String code);
}