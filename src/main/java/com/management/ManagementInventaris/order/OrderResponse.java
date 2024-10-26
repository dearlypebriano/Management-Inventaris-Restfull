package com.management.ManagementInventaris.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.management.ManagementInventaris.product.Product;
import com.management.ManagementInventaris.product.ProductResponse;
import com.management.ManagementInventaris.user.UserProfile;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private String id;

    private UserProfile user;

    private ProductResponse product;

    private Integer quantity;

    private String orderDate;

    private Boolean deletedByOwner;
}