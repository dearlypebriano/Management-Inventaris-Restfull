package com.management.ManagementInventaris.product.variant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantInfo {

    private String name;

    private BigDecimal price;

    private String formattedPrice;

    private String note;
}