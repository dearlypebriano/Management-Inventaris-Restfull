package com.management.ManagementInventaris.order.history;

import com.management.ManagementInventaris.order.OrderRepository;
import com.management.ManagementInventaris.order.OrderResponse;
import com.management.ManagementInventaris.order.OrderService;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserConverter;
import com.management.ManagementInventaris.utils.UserDetailToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing order history.
 */
@Service
@Slf4j
public class OrderHistoryService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserDetailToken userDetailToken;

    /**
     * Retrieves the current user's ID from the UserDetailToken.
     *
     * @return the current user's ID
     */
    public String getCurrentUserId() {
        return userDetailToken.getCurrentUserId();
    }

    /**
     * Retrieves the order history for the current user and caches the result.
     *
     * @return the order history response for the current user
     */
    @Cacheable(value = "products", key = "'order-history:' + #root.target.getCurrentUserId()")
    public OrderHistoryResponse getOrderHistory() {
        try {
            User user = userDetailToken.dataUserEmail();
            Optional<OrderHistory> orderHistoryOptional = orderHistoryRepository.findByUser(user);

            if (orderHistoryOptional.isPresent()) {
                OrderHistory orderHistory = orderHistoryOptional.get();
                List<OrderResponse> orderResponses = orderHistory.getOrders().stream()
                        .map(orderService::toOrderResponse)
                        .toList();

                return OrderHistoryResponse.builder()
                        .orderResponses(orderResponses)
                        .user(UserConverter.toUserProfile(user))
                        .build();
            } else {
                return OrderHistoryResponse.builder().build();
            }
        } catch (Exception e) {
            log.error("Error fetching order history: ", e);
            throw new RuntimeException("Failed to fetch order history");
        }
    }
}