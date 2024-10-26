package com.management.ManagementInventaris.order.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductNoteResponse {
    private String productId;
    private String productName;
    private String note;
}
