package com.management.ManagementInventaris.product.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponRequest {

    private String discountType;

    private Integer discountValue;

    private Integer maxUses;

    private String timeUnit;

    private Integer numberOfDays;
}