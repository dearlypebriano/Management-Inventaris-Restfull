package com.management.ManagementInventaris.order;

import com.management.ManagementInventaris.product.Product;
import com.management.ManagementInventaris.user.UserConverter;
import com.management.ManagementInventaris.user.UserProfile;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    @NotNull
    private String id;

    private UserProfile user;

    private Product product;

    private Integer quantity;

    private String orderDate;

    public static OrderDTO fromEntity(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .user(UserConverter.toUserProfile(order.getUser()))
                .product(order.getProduct())
                .quantity(order.getQuantity())
                .orderDate(order.getOrderDate())
                .build();
    }
}