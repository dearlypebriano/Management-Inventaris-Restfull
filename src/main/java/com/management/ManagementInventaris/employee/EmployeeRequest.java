package com.management.ManagementInventaris.employee;

import com.management.ManagementInventaris.gender.Gender;
import com.management.ManagementInventaris.job.Job;
import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.village.Village;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeRequest {

    @NotNull
    private String employeeName;

    @NotNull
    private String gender;

    @NotNull
    private String phone;

    @NotNull
    private BigDecimal salary;

    @NotNull
    private String job;

    @NotNull
    private String provinceName;

    @NotNull
    private String regencyName;

    @NotNull
    private String districtName;

    @NotNull
    private String villageName;

    private Integer postalCode;
}
