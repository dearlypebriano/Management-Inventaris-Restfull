package com.management.ManagementInventaris.order;

/**
 * Interface for Order Service which provides methods for creating, removing, and converting orders.
 */
public interface IOrderService {

    /**
     * Creates a new order based on the provided OrderRequest.
     *
     * @param request the OrderRequest containing order details
     * @return the created OrderResponse
     */
    OrderResponse createOrder(OrderRequest request);

    /**
     * Removes a product from an order by its orderId.
     *
     * @param orderId the ID of the order from which the product should be removed
     */
    void removeProductFromOrder(String orderId);

    /**
     * Converts an Order entity to an OrderResponse.
     *
     * @param order the Order entity to convert
     * @return the converted OrderResponse
     */
    OrderResponse toOrderResponse(Order order);
}