package com.management.ManagementInventaris.store.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewStoreRequest {

    private String message;

    private Integer rating;
}