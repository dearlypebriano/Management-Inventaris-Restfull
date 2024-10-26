package com.management.ManagementInventaris.utils;

import com.management.ManagementInventaris.config.JwtService;
import com.management.ManagementInventaris.exception.AuthorizationException;
import com.management.ManagementInventaris.token.TokenRepository;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
public final class UserDetailToken {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves user details from the provided JWT token and sets the authentication context if valid.
     * <p>
     * This method performs the following operations:
     * 1. Extracts the username (email) from the JWT token using `jwtService`.
     * 2. Checks if the extracted username is not null and if the current authentication context is empty.
     * 3. Loads the user details using `userDetailsService` based on the extracted username.
     * 4. Validates the token by checking its existence, expiration status, and revocation status from the `tokenRepository`.
     * 5. If the token is valid and matches the user details, creates an `UsernamePasswordAuthenticationToken`
     * and sets it in the `SecurityContextHolder`.
     *
     * @param token The JWT token from which to extract user details.
     * @return The email of the user extracted from the token, or null if the extraction fails or the token is invalid.
     */
    private String getUserDetailsFromToken(String token) {
        String userEmail = jwtService.extractUsername(token);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            boolean isTokenValid = tokenRepository.findByToken(token)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(token, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        return userEmail;
    }

    /**
     * Extracts the JWT token from the "Authorization" header of the HTTP request, validates it,
     * and sets the authentication context if the token is valid.
     * <p>
     * This method performs the following operations:
     * 1. Retrieves the current HTTP request using `RequestContextHolder`.
     * 2. Checks the "Authorization" header for a Bearer token.
     * 3. If the header is missing or does not start with "Bearer ", continues the filter chain and returns null.
     * 4. Extracts the JWT token from the header.
     * 5. Extracts the username (email) from the JWT token using `jwtService`.
     * 6. If the username is not null and there is no current authentication, loads the user details.
     * 7. Validates the token by checking its existence, expiration status, and revocation status from the `tokenRepository`.
     * 8. If the token is valid and matches the user details, creates an `UsernamePasswordAuthenticationToken`
     * and sets it in the `SecurityContextHolder`.
     *
     * @return The extracted JWT token, or null if the token is invalid or not present.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an input or output error occurs.
     */
    private String extractTokenFromBearerHeader() throws ServletException, IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = null;
        FilterChain filterChain = null;
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return null;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            boolean isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        return jwt;
    }

    /**
     * Retrieves the user details associated with the current authenticated user.
     * <p>
     * This method performs the following operations:
     * 1. Extracts the JWT token from the "Authorization" header of the HTTP request.
     * 2. Validates the token by checking its existence, expiration status, and revocation status from the `tokenRepository`.
     * 3. If the token is valid, extracts the username (email) from the JWT token using `jwtService`.
     * 4. Loads the user details from the `userRepository` based on the extracted email.
     * 5. If the user is found, returns the user object.
     * 6. If the user is not found, throws a `ResponseStatusException` with a 404 status code and a message indicating that the user was not found.
     * 7. If any other exception occurs, throws a `RuntimeException` with the original exception as the cause.
     *
     * @return The user object associated with the current authenticated user.
     * @throws AuthorizationException If the Bearer token is not found in the Authorization header.
     * @throws ResponseStatusException If the user is not found in the database.
     * @throws RuntimeException       If any other exception occurs.
     */
    public User dataUserEmail() {
        try {
            String token = extractTokenFromBearerHeader();
            if (token == null) throw new AuthorizationException("Bearer token not found in Authorization Header");
            String userEmail = getUserDetailsFromToken(token);
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrentUserId() {
        User user = dataUserEmail();
        return (user != null) ? user.getId() : null;
    }
}