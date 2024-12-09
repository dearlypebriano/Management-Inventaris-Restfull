package com.management.ManagementInventaris.store.review;

import com.management.ManagementInventaris.store.Store;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.utils.DateTimeUtil;
import com.management.ManagementInventaris.validations.NoToxic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "review_store")
public final class ReviewStore implements Serializable {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating")
    private Double rating = 0.0;

    @NoToxic(message = "Your review contains inappropriate language!")
    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "created_at")
    private String createdAt;

    @OneToMany(mappedBy = "reviewStore", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewRating> reviewRatings = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        this.createdAt = DateTimeUtil.getCurrentDateTime(ZoneId.systemDefault());
    }
}