package com.management.ManagementInventaris.store.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewStoreRepository extends JpaRepository<ReviewStore, String> {
}