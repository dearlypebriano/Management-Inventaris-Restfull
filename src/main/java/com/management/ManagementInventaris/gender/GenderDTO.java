package com.management.ManagementInventaris.gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenderDTO {

    private Integer id;

    private String name;

    public static GenderDTO fromEntity(Gender gender) {
        return GenderDTO.builder()
                .id(gender.getId())
                .name(gender.getName())
                .build();
    }
}