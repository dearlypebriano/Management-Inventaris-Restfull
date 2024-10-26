package com.management.ManagementInventaris.order.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.management.ManagementInventaris.order.OrderResponse;
import com.management.ManagementInventaris.user.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderHistoryResponse implements Serializable {

    private String id;

    private UserProfile user;

    private List<OrderResponse> orderResponses;

    private String status;
}