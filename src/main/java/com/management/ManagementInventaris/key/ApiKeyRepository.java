package com.management.ManagementInventaris.key;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, String> {

    Optional<ApiKey> findByKey(String key);

    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN TRUE ELSE FALSE END FROM ApiKey k WHERE k.key = :key")
    boolean existsByKey(String key);

    @Query("SELECT k FROM ApiKey k WHERE k.expirationDate > :now AND k.expired = false ORDER BY k.expirationDate DESC")
    ApiKey findLatestValidKey(@Param("now") LocalDateTime now);

    List<ApiKey> findAllByExpirationDateBefore(LocalDateTime dateTime);

    void deleteAllByExpiredTrue();
}