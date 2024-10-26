package com.management.ManagementInventaris.gender;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenderRequest {
    private Integer id;
    private String name;
}