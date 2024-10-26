package com.management.ManagementInventaris.product.promoted;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionDTO {

    private String id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    private String discountType;

    @NotNull
    private Long discountValue;

    private String startDate;

    private String endDate;

    public static PromotionDTO fromEntity(Promotion promotion) {
        return PromotionDTO.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType().name())
                .discountValue(promotion.getDiscountValue())
                .startDate(promotion.getStartDate().toString())
                .endDate(promotion.getEndDate().toString())
                .build();
    }
}