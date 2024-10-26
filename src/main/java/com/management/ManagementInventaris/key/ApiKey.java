package com.management.ManagementInventaris.key;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @Column(name = "key", nullable = false, updatable = false, unique = true)
    private String key;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "is_expired", nullable = false)
    private Boolean expired;

    public ApiKey(String key, LocalDateTime localDateTime, Boolean expired) {
        this.key = key;
        this.expirationDate = localDateTime;
        this.expired = expired;
    }
}
