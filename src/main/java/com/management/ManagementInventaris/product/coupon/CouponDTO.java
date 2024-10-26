package com.management.ManagementInventaris.product.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDTO {

    private String id;

    private String code;

    private String discountType;

    private Integer discountValue;

    private Integer maxUses;

    private Integer usesCount;

    private String startDate;

    private String endDate;

    private Boolean isActive;

    private Boolean isExpired;

    public static CouponDTO fromEntity(Coupon coupon) {
        return CouponDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType().name())
                .discountValue(coupon.getDiscountValue())
                .maxUses(coupon.getMaxUses())
                .usesCount(coupon.getUsesCount())
                .startDate(coupon.getStartDate().toString())
                .endDate(coupon.getEndDate().toString())
                .isActive(coupon.getIsActive())
                .isExpired(coupon.getIsExpired())
                .build();
    }
}