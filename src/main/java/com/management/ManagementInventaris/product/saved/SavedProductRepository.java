package com.management.ManagementInventaris.product.saved;

import com.management.ManagementInventaris.product.SavedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedProductRepository extends JpaRepository<SavedProduct, String> {

    List<SavedProduct> findByUserId(String userId);

    Optional<SavedProduct> findByProductId(String productId);
}