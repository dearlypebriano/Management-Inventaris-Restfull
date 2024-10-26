package com.management.ManagementInventaris.product.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponResponse implements Serializable {

    private String id;

    private String code;

    private String discountType;

    private Integer discountValue;

    private String formatDiscountValue;

    private Integer maxUses;

    private Integer usesCount;

    private String startDate;

    private String endDate;

    private Boolean isActive;

    private Boolean isExpired;

    public void formatDiscountValue() {
        if (discountType != null && discountValue != null) {
            switch (discountType.toUpperCase()) {
                case "PERCENTAGE":
                    formatDiscountValue = discountValue + "%";
                    break;
                case "FIXED_AMOUNT":
                    formatDiscountValue = formatCurrency(discountValue);
                    break;
                case "BUY_ONE_GET_ONE":
                    formatDiscountValue = "Buy One Get One";
                    break;
                default:
                    formatDiscountValue = discountValue.toString();
                    break;
            }
        } else {
            formatDiscountValue = "N/A";
        }
    }

    private String formatCurrency(Integer amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(amount);
    }
}