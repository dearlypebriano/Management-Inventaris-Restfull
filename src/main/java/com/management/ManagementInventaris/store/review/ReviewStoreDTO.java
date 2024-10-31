package com.management.ManagementInventaris.store.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewStoreDTO {
    private String id;

    private Integer rating;

    private String comment;

    private String user;

    private String store;

    public static ReviewStoreDTO fromEntity(ReviewStore reviewStore) {
        return ReviewStoreDTO.builder()
                .id(reviewStore.getId())
                .rating(reviewStore.getRating())
                .comment(reviewStore.getMessage())
                .user(reviewStore.getUser().displayName())
                .store(reviewStore.getStore().getStoreName())
                .build();
    }
}