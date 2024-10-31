package com.management.ManagementInventaris.store.review;

import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.utils.DateTimeUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "review_rating", indexes = {
        @Index(name = "idx_review_user", columnList = "review_id, user_id")
})
public class ReviewRating implements Serializable {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewStore reviewStore;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating", nullable = false)
    private int rating; // e.g., 1–5 stars

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = DateTimeUtil.getCurrentDateTime(ZoneId.systemDefault());
    }
}