package com.management.ManagementInventaris.product.promoted;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionResponse implements Serializable {

    private String id;

    private String name;

    private String description;

    private String discountType;

    private Long discountValue;

    private String formatDiscountValue;

    private List<String> imageUrls;

    private String startDate;

    private String endDate;

    public void formatDiscountValue() {
        if (discountType != null && discountValue != null) {
            switch (discountType.toUpperCase()) {
                case "PERCENTAGE":
                    formatDiscountValue = String.format(Locale.US, "%.2f%%", discountValue.doubleValue());
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

    private String formatCurrency(Long amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(amount);
    }
}