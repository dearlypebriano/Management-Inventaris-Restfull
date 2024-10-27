package com.management.ManagementInventaris.order.cart;

import com.management.ManagementInventaris.exception.CartException;
import com.management.ManagementInventaris.order.IOrderService;
import com.management.ManagementInventaris.order.Order;
import com.management.ManagementInventaris.order.OrderRepository;
import com.management.ManagementInventaris.order.OrderResponse;
import com.management.ManagementInventaris.product.IProductService;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserConverter;
import com.management.ManagementInventaris.utils.CurrencyFormatter;
import com.management.ManagementInventaris.utils.UserDetailToken;
import com.management.ManagementInventaris.utils.Zone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing the shopping cart functionality.
 */
@Service
@Slf4j
public class CartService implements ICartService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserDetailToken userDetailToken;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IOrderService iOrderService;

    public String getCurrentUserId() {
        return userDetailToken.getCurrentUserId();
    }

    /**
     * Retrieves the order products with the cart details for the current authenticated user.
     *
     * @return A list of CartResponse objects containing the order products, user details, and cart details.
     * @throws CartException If no orders are found for the authenticated user.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'orders:' + 'user:' + #root.target.getCurrentUserId()")
    public List<CartResponse> getOrderProductWithCartFromUser() {
        try {
            User user = userDetailToken.dataUserEmail();
            List<Order> orders = orderRepository.findAllByUserWithCartAndProducts(user);

            if (orders.isEmpty()) {
                throw new CartException("No orders found for the authenticated user", "No orders were found in the cart.");
            }

            return orders.stream()
                    .map(order -> toCartResponse(order.getCart()))
                    .distinct()
                    .collect(Collectors.toList());
        } catch (CartException e) {
            log.error("Cart error: {} - Details: {}", e.getMessage(), e.getDetails());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving orders: {}", e.getMessage());
            throw new CartException("An unexpected error occurred while retrieving orders", e.getMessage());
        }
    }

    @Override
    public CartResponse toCartResponse(Cart cart) {
        BigDecimal totalPrice = cart.getOrders().stream()
                .map(order -> order.getProduct().getPrice().multiply(BigDecimal.valueOf(order.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ZonedDateTime dateTime = ZonedDateTime.now();
        String zoneId = dateTime + " " + Zone.getZoneLabel(dateTime);

        List<OrderResponse> orderResponses = cart.getOrders().stream()
                .map(order -> iOrderService.toOrderResponse(order))
                .collect(Collectors.toList());

        List<ProductNoteResponse> productNotes = cart.getCartProductNotes().stream()
                .map(cartProductNote -> new ProductNoteResponse(
                        cartProductNote.getProduct().getId(),
                        cartProductNote.getProduct().getTitle(),
                        cartProductNote.getNote()
                )).collect(Collectors.toList());

        return CartResponse.builder()
                .id(cart.getId())
                .user(UserConverter.toUserProfile(cart.getUser()))
                .orders(orderResponses)
                .totalPrice(totalPrice)
                .formattedTotalPrice(CurrencyFormatter.formatIDR(totalPrice))
                .deliveryDate(zoneId)
                .deliveryAddress(cart.getDeliveryAddress())
                .deliveryNote(productNotes)
                .build();
    }
}