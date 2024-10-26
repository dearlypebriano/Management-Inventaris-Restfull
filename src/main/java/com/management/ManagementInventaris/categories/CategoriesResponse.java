package com.management.ManagementInventaris.categories;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoriesResponse implements Serializable {

    private String id;
    private String categoryName;
    private String description;
    private String imageUrl;
    private Boolean isConstant;
}