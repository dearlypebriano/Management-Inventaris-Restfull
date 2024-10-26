package com.management.ManagementInventaris.user;

import com.management.ManagementInventaris.exception.AuthorizationException;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.utils.Cryptographic;
import com.management.ManagementInventaris.utils.UserDetailToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserDetailToken userDetailToken;

    

    @PostMapping("/follow/{userId}")
    public ResponseEntity<String> followUser(@PathVariable String userId) {
        String decryptedUserId;
        try {
            decryptedUserId = Cryptographic.decrypt(userId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        userService.followUser(decryptedUserId);
        User user = userRepository.findById(decryptedUserId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return ResponseEntity.ok("Successfully followed user with full name: " + user.displayName());
    }

    @PostMapping("/unfollow/{userId}")
    public ResponseEntity<String> unfollowUser(
            @PathVariable String userId
    ) {
        String decryptedUserId;
        try {
            decryptedUserId = Cryptographic.decrypt(userId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        try {
            User user = userRepository.findById(decryptedUserId).orElse(null);
            assert user != null;
            userService.unfollowUser(decryptedUserId);
            return ResponseEntity.ok("Successfully unfollowed user with ID: " + user.displayName());
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/like/{userIdToLike}")
    public ResponseEntity<String> like(
            @PathVariable("userIdToLike") String userIdToLike) {
        String decryptedUserId;
        try {
            decryptedUserId = Cryptographic.decrypt(userIdToLike);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        User user = userRepository.findById(decryptedUserId).orElse(null);
        userService.like(decryptedUserId);
        assert user != null;
        return ResponseEntity.ok("Successfully liked user %s".formatted(user.displayName()));
    }

    @PostMapping("/unlike/{userIdToUnlike}")
    public ResponseEntity<String> unlike(
            @PathVariable("userIdToUnlike") String userIdToUnlike) {
        String decryptedUserId;
        try {
            decryptedUserId = Cryptographic.decrypt(userIdToUnlike);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        User user = userRepository.findById(userIdToUnlike).orElse(null);
        userService.unlike(decryptedUserId);
        assert user != null;
        return ResponseEntity.ok("Successfully unliked user %s".formatted(user.displayName()));
    }

    @GetMapping(path = "/followers/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<UserProfile>>> getFollowers(@PathVariable String userId) {
        WebResponse<List<UserProfile>> users = userService.getFollowers(userId);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping(path = "/following/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<UserProfile>>> getFollowing(@PathVariable String userId) {
        WebResponse<List<UserProfile>> users = userService.getFollowing(userId);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping(path = "/followers/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<UserProfile>>> getCurrentUserFollowers(Principal connectedUser) {
        User currentUser = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        WebResponse<List<UserProfile>> users = userService.getCurrentUserFollowers(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping(path = "/following/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<UserProfile>>> getCurrentUserFollowing(Principal connectedUser) {
        User currentUser = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        WebResponse<List<UserProfile>> users = userService.getCurrentUserFollowing(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @DeleteMapping(path = "/delete-account/{userId}")
    public ResponseEntity<String> deleteAccount(
            @PathVariable String userId) {
        String decryptedUserId;
        try {
            decryptedUserId = Cryptographic.decrypt(userId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        userService.deleteAccount(decryptedUserId);
        return ResponseEntity.ok("Account deleted successfully");
    }
}