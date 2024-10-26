package com.management.ManagementInventaris.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiKeyService {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Transactional
    public String generateUniqueApiKey() {
        String apiKey;
        do {
            apiKey = generateApiKey();
        } while (apiKeyRepository.existsByKey(apiKey));

        ApiKey key = new ApiKey();
        key.setId(UUID.randomUUID().toString());
        key.setKey(apiKey);
        key.setExpirationDate(LocalDateTime.now().plusMonths(1));
        key.setExpired(false);
        apiKeyRepository.save(key);

        return apiKey;
    }

    public String getValidApiKey() {
        LocalDateTime now = LocalDateTime.now();
        ApiKey existingKey = apiKeyRepository.findLatestValidKey(now);

        if (existingKey != null && !existingKey.getExpired()) {
            return existingKey.getKey();
        } else {
            return generateUniqueApiKey();
        }
    }

    public boolean isValidApiKey(String apiKey) {
        LocalDateTime now = LocalDateTime.now();
        Optional<ApiKey> apiKeyOptional = apiKeyRepository.findByKey(apiKey);

        if (apiKeyOptional.isPresent()) {
            ApiKey key = apiKeyOptional.get();
            if (key.getExpirationDate().isBefore(now) || key.getExpired()) {
                key.setExpired(true);
                apiKeyRepository.save(key);
                apiKeyRepository.delete(key);

                generateUniqueApiKey();
                return false;
            }

            return !key.getExpired() && key.getExpirationDate().isAfter(now);
        }

        return false;
    }

    private String generateApiKey() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}