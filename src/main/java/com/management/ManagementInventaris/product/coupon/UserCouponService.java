package com.management.ManagementInventaris.product.coupon;

import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.utils.UserDetailToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class UserCouponService {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserDetailToken userDetailToken;

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public String applyCouponToUser(String couponCode) {
        User user = userDetailToken.dataUserEmail();
        Coupon coupon = couponRepository.findByCode(couponCode)
                .filter(c -> !c.getIsExpired() && c.getIsActive())
                .orElseThrow(() -> new IllegalArgumentException(couponCode + " is not active / expired"));

        UserCoupon existingUserCoupon = userCouponRepository.findByUserAndCoupon(user, coupon);
        if (existingUserCoupon != null) {
            LocalDateTime usedAt = existingUserCoupon.getUsedAt();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyy 'At' HH.mm");
            String formattedUsedAt = usedAt.format(formatter);
            throw new IllegalArgumentException("You have already used this coupon on " + formattedUsedAt);
        }

        if (coupon.getUsesCount() != null && coupon.getMaxUses() != null) {
            if (coupon.getUsesCount() >= coupon.getMaxUses()) {
                coupon.setIsExpired(true);
                coupon.setIsActive(false);
                couponRepository.save(coupon);
                throw new IllegalArgumentException("This coupon has reached its maximum usage limit and is now expired.");
            }
        }

        coupon.setUsesCount(coupon.getUsesCount() == null ? 1 : coupon.getUsesCount() + 1);
        if (coupon.getUsesCount() >= coupon.getMaxUses()) {
            coupon.setIsExpired(true);
            coupon.setIsActive(false);
        }
        couponRepository.save(coupon);

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setId(UUID.randomUUID().toString());
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setUsedAt(LocalDateTime.now());
        userCoupon.setIsActive(true);
        userCouponRepository.save(userCoupon);

        return "User " + user.displayName() + " successfully entered the coupon code correctly!";
    }
}