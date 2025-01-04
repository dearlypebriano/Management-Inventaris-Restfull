package com.management.ManagementInventaris.employee;

import com.management.ManagementInventaris.gender.Gender;
import com.management.ManagementInventaris.job.Job;
import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.village.Village;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "employees")
public class Employee implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(nullable = false, updatable = false, unique = true)
    private Integer nip;

    @Column(name = "phone", nullable = false, columnDefinition = "bigint default 0")
    private Long phone = 0L;

    @NotNull(message = "PRICE_NULL")
    @DecimalMin(value = "0.0", inclusive = false, message = "PRICE_TOO_LOW")
    @Column(name = "salary", columnDefinition = "numeric(38,2)")
    private BigDecimal salary;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "job_id", referencedColumnName = "id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne
    @JoinColumn(name = "regency_id")
    private Regency regency;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "village_id", referencedColumnName = "id")
    private Village village;

    @Column(name = "postal_code")
    private Integer postalCode;

    @Column(name = "image_url")
    private String imageUrl;
}