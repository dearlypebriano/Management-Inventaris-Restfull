package com.management.ManagementInventaris.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfile {

    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String bioProfile;
    private String username;
    private Long phone;
    private String whatsappUrl;
    private Integer followersCount;
    private Integer followingCount;
    private Integer likedCount;
    private String userUrl;
    private Role role;
    private String location;
    private String createdAt;
    private String updatedAt;
    private String timezoneLabel;
    private String imageUrl;
}