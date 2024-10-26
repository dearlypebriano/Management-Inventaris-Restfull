package com.management.ManagementInventaris.location.regency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegencyRequest {

    private String provinceName;

    private String name;
}