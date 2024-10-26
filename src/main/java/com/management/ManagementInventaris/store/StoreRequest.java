package com.management.ManagementInventaris.store;

import com.management.ManagementInventaris.location.village.Village;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreRequest {

    private String storeName;

    private String street;
}