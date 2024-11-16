package com.management.ManagementInventaris.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.management.ManagementInventaris.store.review.ReviewStoreResponse;
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
public class WebResponse<T> implements Serializable {

    private T data;

    private String errors;

    private PagingResponse paging;
}