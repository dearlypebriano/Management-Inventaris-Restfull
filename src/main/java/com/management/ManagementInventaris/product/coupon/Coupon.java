package com.management.ManagementInventaris.product.coupon;

import com.management.ManagementInventaris.product.promoted.DiscountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "coupons")
public final class Coupon implements Serializable, Comparable<Coupon> {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @Column(nullable = false, updatable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value")
    private Integer discountValue;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "uses_count")
    private Integer usesCount;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_expired")
    private Boolean isExpired = false;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserCoupon> userCoupons;

    @Override
    public int compareTo(@NotNull Coupon cp) {
        return this.startDate.compareTo(cp.startDate);
    }
}