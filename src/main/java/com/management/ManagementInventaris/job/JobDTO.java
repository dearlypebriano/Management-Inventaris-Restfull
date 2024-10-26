package com.management.ManagementInventaris.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobDTO {

    private String id;

    private String jobName;

    private String description;

    public static JobDTO fromEntity(Job job) {
        return JobDTO.builder()
                .id(job.getId())
                .jobName(job.getJobName())
                .description(job.getDescription())
                .build();
    }
}