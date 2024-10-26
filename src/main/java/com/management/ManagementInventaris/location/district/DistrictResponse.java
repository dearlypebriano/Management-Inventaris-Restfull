package com.management.ManagementInventaris.location.district;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistrictResponse implements Serializable {

    private String id;

    private String regency;

    private String name;
}