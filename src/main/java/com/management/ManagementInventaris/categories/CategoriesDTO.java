package com.management.ManagementInventaris.categories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriesDTO {

    private String id;
    private String categoryName;
    private String description;
    private String imageUrl;
    private Boolean isConstant;

    public static CategoriesDTO fromEntity(Categories categories) {
        return CategoriesDTO.builder()
                .id(categories.getId())
                .categoryName(categories.getCategoryName())
                .description(categories.getDescription())
                .imageUrl(categories.getImageUrl())
                .isConstant(categories.getIsConstant())
                .build();
    }
}