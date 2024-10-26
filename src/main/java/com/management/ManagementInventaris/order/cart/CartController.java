package com.management.ManagementInventaris.order.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController{

    @Autowired
    private CartService cartService;

    @GetMapping(path = "/my-cart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CartResponse>> getAllProductsByOrderUser() {
        List<CartResponse> responses = cartService.getOrderProductWithCartFromUser();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}