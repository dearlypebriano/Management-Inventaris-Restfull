package com.management.ManagementInventaris.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDTO {

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

    private String imageUrl;

    public static EmployeeDTO fromEntity(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .employeeName(employee.getEmployeeName())
                .nip(employee.getNip())
                .phone(employee.getPhone())
                .gender(employee.getGender().getName())
                .jobName(employee.getJob().getJobName())
                .salary(employee.getSalary().toString())
                .province(employee.getProvince().getName())
                .regency(employee.getRegency().getName())
                .district(employee.getDistrict().getName())
                .village(employee.getVillage().getName())
                .postalCode(employee.getPostalCode())
                .imageUrl(employee.getImageUrl())
                .build();
    }
}