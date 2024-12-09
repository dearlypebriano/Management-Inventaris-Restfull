package com.management.ManagementInventaris.product;

import com.management.ManagementInventaris.categories.Categories;
import com.management.ManagementInventaris.product.variant.Variant;
import com.management.ManagementInventaris.user.User;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private String id;
    private List<Unit> units;
    private String title;
    private String description;
    private BigDecimal price;
    private String priceRange;
    private Integer quantity;
    private Double rating;
    private List<Variant> variants;
    private List<Categories> categories;
    private List<String> imageUrls;
    private String barcodeUrl;
    private String createdAt;
    private String updatedAt;
    private String timezoneLabel;
    private User uploadedBy;

    public static ProductDTO fromEntity(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .units(product.getUnits())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .priceRange(product.getPriceRange())
                .quantity(product.getQuantity())
                .rating(product.getRating())
                .variants(product.getVariantsInitialized())
                .categories(product.getCategories())
                .imageUrls(product.getImageUrls())
                .barcodeUrl(product.getBarcodeUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .timezoneLabel(product.getTimezoneLabel())
                .uploadedBy(product.getUploadedBy())
                .build();
    }
}