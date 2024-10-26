package com.management.ManagementInventaris.product.saved;

import com.management.ManagementInventaris.product.Product;
import com.management.ManagementInventaris.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedProductResponse {

    private User user;
    private Product product;
    private boolean status;
    private Map<String, String> errors;
}