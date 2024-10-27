package com.management.ManagementInventaris.order.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order-history")
public class OrderHistoryController {

    @Autowired
    private OrderHistoryService orderHistoryService;

    @GetMapping(path = "/history-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderHistoryResponse> getOrderHistory() {
        OrderHistoryResponse orderHistoryResponse = orderHistoryService.getOrderHistory();
        return ResponseEntity.status(HttpStatus.OK).body(orderHistoryResponse);
    }
}