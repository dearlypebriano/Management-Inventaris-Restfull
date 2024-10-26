package com.management.ManagementInventaris.order.cart;

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
@Table(name = "carts")
@Entity
public class Cart {

    @Id
    @Column(name = "cart_id", nullable = false, unique = true, updatable = false)
    private String id;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress = "Indonesia";

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartProductNote> cartProductNotes = new ArrayList<>();
}