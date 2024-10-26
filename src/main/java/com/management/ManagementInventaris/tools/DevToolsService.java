package com.management.ManagementInventaris.tools;

import com.management.ManagementInventaris.auth.AuthenticationService;
import com.management.ManagementInventaris.config.JwtService;
import com.management.ManagementInventaris.gender.GenderRepository;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.key.ApiKeyRepository;
import com.management.ManagementInventaris.key.ApiKeyService;
import com.management.ManagementInventaris.location.district.DistrictService;
import com.management.ManagementInventaris.location.province.ProvinceService;
import com.management.ManagementInventaris.location.regency.RegencyService;
import com.management.ManagementInventaris.location.village.VillageService;
import com.management.ManagementInventaris.token.TokenRepository;
import com.management.ManagementInventaris.user.*;
import com.management.ManagementInventaris.utils.UserDetailToken;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ch.qos.logback.core.util.StringUtil.capitalizeFirstLetter;
import static com.management.ManagementInventaris.user.Role.ADMIN;

@Slf4j
@Service
public class DevToolsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailToken userDetailToken;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private GenderRepository genderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private RegencyService regencyService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private VillageService villageService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenRepository tokenRepository;

    @Getter
    @Setter
    private String usedDatabase;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.jpa.database}")
    private String databaseType;

    @Value("${spring.flyway.enabled:false}")
    private boolean isFlywayEnabled;

    public String getDatabaseUsed() {
        validateRole();
        if (usedDatabase == null) {
            String dbName = databaseUrl.substring(databaseUrl.lastIndexOf("/") + 1);
            String dbDriver = formatDatabaseType(databaseType);
            return dbDriver + "@" + dbName;
        } else {
            return usedDatabase;
        }
    }

    public void useDatabase(@NotNull String dbName) {
        validateRole();
        this.usedDatabase = dbName;
    }

    public List<String> getAllTables() {
        validateRole();
        if (usedDatabase == null) {
            throw new IllegalStateException("No database selected. Please use the USE command first.");
        }

        List<String> tableList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableList.add(tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tableList;
    }

    public String checkFlywayStatus() {
        validateRole();
        if (isFlywayEnabled) {
            return "FlywayDB is enabled and running.";
        } else {
            return "FlywayDB is not enabled.";
        }
    }

    public String checkCacheStatus() {
        validateRole();
        return cacheManager != null ? "Cache Manager is enabled" : "No caching system is enabled";
    }

    private void validateRole() {
        User validUser = userDetailToken.dataUserEmail();
        if (!validUser.getRole().name().equals(ADMIN.name())) throw new IllegalStateException("Invalid Role: " + validUser.getRole());
    }

    public String lockAccount(String userId, String email) {
        validateRole();
        User user = null;

        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        } else if (email != null) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        } else {
            throw new IllegalArgumentException("Either userId or email must be provided.");
        }

        user.setAccountNonLocked(false);
        userRepository.save(user);

        return "Account for user with " + (userId != null ? "id: " + userId : "email: " + email) + " has been locked.";
    }

    public String unlockAccount(String userId, String email) {
        validateRole();

        User user = null;

        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        } else if (email != null) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        } else {
            throw new IllegalArgumentException("Either userId or email must be provided.");
        }

        user.setAccountNonLocked(true);
        userRepository.save(user);

        return "Account for user with " + (userId != null ? "id: " + userId : "email: " + email) + " has been unlocked.";
    }

    public WebResponse<List<UserProfile>> getAllDataUser(int page, int size) {
        validateRole();

        int offset = page * size;
        List<User> users = userRepository.findAllWithPagination(offset, size);

        List<UserProfile> userProfiles = users.stream()
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
                            .phone(user.getPhone())
                            .whatsappUrl("https://wa.me/" + user.getPhone())
                            .role(user.getRole())
                            .imageUrl(user.getImageUrl())
                            .location(location)
                            .build();
                }).toList();

        return WebResponse.<List<UserProfile>>builder()
                .data(userProfiles)
                .build();
    }

    @Contract(pure = true)
    private String formatDatabaseType(String dbType) {
        return switch (dbType.toLowerCase()) {
            case "postgresql" -> "PostgreSQL";
            case "mysql" -> "MySQL";
            case "mariadb" -> "MariaDB";
            case "h2" -> "H2";
            case "sqlite" -> "SQLite";
            case "oracle" -> "Oracle";
            case "db2" -> "DB2";
            case "sqlserver" -> "SQL Server";
            case "mongodb" -> "MongoDB";
            case "cassandra" -> "Cassandra";
            case "neo4j" -> "Neo4j";
            case "orientdb" -> "OrientDB";
            case "redis" -> "Redis";
            case "elasticsearch" -> "Elasticsearch";
            case "memcached" -> "Memcached";
            default -> dbType;
        };
    }

    public String generateNewApiKey() {
        apiKeyRepository.deleteAll();
        String apiKey = apiKeyService.generateUniqueApiKey();
        return "The latest API Key has been successfully generated : " + apiKey;
    }
}