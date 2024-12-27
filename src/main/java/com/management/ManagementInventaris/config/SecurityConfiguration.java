package com.management.ManagementInventaris.config;

import com.management.ManagementInventaris.filter.ApiKeyFilter;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.management.ManagementInventaris.user.Permission.*;
import static com.management.ManagementInventaris.user.Role.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
            "/ws/**",
            "/topic/**",
            "/app/**",
            "/send/**",
            "/actuator",
            "/actuator/**",
            "/api/v1/auth/**",
            "/api/v1/location/indonesia/**",
            "/api/v1/jobs/list",
            "/api/v1/jobs/findById/**",
            "/api/v1/products/getProductsWithUser/**",
            "/api/v1/jobs/findByName/**",
            "/api/v1/categories/list",
            "/api/v1/categories/findById/**",
            "/api/v1/categories/findByName/**",
            "/api/v1/categories/findByKeyword/**",
            "/api/v1/products/list",
            "/api/v1/products/search/products/**",
            "/api/v1/products/search/productsByCategory/**",
            "/api/v1/products/findById/**",
            "/api/v1/products/search/price/cheap",
            "/api/v1/auth/findUserByEmail/**",
            "/api/v1/auth/findUserById/**",
            "/api/minio/**",
            "/api/v1/genders/**",
            "/sendMail",
            "/sendMailWithAttachment",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/api/v1/orders/request-order",
            "/api/v1/promoted/allPromoted",
            "/api/v1/promoted/{promotionId}",
            "/api/v1/promoted/search",
            "/api/v1/coupons/applyCoupon",
            "/api/v1/coupons/allCoupons"
    };
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final ApiKeyFilter apiKeyFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/api/v1/store/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/store/create").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(POST, "/api/v1/store/store-accounting").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(PATCH, "/api/v1/store/update").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(PATCH, "/api/v1/store/update/store-accounting").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/store/delete").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers(GET, "/api/v1/store/find/{storeId}").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(GET, "/api/v1/store/all").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(GET, "/api/v1/store/all-store-accounting").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers("/api/v1/review-store").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/review-store/create-review").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(PATCH, "/api/v1/review-store/update-review/{reviewStoreId}").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/review-store/delete-review").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(), USER_DELETE.name())
                                .requestMatchers(DELETE, "/api/v1/review-store/delete-review-by-sales").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(), USER_DELETE.name())
                                .requestMatchers(GET, "/api/v1/review-store/stores/{storeId}/reviews").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER_READ.name())
                                .requestMatchers("/api/v1/users/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/users/follow/{id}").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(POST, "/api/v1/users/unfollow/{id}").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER_UPDATE.name())
                                .requestMatchers(GET, "/api/v1/users/followers/me").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER_READ.name())
                                .requestMatchers(GET, "/api/v1/users/following/me").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER_READ.name())
                                .requestMatchers(POST, "/api/v1/users/like/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(POST, "/api/v1/users/unlike/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(DELETE, "/api/v1/users/delete-account/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(), USER_DELETE.name())
                                .requestMatchers(PATCH, "/api/v1/users/changePassword").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER_UPDATE.name())
                                .requestMatchers("/api/v1/coupons/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/coupons/create/coupon").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers("/api/v1/jobs/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers(POST, "/api/v1/jobs/create").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PATCH, "/api/v1/jobs/update/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/jobs/delete/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers("/api/v1/employees/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/employees/create").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PATCH, "/api/v1/employees/update/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/employees/delete/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers(GET, "/api/v1/employees/list").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER_READ.name())
                                .requestMatchers(GET, "/api/v1/employees/findByNip").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(GET, "/api/v1/employees/findAllByJob/**?view=private").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(GET, "/api/v1/employees/findAllByJob/**?view=public").permitAll()
                                .requestMatchers(GET, "/api/v1/employees/findById/**?view=private").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(GET, "/api/v1/employees/findById/**?view=public").permitAll()
                                .requestMatchers("/api/v1/categories/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/categories/create").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(PATCH, "/api/v1/categories/update/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/categories/delete/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(), USER_DELETE.name())
                                .requestMatchers("/api/v1/products/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/v1/products/**/toggleRating").hasAnyAuthority(USER_CREATE.name(), ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(DELETE, "/api/v1/products/delete/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers(GET, "/api/v1/products/checkStock").hasAnyAuthority(USER_READ.name(), ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(POST, "/api/v1/products/create").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(PATCH, "/api/v1/products/update/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(), USER_UPDATE.name())
                                .requestMatchers(GET, "/api/v1/auth/findUserWithRegency/**").hasAnyAuthority(ADMIN_READ.name())
                                .requestMatchers("/api/saved-products/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(POST, "/api/saved-products/save/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(), USER_CREATE.name())
                                .requestMatchers(GET, "/api/saved-products/user").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER_READ.name())
                                .requestMatchers("/api/v1/carts/**").hasAnyRole(ADMIN.name(), MANAGER.name(), USER.name())
                                .requestMatchers(GET, "/api/v1/carts/my-cart").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(), USER_READ.name())
                                .requestMatchers(GET,"/api/v1/devtools/execute?command=**").hasRole(ADMIN.name())
                                .requestMatchers("/api/v1/promoted/**").hasAnyRole(ADMIN.name(), MANAGER_READ.name())
                                .requestMatchers(POST, "/api/v1/promoted/create").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PATCH, "/api/v1/promoted/update/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/promoted/deletePromoted/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> {
                                    SecurityContextHolder.clearContext();
                                    response.addCookie(new Cookie("access_token", null));
                                    response.addCookie(new Cookie("refresh_token", null));
                                })
                );
        return http.build();
    }
}