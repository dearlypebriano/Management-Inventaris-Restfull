package com.management.ManagementInventaris.order.cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.management.ManagementInventaris.order.OrderResponse;
import com.management.ManagementInventaris.user.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartResponse implements Serializable {

    private String id;

    private UserProfile user;

    private BigDecimal totalPrice;

    private String formattedTotalPrice;

    private String deliveryDate;

    private String deliveryAddress;

    private List<ProductNoteResponse> deliveryNote;

    private List<OrderResponse> orders;
}