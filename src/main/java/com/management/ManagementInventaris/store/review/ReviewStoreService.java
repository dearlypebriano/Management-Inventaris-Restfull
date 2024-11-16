package com.management.ManagementInventaris.store.review;

import com.management.ManagementInventaris.store.Store;
import com.management.ManagementInventaris.store.StoreRepository;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.utils.UserDetailToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ReviewStoreService implements IReviewStoreService {

    @Autowired
    private ReviewStoreRepository reviewStoreRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserDetailToken userDetailToken;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @Override
    @CacheEvict(value = "store", allEntries = true)
    @CachePut(value = "store", key = "#result.id")
    public ReviewStoreResponse createReviewStore(ReviewStoreRequest request) {
        User userComment = userDetailToken.dataUserEmail();
        Store storeOpt = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + request.getStoreId()));

        ReviewStore reviewStore = new ReviewStore();
        reviewStore.setId(UUID.randomUUID().toString());
        reviewStore.setStore(storeOpt);
        reviewStore.setUser(userComment);
        reviewStore.setRating(request.getRating());
        reviewStore.setMessage(request.getMessage());
        reviewStoreRepository.save(reviewStore);

        ReviewStoreDTO reviewStoreDTO = ReviewStoreDTO.fromEntity(reviewStore);
        redisTemplate.opsForValue().set("store-review:" + reviewStoreDTO.getId(), reviewStoreDTO);
        return toReviewStoreResponse(reviewStore);
    }

    @Transactional
    @Override
    @CacheEvict(value = "store", allEntries = true)
    @CachePut(value = "store", key = "#result.id")
    public ReviewStoreResponse updateReviewStore(ReviewStoreRequest request, String reviewStoreId) {
        User user = userDetailToken.dataUserEmail();
        ReviewStore reviewStore = reviewStoreRepository.findById(reviewStoreId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ReviewStore not found"));

        if (!reviewStore.getUser().getEmail().equals(user.getEmail())) throw new IllegalStateException("This is not your data. You cannot change this data!");

        if (request.getMessage() != null) reviewStore.setMessage(request.getMessage());
        reviewStoreRepository.save(reviewStore);

        ReviewStoreDTO reviewStoreDTO = ReviewStoreDTO.fromEntity(reviewStore);
        redisTemplate.opsForValue().set("store-review:" + reviewStoreDTO.getId(), reviewStoreDTO);
        return toReviewStoreResponse(reviewStore);
    }

    @Transactional(readOnly = true)
    @Override
    @CacheEvict(value = "store", allEntries = true)
    public void deleteReviewStore(String reviewStoreId) {
        User user = userDetailToken.dataUserEmail();
        ReviewStore reviewStore = reviewStoreRepository.findById(reviewStoreId)
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ReviewStore not found"));

        if (!reviewStore.getUser().getEmail().equals(user.getEmail())) throw new IllegalStateException("This is not your data. You cannot delete this data!");

        reviewStoreRepository.delete(reviewStore);
        redisTemplate.delete("store-review:" + reviewStoreId);
    }

    @Transactional(readOnly = true)
    @Override
    @CacheEvict(value = "store", allEntries = true)
    public void deleteReviewStoreWithSales(String reviewStoreId) {
        User sales = userDetailToken.dataUserEmail();
        Store store = storeRepository.findByUserEmail(sales.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        ReviewStore reviewStore = reviewStoreRepository.findById(reviewStoreId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!store.getUser().getEmail().equals(sales.getEmail())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You do not have access to delete the following data!");

        reviewStoreRepository.delete(reviewStore);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "store", key = "'getReviewStoresWithStoreId:' + #storeId")
    public List<ReviewStoreResponse> getAllReviewStoreById(String storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review Store With Store ID " + storeId + " Not Found!"));

        List<ReviewStore> reviewStores = reviewStoreRepository.findByStoreId(store.getId());
        if (reviewStores.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No reviews found for this store");
        return reviewStores.stream()
                .map(this::toReviewStoreResponse)
                .toList();
    }
}