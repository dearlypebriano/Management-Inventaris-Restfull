package com.management.ManagementInventaris.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.management.ManagementInventaris.categories.Categories;
import com.management.ManagementInventaris.product.variant.VariantInfo;
import com.management.ManagementInventaris.user.UserProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse implements Serializable {

    @NotNull
    private String id;

    private List<String> units;

    @NotNull
    private String title;

    @NotNull
    private String description;

    private String priceRange;

    private BigDecimal price;

    private String formattedPrice;

    @NotBlank
    private Integer quantity;

    private Integer viewers;

    private String rating;

    private List<VariantInfo> variants;

    private List<String> categories;

    private List<String> imageUrls;

    private String barcodeProduct;

    private String shareLink;

    private String createdAt;

    private String updatedAt;

    private String timezoneLabel;

    private UserProfile uploadedBy;

    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setTitle(this.title);
        product.setRating(this.rating);
        product.setQuantity(this.quantity);
        product.setDescription(this.description);
        List<Categories> categoriesList = this.categories.stream().map(categoryName -> {
            Categories category = new Categories();
            category.setCategoryName(categoryName);
            return category;
        }).collect(Collectors.toList());
        product.setCategories(categoriesList);
        product.setImageUrls(this.imageUrls);
        product.setCreatedAt(product.getCreatedAt());
        product.setUpdatedAt(product.getUpdatedAt());
        product.setUploadedBy(this.toEntity().uploadedBy);
        return product;
    }
}