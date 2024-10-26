package com.management.ManagementInventaris.categories;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriesRequest {

    @NotNull
    private String categoryName;

    @NotNull
    private String description;
}