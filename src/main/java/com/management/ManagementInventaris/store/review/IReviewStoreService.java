package com.management.ManagementInventaris.store.review;

import com.management.ManagementInventaris.utils.Cryptographic;

import java.security.GeneralSecurityException;
import java.util.List;

public interface IReviewStoreService {

    ReviewStoreResponse createReviewStore(ReviewStoreRequest request);

    ReviewStoreResponse updateReviewStore(ReviewStoreRequest request, String reviewStoreId);

    void deleteReviewStore(String reviewStoreId);

    void deleteReviewStoreWithSales(String reviewStoreId);

    List<ReviewStoreResponse> getAllReviewStoreById(String storeId);

    default ReviewStoreResponse toReviewStoreResponse(ReviewStore reviewStore)  {
        String encryptedReviewStoreId;
        try {
            encryptedReviewStoreId = Cryptographic.encrypt(reviewStore.getId());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        return ReviewStoreResponse.builder()
                .id(encryptedReviewStoreId)
                .username(reviewStore.getUser().getUsernameUser())
                .message(reviewStore.getMessage())
                .rating(reviewStore.getRating())
                .storeName(reviewStore.getStore().getStoreName())
                .createdAt(reviewStore.getCreatedAt())
                .build();
    }
}