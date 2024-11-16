package com.management.ManagementInventaris.auth;

import com.management.ManagementInventaris.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationDTO {

    private String id;
    private String email;

    public static AuthenticationDTO fromEntity(User user) {
        return AuthenticationDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }
}