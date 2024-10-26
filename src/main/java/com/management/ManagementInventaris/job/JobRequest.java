package com.management.ManagementInventaris.job;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobRequest {

    @NotNull
    private String jobName;

    @NotNull
    private String description;
}