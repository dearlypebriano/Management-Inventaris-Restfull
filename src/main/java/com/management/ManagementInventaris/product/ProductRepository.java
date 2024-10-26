package com.management.ManagementInventaris.product;

import com.management.ManagementInventaris.product.variant.Variant;
import com.management.ManagementInventaris.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") String id);

    /**
     * This method is used to retrieve a paginated list of products.
     * It uses a native SQL query to perform the pagination.
     *
     * @param offset The offset for pagination. It represents the starting index of the products to retrieve.
     * @param size The size of the page. It represents the number of products to retrieve per page.
     * @return A list of products retrieved based on the provided offset and size.
     */
    @Query(value = "SELECT * FROM products ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Product> findAllWithPagination(int offset, int size);

    /**
     * This method is used to search for products based on a specific category name.
     * It performs a join between the Product and Category tables and looks for matching category names.
     *
     * @param categoryName The name of the category to search for.
     * @return A list of products that fall into a specific category.
     */
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE LOWER(c.categoryName) LIKE LOWER(concat('%', :categoryName, '%'))")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    /**
     * This method is used to search for products with quantity less than the specified value.
     *
     * @param qty The maximum quantity to search for.
     * @return List of products whose quantity is less than the specified value.
     */
    List<Product> findByQuantityLessThan(int qty);

    /**
     * This method is used to find products uploaded by a specific user.
     *
     * @param uploadedBy The user who uploaded the products.
     * @return A list of products uploaded by the specified user.
     */
    @Query("SELECT p FROM Product p WHERE p.uploadedBy = :user")
    List<Product> findByUploadedBy(@Param("user") User uploadedBy);

    /**
     * This method is used to find products with a price less than or equal to a specified maximum price.
     *
     * @param maxPrice The maximum price to search for.
     * @return A list of products with a price less than or equal to the specified maximum price.
     */
    @Query("SELECT p FROM Product p WHERE p.price <= :maxPrice")
    List<Product> findProductsByPriceLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);

    /**
     * This method is used to find variants with a price less than or equal to a specified maximum price.
     *
     * @param maxPrice The maximum price to search for.
     * @return A list of variants with a price less than or equal to the specified maximum price.
     */
    @Query("SELECT v FROM Variant v WHERE v.price <= :maxPrice")
    List<Variant> findVariantsByPriceLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);
}
