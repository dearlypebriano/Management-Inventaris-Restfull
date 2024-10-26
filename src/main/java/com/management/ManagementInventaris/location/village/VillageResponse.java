package com.management.ManagementInventaris.location.village;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageResponse implements Serializable {
    private String id;
    private String district;
    private String name;
}