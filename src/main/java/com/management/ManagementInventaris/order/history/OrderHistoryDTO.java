package com.management.ManagementInventaris.order.history;

import com.management.ManagementInventaris.user.UserConverter;
import com.management.ManagementInventaris.user.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderHistoryDTO {

    private String id;

    private UserProfile user;

    private String status;

    private Boolean deletedByOwner;

    public static OrderHistoryDTO fromEntity(OrderHistory orderHistory) {
        return OrderHistoryDTO.builder()
                .id(orderHistory.getId())
                .user(UserConverter.toUserProfile(orderHistory.getUser()))
                .status(orderHistory.getStatus())
                .deletedByOwner(orderHistory.getDeletedByOwner())
                .build();
    }
}