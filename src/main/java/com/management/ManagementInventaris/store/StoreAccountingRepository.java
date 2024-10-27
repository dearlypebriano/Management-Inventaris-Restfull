package com.management.ManagementInventaris.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreAccountingRepository extends JpaRepository<StoreAccounting, String> {

    @Query(value = "SELECT * FROM store_accounting ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<StoreAccounting> findAllWithPagination(int offset, int size);

    Optional<StoreAccounting> findByStore(Store store);
}