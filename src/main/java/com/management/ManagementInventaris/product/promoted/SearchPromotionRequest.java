package com.management.ManagementInventaris.product.promoted;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchPromotionRequest {

    private String name;

    private String description;

    private String discountType;

    private BigDecimal discountValue;
}