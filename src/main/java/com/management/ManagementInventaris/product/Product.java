package com.management.ManagementInventaris.product;

import com.management.ManagementInventaris.categories.Categories;
import com.management.ManagementInventaris.product.variant.Variant;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.utils.DateTimeUtil;
import com.management.ManagementInventaris.utils.Zone;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_title", columnList = "title"),
        @Index(name = "idx_product_price", columnList = "price")
})
public final class Product implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @ElementCollection(targetClass = Unit.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Unit> units;

    @NotBlank(message = "TITLE_BLANK")
    @Size(max = 300, message = "TOO_LONG")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "DESCRIPTION_BLANK")
    @Size(max = 1000, message = "TOO_LONG")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "PRICE_TOO_LOW")
    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "price_range")
    private String priceRange;

    @NotNull(message = "QUANTITY_NULL")
    @Min(value = 0, message = "QUANTITY_TOO_LOW")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    private Integer viewers = 0;

    private String rating = "0.0.0.0.0";

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Variant> variants = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "products_categories",
            joinColumns = @JoinColumn(name = "product_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Categories> categories;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(name = "barcode_url", nullable = true)
    private String barcodeUrl;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    @Column(name = "timezone_label", nullable = false)
    private String timezoneLabel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    public User uploadedBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_ratings",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> ratingUsers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_viewers",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> viewedUsers = new HashSet<>();

    @Version
    private Long version;

    public List<Variant> getVariantsInitialized() {
        Hibernate.initialize(variants);
        return variants;
    }

    @PreRemove
    protected void onRemove() {
        variants.forEach(variant -> variant.setProduct(null));
        variants.clear();
    }

    @PrePersist
    protected void onCreate() {
        String dateTime = DateTimeUtil.getCurrentDateTime(ZoneId.systemDefault());
        ZonedDateTime time = ZonedDateTime.now();
        String zoneId = Zone.getZoneLabel(time);

        this.createdAt = dateTime;
        this.updatedAt = dateTime;
        this.timezoneLabel = zoneId;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = DateTimeUtil.getCurrentDateTime(ZoneId.systemDefault());
    }

    public void updateTimestampsWithZone() {
        this.updatedAt = DateTimeUtil.getCurrentDateTime(ZoneId.systemDefault());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(id, product.id) && Objects.equals(units, product.units) && Objects.equals(title, product.title) && Objects.equals(description, product.description) && Objects.equals(price, product.price) && Objects.equals(priceRange, product.priceRange) && Objects.equals(quantity, product.quantity) && Objects.equals(rating, product.rating) && Objects.equals(variants, product.variants) && Objects.equals(categories, product.categories) && Objects.equals(imageUrls, product.imageUrls) && Objects.equals(barcodeUrl, product.barcodeUrl) && Objects.equals(createdAt, product.createdAt) && Objects.equals(updatedAt, product.updatedAt) && Objects.equals(uploadedBy, product.uploadedBy) && Objects.equals(ratingUsers, product.ratingUsers) && Objects.equals(version, product.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, units, title, description, price, priceRange, quantity, rating, variants, categories, imageUrls, barcodeUrl, createdAt, updatedAt, uploadedBy, ratingUsers, version);
    }
}