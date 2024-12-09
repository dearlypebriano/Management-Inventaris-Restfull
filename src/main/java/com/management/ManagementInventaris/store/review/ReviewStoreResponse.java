package com.management.ManagementInventaris.store.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewStoreResponse implements Serializable {

    private String id;

    private String username;

    private String message;

    private Double rating;

    private String storeName;

    private String createdAt;
}