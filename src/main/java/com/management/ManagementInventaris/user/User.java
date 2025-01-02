package com.management.ManagementInventaris.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.management.ManagementInventaris.gender.Gender;
import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.village.Village;
import com.management.ManagementInventaris.store.review.ReviewRating;
import com.management.ManagementInventaris.store.review.ReviewStore;
import com.management.ManagementInventaris.token.Token;
import com.management.ManagementInventaris.utils.DateTimeUtil;
import com.management.ManagementInventaris.utils.Zone;
import jakarta.persistence.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @Column(unique = true, nullable = false, updatable = false)
    private String id;

    @Column(name = "firstname", nullable = false)
    @NotBlank(message = "FIRSTNAME_BLANK")
    @Size(max = 300, message = "FIRSTNAME_TOO_LONG")
    private String firstname;

    @Column(name = "lastname", nullable = false)
    @NotBlank(message = "LASTNAME_BLANK")
    @Size(max = 300, message = "LASTNAME_TOO_LONG")
    private String lastname;

    @Column(name = "bio_profile")
    @Size(max = 1000, message = "BIO_PROFILE_TOO_LONG")
    private String bioProfile = "No Bio Yet!";

    @Column(name = "phone", nullable = false, columnDefinition = "bigint default 0")
    private Long phone = 0L;

    @Column(name = "email", nullable = false)
    @NotBlank(message = "EMAIL_BLANK")
    @Size(max = 300, message = "EMAIL_TOO_LONG")
    private String email;

    @Column(name = "username_user")
    private String usernameUser;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Token> tokens;

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

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress = "0.0.0.0";

    @Column(name = "user_agent", nullable = false)
    private String userAgent = "N/A";

    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked = true;

    @Column(name = "followers_count", nullable = false)
    private Integer followersCount = 0;

    @Column(name = "following_count", nullable = false)
    private Integer followingCount = 0;

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    @Column(name = "liked_users_count", nullable = false)
    private Integer likedUsersCount = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_likes",
            joinColumns = @JoinColumn(name = "liker_id"),
            inverseJoinColumns = @JoinColumn(name = "liked_id"))
    @JsonIgnore
    private Set<User> likes = new HashSet<>();

    @ManyToMany(mappedBy = "likes", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> likedUsers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id"))
    @JsonIgnore
    private Set<User> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> following = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    @Column(name = "timezone_label", nullable = false)
    private String timezoneLabel;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewStore> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewRating> givenRatings = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @PrePersist
    protected void onCreate() {
        String dateTime = DateTimeUtil.getCurrentDateTime(ZoneId.systemDefault());
        ZonedDateTime time = ZonedDateTime.now();
        String zoneId = Zone.getZoneLabel(time);

        this.createdAt = dateTime;
        this.updatedAt = dateTime;
        this.timezoneLabel = zoneId;

        if (firstname != null && !firstname.trim().isEmpty() && lastname != null && !lastname.trim().isEmpty()) {
            this.usernameUser = (firstname + lastname).toLowerCase();
        } else {
            this.usernameUser = "";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = DateTimeUtil.getCurrentDateTime(ZoneId.systemDefault());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public String displayName() {
        return this.firstname + " " + this.lastname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}