package com.management.ManagementInventaris.product.coupon;

import com.management.ManagementInventaris.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, String> {

    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user.id = :userId AND uc.isActive = true AND uc.coupon.endDate > CURRENT_TIMESTAMP")
    Optional<UserCoupon> findActiveCouponByUserId(@Param("userId") String userId);

    @Query("DELETE FROM UserCoupon uc WHERE uc.coupon.id = :couponId")
    void deleteByCouponId(@Param("couponId") String couponId);

    UserCoupon findByUserAndCoupon(User user, Coupon coupon);
}