package com.management.ManagementInventaris.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, String>, JpaSpecificationExecutor<Categories> {

    Categories findByCategoryName(String categoryName);

    @Query("SELECT c FROM Categories c WHERE LOWER(c.categoryName) LIKE LOWER(concat('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(concat('%', :keyword, '%'))")
    List<Categories> findByCategoryNameOrDescriptionContainingIgnoreCase(String keyword);

    @Query(value = "SELECT * FROM categories ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Categories> findAllWithPagination(int offset, int size);
}