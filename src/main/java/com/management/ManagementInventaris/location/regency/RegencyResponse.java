package com.management.ManagementInventaris.location.regency;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegencyResponse implements Serializable {

    private String id;

    private String province;

    private String name;
}