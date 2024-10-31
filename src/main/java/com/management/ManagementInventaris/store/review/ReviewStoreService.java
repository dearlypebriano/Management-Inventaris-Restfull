package com.management.ManagementInventaris.store.review;

import com.management.ManagementInventaris.utils.Cryptographic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;

@Service
@Slf4j
public class ReviewStoreService {

    @Autowired
    private ReviewStoreRepository reviewStoreRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @CacheEvict(value = "store", allEntries = true)
    @CachePut(value = "store", key = "#result.id")
    public ReviewStoreResponse createReviewStore(ReviewStoreRequest request) {
        ReviewStore reviewStore = new ReviewStore();
        reviewStoreRepository.save(reviewStore);

        ReviewStoreDTO reviewStoreDTO = ReviewStoreDTO.fromEntity(reviewStore);
        redisTemplate.opsForValue().set("store-review:" + reviewStoreDTO.getId(), reviewStoreDTO);
        return toReviewStoreResponse(reviewStore);
    }

    private ReviewStoreResponse toReviewStoreResponse(ReviewStore reviewStore)  {
        String encryptedReviewStoreId;
        try {
            encryptedReviewStoreId = Cryptographic.encrypt(reviewStore.getId());
        } catch (GeneralSecurityException e) {
            log.error("Error Encrypted ID Review Store {}", e.getMessage());
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