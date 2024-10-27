package com.management.ManagementInventaris.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(path = "/request-order", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> createOrderUser(@RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = "/remove-product/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeProductFromOrder(@PathVariable String orderId) {
        orderService.removeProductFromOrder(orderId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}