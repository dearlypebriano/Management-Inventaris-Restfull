package com.management.ManagementInventaris.product.variant;

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
public class VariantResponse {

    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal price;
}
