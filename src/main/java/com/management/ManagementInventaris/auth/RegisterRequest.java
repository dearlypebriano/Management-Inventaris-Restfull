package com.management.ManagementInventaris.auth;

import com.management.ManagementInventaris.gender.Gender;
import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.village.Village;
import com.management.ManagementInventaris.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @NotNull
    private String email;

    @NotBlank
    private String phone;

    private String bioProfile;

    @NotNull
    private String password;

    @NotNull
    private Role role;

    private Gender gender;

    private Province provinceName;

    private Regency regencyName;

    private District districtName;

    private Village villageName;
}