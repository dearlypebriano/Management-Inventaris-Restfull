package com.management.ManagementInventaris.order;

import com.management.ManagementInventaris.order.cart.Cart;
import com.management.ManagementInventaris.order.history.OrderHistory;
import com.management.ManagementInventaris.product.Product;
import com.management.ManagementInventaris.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
@Entity
public class Order {

    @Id
    @Column(name = "order_id", nullable = false, unique = true, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "order_date", nullable = false)
    private String orderDate;

    @Column(name = "status_order", nullable = false)
    private DeliveryStatus status;

    @Column(name = "deleted_by_owner", nullable = false)
    private boolean deletedByOwner = false;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "order_history_id", nullable = false)
    private OrderHistory orderHistory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id) && Objects.equals(user, order.user) && Objects.equals(product, order.product) && Objects.equals(quantity, order.quantity) && Objects.equals(orderDate, order.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, product, quantity, orderDate);
    }
}
