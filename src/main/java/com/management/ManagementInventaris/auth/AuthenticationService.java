package com.management.ManagementInventaris.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.ManagementInventaris.config.JwtService;
import com.management.ManagementInventaris.gender.Gender;
import com.management.ManagementInventaris.gender.GenderRepository;
import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.district.DistrictService;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.province.ProvinceService;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.regency.RegencyService;
import com.management.ManagementInventaris.location.village.Village;
import com.management.ManagementInventaris.location.village.VillageService;
import com.management.ManagementInventaris.token.Token;
import com.management.ManagementInventaris.token.TokenRepository;
import com.management.ManagementInventaris.token.TokenType;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserProfile;
import com.management.ManagementInventaris.user.UserRepository;
import com.management.ManagementInventaris.utils.Cryptographic;
import com.management.ManagementInventaris.utils.ImageCompressor;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for handling user authentication and registration.
 * Provides functionalities for user registration, authentication,
 * JWT token management, and user profile management. It integrates with
 * multiple services such as Gender, Location (Province, Regency, District, Village),
 * and external file storage using Minio for handling user profile images.
 *
 * The service supports JWT-based authentication with access and refresh tokens,
 * stores tokens in cookies for security, and allows refreshing tokens.
 *
 * <p>This class also offers mechanisms for:
 * - Registering a new user with multiple related entity lookups (location, gender).
 * - Managing JWT authentication for login and token refresh.
 * - Setting default user profile images and handling custom profile image uploads to Minio.
 * - Providing user profile search based on location hierarchy.
 *
 * The service is highly cohesive, dealing with multiple concerns around authentication
 * and user identity while maintaining the complexity of managing relationships across
 * different entities and services.</p>
 *
 * <h2>Integration Points</h2>
 * <ul>
 *     <li>MinIO - External file storage for user profile images.</li>
 *     <li>JWTService - Responsible for generating and validating access and refresh tokens.</li>
 *     <li>Gender, Province, Regency, District, and Village services - Handle related user entities for location and gender.</li>
 *     <li>EmailService - (Optional) Service for sending out registration or authentication emails (not implemented in this class).</li>
 * </ul>
 *
 * @see com.management.ManagementInventaris.config.JwtService
 * @see com.management.ManagementInventaris.location.province.ProvinceService
 * @see com.management.ManagementInventaris.token.TokenRepository
 * @see com.management.ManagementInventaris.user.UserRepository
 * @see io.minio.MinioClient
 *
 * @version 6.4.5
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GenderRepository genderRepository;
    private final ProvinceService provinceService;
    private final RegencyService regencyService;
    private final DistrictService districtService;
    private final VillageService villageService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.defaultProfilePath}")
    private String defaultProfilePath;

    @Value("${minio.url}")
    private String minioUrl;

    /**
     * Registers a new user in the system.
     *
     * The method validates if the email is already in use. It constructs the user entity
     * with related location and gender entities, encodes the user's password, sets the default
     * profile picture, and stores the user information in the database. It also generates JWT
     * access and refresh tokens for the user and sets them in the HTTP response cookies.
     *
     * <h2>Validation:</h2>
     * <ul>
     *     <li>Checks if the email already exists in the system.</li>
     *     <li>Validates that the gender and location (province, regency, district, village) exist in the database.</li>
     *     <li>Sanitizes the phone number by removing any non-numeric characters.</li>
     * </ul>
     *
     * <h2>Security:</h2>
     * <ul>
     *     <li>Encrypts the user password using {@link PasswordEncoder}.</li>
     *     <li>Generates and stores JWT access and refresh tokens using {@link JwtService}.</li>
     * </ul>
     *
     * <h2>Exceptions:</h2>
     * <ul>
     *     <li>{@link ResponseStatusException} - if the email is already registered.</li>
     *     <li>{@link IllegalArgumentException} - if the location entities (province, regency, district, or village) cannot be found.</li>
     * </ul>
     *
     * @param request   {@link RegisterRequest} object containing user registration information.
     * @param response  {@link HttpServletResponse} to set JWT tokens as cookies.
     * @return {@link AuthenticationResponse} containing access and refresh tokens.
     * @throws ResponseStatusException If the email is already in use or any validation fails.
     */
    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    @CachePut(value = "user", key = "#request.email")
    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
        if (repository.findByEmail(request.getEmail()).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Registration Failed. Email Already Exists");

        Gender gender = genderRepository.findByName(request.getGender().getName()).orElse(null);
        Province province = provinceService.findByName(request.getProvinceName().getName());
        Regency regency = regencyService.findByNames(province.getName(), request.getRegencyName().getName());
        District district = districtService.findDistrictByNames(province.getName(), regency.getName(), request.getDistrictName().getName());
        Village village = villageService.getVillageByDistrictAndNames(province.getName(), regency.getName(), district.getName(), request.getVillageName().getName());

        long cleanedPhone = Long.parseLong(cleanPhoneNumber(request.getPhone()));

        var user = User.builder()
                .id(UUID.randomUUID().toString())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(cleanedPhone)
                .role(request.getRole())
                .gender(gender)
                .province(province)
                .regency(regency)
                .district(district)
                .village(village)
                .accountNonLocked(true)
                .build();

        setDefaultProfile(user);
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);

        addTokenToCookie(response, jwtToken, refreshToken);

        AuthenticationDTO authDTO = AuthenticationDTO.fromEntity(user);
        redisTemplate.opsForValue().set("user:" + authDTO.getEmail(), authDTO);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Cleans a phone number by removing any non-numeric characters. This method is used during user registration
     * to ensure that the phone number is sanitized before being stored in the database.
     *
     * @param phone the raw phone number provided by the user.
     * @return the sanitized phone number containing only digits, or the original phone number if no changes were necessary.
     */
    private String cleanPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) return phone;
        return phone.replaceAll("[^\\d]", "");
    }

    /**
     * Authenticates a user based on email and password.
     *
     * This method performs authentication by validating the email and password
     * combination. If successful, it generates new JWT access and refresh tokens,
     * revokes any existing tokens, and stores the new tokens in the database and
     * cookies.
     *
     * <h2>Authentication Process:</h2>
     * <ul>
     *     <li>Uses {@link AuthenticationManager} to authenticate the user credentials.</li>
     *     <li>Generates new tokens and invalidates any existing tokens.</li>
     * </ul>
     *
     * <h2>Security:</h2>
     * <ul>
     *     <li>Revokes all previous tokens for the user upon successful login.</li>
     *     <li>Generates new JWT tokens using {@link JwtService}.</li>
     * </ul>
     *
     * <h2>Exceptions:</h2>
     * <ul>
     *     <li>{@link ResponseStatusException} - if authentication fails (e.g., invalid credentials).</li>
     * </ul>
     *
     * @param request   {@link AuthenticationRequest} object containing the user's email and password.
     * @param response  {@link HttpServletResponse} to set JWT tokens as cookies.
     * @return {@link AuthenticationResponse} containing the access and refresh tokens.
     * @throws ResponseStatusException If authentication fails due to invalid credentials.
     */
    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    @CachePut(value = "user", key = "#request.email")
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password", e);
        }
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        addTokenToCookie(response, jwtToken, refreshToken);

        AuthenticationDTO authDTO = AuthenticationDTO.fromEntity(user);
        redisTemplate.opsForValue().set("user:" + authDTO.getEmail(), authDTO);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Refreshes the user's JWT access token using the provided refresh token. The refresh token is extracted
     * from the `Authorization` header of the HTTP request. If the refresh token is valid, a new access token is generated,
     * and both the old tokens are revoked.
     *
     * @param request  the {@link HttpServletRequest} containing the refresh token in the Authorization header.
     * @param response the {@link HttpServletResponse} where the new tokens are added as cookies.
     * @throws IOException if an I/O error occurs during the process.
     */
    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    @CachePut(value = "user", key = "#user.email")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                addTokenToCookie(response, accessToken, refreshToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    /**
     * Adds JWT access and refresh tokens to HTTP cookies.
     *
     * This function creates two cookies: one for the access token and one for the refresh token.
     * The access token cookie is set to be HTTP-only and secure (HTTPS), and it is valid for 30 days.
     * The refresh token cookie is also set to be HTTP-only and secure (HTTPS), and it is valid for 30 days.
     *
     * @param response The HTTP response object where the cookies will be added.
     * @param accessToken The JWT access token to be stored in the access token cookie.
     * @param refreshToken The JWT refresh token to be stored in the refresh token cookie.
     */
    private void addTokenToCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        accessTokenCookie.setHttpOnly(false);
        accessTokenCookie.setSecure(false); // Rubah menjadi true jika menggunakan HTTPS
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(false);
        refreshTokenCookie.setSecure(false); // Rubah menjadi true jika menggunakan HTTPS
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    /**
     * Finds a user profile by their unique identifier.
     *
     * This method retrieves the user by ID and constructs a {@link UserProfile} object
     * that includes user details such as name, email, phone number, location, and profile image.
     * The location is formatted as a string in the form of "Province, Regency, District, Village".
     *
     * <h2>Output:</h2>
     * <ul>
     *     <li>Returns a {@link UserProfile} with user details.</li>
     *     <li>Constructs a formatted location string from the related entities (Province, Regency, etc.).</li>
     * </ul>
     *
     * <h2>Exceptions:</h2>
     * <ul>
     *     <li>{@link ResponseStatusException} - if the user is not found in the system.</li>
     * </ul>
     *
     * @param id The unique identifier of the user.
     * @return {@link UserProfile} object containing the user's information.
     * @throws ResponseStatusException If the user is not found.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "'getUserById:' + #id")
    public UserProfile findUserById(String id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found!"));

        String location = String.format("%s, %s, %s, %s",
                capitalizeFirstLetter(user.getProvince().getName()),
                capitalizeFirstLetter(user.getRegency().getName()),
                capitalizeFirstLetter(user.getDistrict().getName()),
                capitalizeFirstLetter(user.getVillage().getName()));

        String idUser = "";
        try {
            idUser = Cryptographic.encrypt(user.getId());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Error encrypting user ID", e);
        }

        String encryptEmail = "";
        try {
            encryptEmail = Cryptographic.encrypt(user.getEmail());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Error encrypting user email", e);
        }

        String located = "";
        try {
            located = Cryptographic.encrypt(location);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return UserProfile.builder()
                .id(idUser)
                .email(encryptEmail)
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsernameUser())
                .bioProfile(user.getBioProfile())
                .phone(user.getPhone())
                .followersCount(user.getFollowersCount())
                .followingCount(user.getFollowingCount())
                .likedCount(user.getLikedUsersCount())
                .whatsappUrl("https://wa.me/" + user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .timezoneLabel(user.getTimezoneLabel())
                .location(located)
                .imageUrl(user.getImageUrl())
                .build();
    }

    /**
     * Finds a user profile by their email.
     *
     * This method functions similarly to {@link #findUserById(String)} but searches
     * for the user based on their email address. It constructs a {@link UserProfile}
     * object with user details.
     *
     * @param email The user's email address.
     * @return {@link UserProfile} object containing the user's information.
     * @throws ResponseStatusException If the user is not found by the provided email.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "'getUserByEmail:' + #email")
    public UserProfile findUserByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new  ResponseStatusException(HttpStatus.NOT_FOUND, "Email dengan alamat: " + email + " Tidak dapat ditemukan"));

        String location = String.format("%s, %s, %s, %s",
                capitalizeFirstLetter(user.getProvince().getName()),
                capitalizeFirstLetter(user.getRegency().getName()),
                capitalizeFirstLetter(user.getDistrict().getName()),
                capitalizeFirstLetter(user.getVillage().getName()));

        return UserProfile.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .bioProfile(user.getBioProfile())
                .username(user.getUsernameUser())
                .phone(user.getPhone())
                .whatsappUrl("https://wa.me/" + user.getPhone())
                .followersCount(user.getFollowersCount())
                .followingCount(user.getFollowingCount())
                .likedCount(user.getLikedUsersCount())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .location(location)
                .imageUrl(user.getImageUrl())
                .timezoneLabel(user.getTimezoneLabel())
                .build();
    }

    /**
     * Finds a list of user profiles based on the provided province and regency.
     *
     * This function retrieves a list of users who belong to the specified province and regency.
     * It constructs a {@link UserProfile} object for each user, including their details such as name,
     * email, phone number, location, and profile image. The location is formatted as a string in the form
     * of "Province, Regency, District, Village".
     *
     * @param provinceName The name of the province.
     * @param regencyName The name of the regency.
     * @return A list of {@link UserProfile} objects containing the user's information.
     * @throws ResponseStatusException If no users are found in the specified province and regency.
     */
    @Transactional
    @Cacheable(value = "user", key = "'getAllUserByProvinceAndRegency:' + #provinceName + ' - #regencyName")
    public List<UserProfile> findUserWithRegency(String provinceName, String regencyName) {
        Province province = provinceService.findByName(provinceName);
        Regency regency = regencyService.findByNames(province.getName(), regencyName);

        List<User> users = repository.findByProvinceNameAndRegencyName(province.getName(), regency.getName());
        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found in province: " + provinceName + " and regency: " + regencyName);
        }

        return users.stream()
                .map(user -> {
                    String location = String.format("%s, %s, %s, %s",
                            capitalizeFirstLetter(user.getProvince().getName()),
                            capitalizeFirstLetter(user.getRegency().getName()),
                            capitalizeFirstLetter(user.getDistrict().getName()),
                            capitalizeFirstLetter(user.getVillage().getName()));

                    return UserProfile.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .firstname(user.getFirstname())
                            .lastname(user.getLastname())
                            .bioProfile(user.getBioProfile())
                            .username(user.getUsername())
                            .phone(user.getPhone())
                            .whatsappUrl("https://wa.me/" + user.getPhone())
                            .role(user.getRole())
                            .imageUrl(user.getImageUrl())
                            .location(location)
                            .timezoneLabel(user.getTimezoneLabel())
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Capitalizes the first letter of a given word and converts the rest of the letters to lowercase.
     *
     * @param word The word to be capitalized.
     * @return The capitalized word. If the input word is null or empty, the original word is returned.
     */
    private String capitalizeFirstLetter(String word) {
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    /**
     * Sets the default profile image URL for a user.
     *
     * This method sets the default profile image URL for a user. If no profile image is provided,
     * this method is called to set the default image. The default profile image URL is set to
     * "default_profile.jpg".
     *
     * @param user The user for whom the default profile image URL is being set.
     */
    private void setDefaultProfile(User user) {
        user.setImageUrl("default_profile.jpg");
    }

    /**
     * Saves or updates a user's profile picture.
     *
     * This method allows users to upload a new profile picture. The picture is validated
     * for file type (supports PNG, JPG, JPEG, WEBP), and uploaded to Minio. The user's
     * profile image URL is updated after the upload completes. If no file is provided,
     * the default profile image is set.
     *
     * <h2>File Handling:</h2>
     * <ul>
     *     <li>Supported file types: PNG, JPG, JPEG, WEBP.</li>
     *     <li>Files are uploaded to Minio with a hashed file name to avoid collisions.</li>
     * </ul>
     *
     * <h2>Exceptions:</h2>
     * <ul>
     *     <li>{@link ResponseStatusException} - if the file type is unsupported or if any errors occur during upload.</li>
     *     <li>{@link IOException} - if an I/O error occurs while handling the uploaded file.</li>
     *     <li>{@link MinioException} - if Minio encounters an error during file upload.</li>
     * </ul>
     *
     * @param email The user's email for whom the profile picture is being updated.
     * @param file  The {@link MultipartFile} representing the profile picture.
     * @throws ResponseStatusException If the email is not found or file type is unsupported.
     */
    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    @CachePut(value = "user", key = "#email")
    public void saveUserProfile(String email, MultipartFile file) {
        try {
            User user = repository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email dengan alamat: " + email + " Tidak dapat ditemukan"));

            List<String> allowedExtensions = Arrays.asList("PNG", "png", "JPG", "jpg", "JPEG", "jpeg", "webp");

            if (file != null && !file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
                if (!allowedExtensions.contains(extension)) {
                    throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File extension not supported");
                }

                String hashedFileName = ImageCompressor.hashFileName(originalFileName, file.getBytes());

                // Upload file to Minio
                String objectName = "/uploaded/profile/uploaded/" + user.getId() + "/" + hashedFileName;
                InputStream inputStream = file.getInputStream();
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, inputStream.available(), -1)
                                .contentType(file.getContentType())
                                .build()
                );

                user.setImageUrl(String.format("%s/%s/%s", "http://localhost", "api/minio/download/uploaded/profile/uploaded", hashedFileName));
            } else {
                setDefaultProfile(user);
            }

            repository.save(user);
        } catch (IOException  | NoSuchAlgorithmException | InvalidKeyException |
                 ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 ServerException | XmlParserException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while processing profile");
        }
    }
}