package com.management.ManagementInventaris.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProductRequest {

    private String title;

    private String description;

    private String category;

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;
}