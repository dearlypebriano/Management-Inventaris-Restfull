package com.management.ManagementInventaris.filter;

import com.management.ManagementInventaris.key.ApiKey;
import com.management.ManagementInventaris.key.ApiKeyRepository;
import com.management.ManagementInventaris.key.ApiKeyService;
import com.management.ManagementInventaris.email.EmailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ApiKeyFilterTest {

    @InjectMocks
    private ApiKeyFilter apiKeyFilter;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ApiKeyService apiKeyService;

    @Mock
    private EmailService emailService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoFilter_withExpiredApiKey_shouldGenerateNewApiKeyAndSendEmail() throws IOException, ServletException {

        emailService.sendApiKeyToEmail(newApiKey);
    }
}