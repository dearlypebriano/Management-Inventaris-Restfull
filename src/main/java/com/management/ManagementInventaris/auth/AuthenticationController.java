package com.management.ManagementInventaris.auth;

import com.management.ManagementInventaris.user.UserProfile;
import com.management.ManagementInventaris.utils.Cryptographic;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(service.register(request, response));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(service.authenticate(request, response));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("/saveUserProfile")
    public ResponseEntity<Void> saveUserProfile(
            @PathVariable String email,
            @RequestPart("file") MultipartFile file
    ) {
        service.saveUserProfile(email, file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/findUserByEmail/{email}")
    public ResponseEntity<UserProfile> findUserByEmail(
            @Valid @Email @PathVariable String email
    ) {
        String decryptEmail = "";
        try {
            decryptEmail = Cryptographic.decrypt(email);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        UserProfile projection = service.findUserByEmail(decryptEmail);
        return ResponseEntity.status(HttpStatus.OK).body(projection);
    }

    @GetMapping("/findUserById/{id}")
    public ResponseEntity<UserProfile> findUserById(
            @PathVariable String id
    ) {
        String decryptId = "";
        try {
            decryptId = Cryptographic.decrypt(id);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        UserProfile projection = service.findUserById(decryptId);
        return ResponseEntity.status(HttpStatus.OK).body(projection);
    }

    @GetMapping("/findUserWithRegency/{provinceName}/{regencyName}")
    public ResponseEntity<List<UserProfile>> findUserWithRegency(
            @PathVariable String provinceName,
            @PathVariable String regencyName
    ) {
        List<UserProfile> projectionList = service.findUserWithRegency(provinceName, regencyName);
        return ResponseEntity.status(HttpStatus.OK).body(projectionList);
    }
}