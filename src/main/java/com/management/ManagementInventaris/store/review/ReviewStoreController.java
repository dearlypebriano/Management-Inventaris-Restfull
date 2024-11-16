package com.management.ManagementInventaris.store.review;

import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.utils.Cryptographic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/review-store")
public class ReviewStoreController {

    @Autowired
    private ReviewStoreService reviewStoreService;

    @PostMapping(path = "/create-review", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WebResponse<ReviewStoreResponse>> createNewReviewStore(@ModelAttribute ReviewStoreRequest request) {
        ReviewStoreResponse response = reviewStoreService.createReviewStore(request);
        WebResponse<ReviewStoreResponse> webResponse = WebResponse.<ReviewStoreResponse>builder()
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(webResponse);
    }

    @PatchMapping(path = "/update-review/{reviewStoreId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<ReviewStoreResponse>> updateReviewStore(
            @ModelAttribute ReviewStoreRequest request,
            @PathVariable String reviewStoreId)
    {
        String decryptedReviewStoreId;
        try {
            decryptedReviewStoreId = Cryptographic.decrypt(reviewStoreId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        ReviewStoreResponse response = reviewStoreService.updateReviewStore(request, decryptedReviewStoreId);
        WebResponse<ReviewStoreResponse> webResponse = WebResponse.<ReviewStoreResponse>builder().data(response).build();
        return ResponseEntity.status(HttpStatus.OK).body(webResponse);
    }

    @DeleteMapping(path = "/delete-review", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteReviewStore(@RequestParam String reviewStoreId) {
        String decryptedReviewStoreId;
        try {
            decryptedReviewStoreId = Cryptographic.decrypt(reviewStoreId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        reviewStoreService.deleteReviewStore(decryptedReviewStoreId);
        return ResponseEntity.status(HttpStatus.OK).body("Data Deleted Successfully!");
    }

    @DeleteMapping(path = "/delete-review-by-sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteReviewStoreWithSales(@RequestParam String reviewStoreId) {
        String decryptedReviewStoreId;
        try {
            decryptedReviewStoreId = Cryptographic.decrypt(reviewStoreId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        reviewStoreService.deleteReviewStoreWithSales(decryptedReviewStoreId);
        return ResponseEntity.status(HttpStatus.OK).body("Data Deleted Successfully By Sales!");
    }

    @GetMapping(path = "/stores/{storeId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<ReviewStoreResponse>>> getAllReviewStoreById(@RequestParam String storeId) {
        String decryptStoreId;
        try {
            decryptStoreId = Cryptographic.decrypt(storeId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        List<ReviewStoreResponse> response = reviewStoreService.getAllReviewStoreById(decryptStoreId);
        WebResponse<List<ReviewStoreResponse>> webResponse = WebResponse.<List<ReviewStoreResponse>>builder().data(response).build();
        return ResponseEntity.status(HttpStatus.OK).body(webResponse);
    }
}