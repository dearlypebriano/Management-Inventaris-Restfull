
package com.management.ManagementInventaris.order;

import com.management.ManagementInventaris.order.cart.Cart;
import com.management.ManagementInventaris.order.cart.CartRepository;
import com.management.ManagementInventaris.order.history.OrderHistory;
import com.management.ManagementInventaris.order.history.OrderHistoryDTO;
import com.management.ManagementInventaris.order.history.OrderHistoryRepository;
import com.management.ManagementInventaris.order.history.OrderHistoryService;
import com.management.ManagementInventaris.product.IProductService;
import com.management.ManagementInventaris.product.Product;
import com.management.ManagementInventaris.product.ProductRepository;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserConverter;
import com.management.ManagementInventaris.user.UserRepository;
import com.management.ManagementInventaris.utils.UserDetailToken;
import com.management.ManagementInventaris.utils.Zone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

import static java.util.stream.Collectors.*;

/**
 * Service implementation for managing orders, including creating, removing, and converting orders.
 */
@Service
@Slf4j
public class OrderService implements IOrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailToken userDetailToken;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Lazy
    @Autowired
    private OrderHistoryService orderHistoryService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "products", allEntries = true)
    @CachePut(value = "products", key = "'orders:' + #result.id")
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        try {
            Product product = productRepository.findByIdWithLock(request.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + request.getProductId() + " not found"));

            if (product.getQuantity() == 0) throw new IllegalStateException("Product with ID " + request.getProductId() + " is not available");

            User user = userDetailToken.dataUserEmail();

            if (product.getUploadedBy().getEmail().equals(user.getEmail())) throw new IllegalTransactionStateException("This product : " + product.getTitle() + " belongs to you and you may not buy your own product");

            String location = String.format("%s, %s, %s, %s",
                    user.getProvince().getName(),
                    user.getRegency().getName(),
                    user.getDistrict().getName(),
                    user.getVillage().getName());

            location = Arrays.stream(location.split(", "))
                    .map(word -> {
                        if (word.equals(word.toUpperCase())) {
                            return word.charAt(0) + word.substring(1).toLowerCase();
                        } else {
                            return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                        }
                    })
                    .collect(joining(", "));

            ZonedDateTime dateTime = ZonedDateTime.now();
            String zoneId = dateTime + " " + Zone.getZoneLabel(dateTime);

            String finalLocation = location;
            Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setId(UUID.randomUUID().toString());
                newCart.setDeliveryAddress(finalLocation);
                newCart.setUser(user);
                return newCart;
            });

            OrderHistory orderHistory = orderHistoryRepository.findByUser(user).orElseGet(() -> {
                OrderHistory history = new OrderHistory();
                history.setId(UUID.randomUUID().toString());
                history.setUser(user);
                orderHistoryRepository.save(history);

                OrderHistoryDTO dto = OrderHistoryDTO.fromEntity(history);
                redisTemplate.opsForValue().set("order-history:" + dto.getId(), dto);

                return history;
            });

            Order order = new Order();
            order.setId(UUID.randomUUID().toString());
            order.setUser(user);
            order.setProduct(product);
            order.setOrderDate(zoneId);
            if (request.getQuantity() < 0) throw new IllegalArgumentException("Requested quantity is negative");
            order.setQuantity(request.getQuantity());
            order.setStatus(DeliveryStatus.PENDING);
            order.setCart(cart);
            order.setOrderHistory(orderHistory);

            orderHistory.setStatus(order.getStatus().toString());

            cart.getOrders().add(order);
            orderHistory.getOrders().add(order);

            cartRepository.save(cart);
            orderRepository.save(order);
            orderHistoryRepository.save(orderHistory);

            OrderDTO orderDTO = OrderDTO.fromEntity(order);
            redisTemplate.opsForValue().set("order:" + orderDTO.getId(), orderDTO);

            OrderHistoryDTO historyDTO = OrderHistoryDTO.fromEntity(orderHistory);
            redisTemplate.opsForValue().set("order-history:" + historyDTO.getId(), historyDTO);

            return toOrderResponse(order);
        } catch (Exception e) {
            log.error("Error creating order: ", e);
            throw new RuntimeException("Failed to create order");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "products", key = "'orders' + #orderId", allEntries = true)
    @Transactional
    public void removeProductFromOrder(String orderId) {
        User user = userDetailToken.dataUserEmail();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with ID : " + orderId + " not found"));

        if (!order.getUser().getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User with ID : " + user.getId() + " is not a member of this order");

        Cart cart = order.getCart();
        if (cart == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart associated with this order not found");

        cart.getOrders().remove(order);
        cartRepository.save(cart);
        orderRepository.delete(order);

        String redisKey = "order:" + orderId;
        Boolean isDeleted = redisTemplate.delete(redisKey);
        if (Boolean.FALSE.equals(isDeleted)) {
            log.warn("Order dengan ID: {} tidak ditemukan di Redis atau gagal dihapus.", orderId);
        } else {
            log.info("Order dengan ID: {} berhasil dihapus dari Redis.", orderId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .quantity(order.getQuantity())
                .product(iProductService.toProductResponse(order.getProduct()))
                .user(UserConverter.toUserProfile(order.getUser()))
                .deletedByOwner(order.isDeletedByOwner())
                .build();
    }
}