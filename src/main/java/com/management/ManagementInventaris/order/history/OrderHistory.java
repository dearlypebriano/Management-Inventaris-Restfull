package com.management.ManagementInventaris.order.history;

import com.management.ManagementInventaris.order.DeliveryStatus;
import com.management.ManagementInventaris.order.Order;
import com.management.ManagementInventaris.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_history")
public class OrderHistory {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "orderHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @Column(name = "status", nullable = false)
    private String status = DeliveryStatus.PENDING.toString();

    @Column(name = "deleted_by_owner", nullable = false)
    private Boolean deletedByOwner = false;
}