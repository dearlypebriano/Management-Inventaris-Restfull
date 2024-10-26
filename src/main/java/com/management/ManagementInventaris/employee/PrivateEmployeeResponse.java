package com.management.ManagementInventaris.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrivateEmployeeResponse implements Serializable {

    private String id;
    private String employeeName;
    private Integer nip;
    private Long phone;
    private String gender;
    private String jobName;
    private String salary;
    private String province;
    private String regency;
    private String district;
    private String village;
    private Integer postalCode;
    private String profile;
}
