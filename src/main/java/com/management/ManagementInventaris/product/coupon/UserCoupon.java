package com.management.ManagementInventaris.product.coupon;

import com.management.ManagementInventaris.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_coupon")
public class UserCoupon implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "is_active")
    private Boolean isActive;
}