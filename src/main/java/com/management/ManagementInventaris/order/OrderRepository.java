package com.management.ManagementInventaris.order;

import com.management.ManagementInventaris.product.Product;
import com.management.ManagementInventaris.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT o FROM Order o JOIN FETCH o.product p JOIN FETCH o.cart c WHERE o.user = :user")
    List<Order> findAllByUserWithCartAndProducts(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.deletedByOwner = true WHERE o.product = :product")
    void markOrdersAsDeletedByOwner(@Param("product") Product product);

    @Modifying
    @Transactional
    @Query("DELETE FROM Order o WHERE o.product = :product AND o.deletedByOwner = true")
    void deleteOrdersByProduct(@Param("product") Product product);
}