package com.management.ManagementInventaris.order.cart;

import com.management.ManagementInventaris.product.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for managing the shopping cart functionality.
 */
public interface ICartService {

    /**
     * Retrieves the order products with the cart details for the current authenticated user.
     *
     * @return A list of CartResponse objects containing the order products, user details, and cart details.
     */
    List<CartResponse> getOrderProductWithCartFromUser();

    /**
     * Converts the cart object to a cart response object.
     *
     * @param cart The cart object to be converted.
     * @return The cart response object containing the order products, user details, total price, formatted total price,
     * delivery date, delivery address, and delivery note.
     */
    CartResponse toCartResponse(Cart cart);

    /**
     * Calculates the total price of the products in the cart.
     *
     * @param products The list of products in the cart.
     * @return The total price of the products.
     */
    default BigDecimal calculateTotalPrice(List<Product> products) {
        return products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}