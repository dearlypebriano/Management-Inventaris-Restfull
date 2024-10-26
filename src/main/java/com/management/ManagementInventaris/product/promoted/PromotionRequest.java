package com.management.ManagementInventaris.product.promoted;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionRequest {

    @NotNull
    private String name;

    @NotNull
    private String description;

    private String discountType;

    private Long discountValue;

    private Long numberOfDays;
}