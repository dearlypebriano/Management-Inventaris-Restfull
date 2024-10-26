package com.management.ManagementInventaris.order.cart;

import com.management.ManagementInventaris.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cart_product_notes")
@Entity
public class CartProductNote {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "note")
    private String note;
}
