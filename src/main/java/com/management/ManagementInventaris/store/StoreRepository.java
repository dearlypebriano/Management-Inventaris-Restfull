package com.management.ManagementInventaris.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {

    @Query(value = "SELECT * FROM store ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Store> findAllWithPagination(int offset, int size);

    Optional<Store> findByUserEmail(String email);
}