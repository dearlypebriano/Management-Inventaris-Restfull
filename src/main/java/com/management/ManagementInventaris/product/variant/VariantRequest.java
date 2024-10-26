package com.management.ManagementInventaris.product.variant;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantRequest {

    @NotNull
    private String name;

    @NotNull
    private BigDecimal price;
}
