package com.management.ManagementInventaris.order.history;

import com.management.ManagementInventaris.order.Order;
import com.management.ManagementInventaris.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, String> {

    List<OrderHistory> findAllByUser(User user);

    Optional<OrderHistory> findByUser(User user);

    @Query("SELECT oh FROM OrderHistory oh JOIN oh.orders o WHERE o.product.id = :productId")
    List<OrderHistory> findByProductId(@Param("productId") String productId);

    @Modifying
    @Transactional
    @Query("UPDATE OrderHistory oh SET oh.deletedByOwner = true WHERE oh.id IN (SELECT o.orderHistory.id FROM Order o WHERE o.product.id = :productId)")
    void markOrderHistoriesAsDeletedByOwner(@Param("productId") String productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderHistory oh WHERE oh.id IN (SELECT o.orderHistory.id FROM Order o WHERE o.product.id = :productId AND o.orderHistory.deletedByOwner = true)")
    void deleteOrderHistoriesByProduct(@Param("productId") String
                                               productId);
}