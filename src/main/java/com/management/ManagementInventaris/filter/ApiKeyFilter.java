package com.management.ManagementInventaris.filter;

import com.management.ManagementInventaris.key.ApiKey;
import com.management.ManagementInventaris.key.ApiKeyRepository;
import com.management.ManagementInventaris.key.ApiKeyService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Setter
@Component
public class ApiKeyFilter implements Filter {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private ApiKeyService apiKeyService;

    private static final List<String> URL_PATTERNS = Arrays.asList(
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**"
    );

    /**
     * Called by the web container to indicate to a filter that it is being placed into service. The servlet container
     * calls the init method exactly once after instantiating the filter. The init method must complete successfully
     * before the filter is asked to do any filtering work.
     * <p>
     * The web container cannot place the filter into service if the init method either:
     * <ul>
     * <li>Throws a ServletException</li>
     * <li>Does not return within a time period defined by the web container</li>
     * </ul>
     * The default implementation is a NO-OP.
     *
     * @param filterConfig The configuration information associated with the filter instance being initialised
     * @throws ServletException if the initialisation fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /**
     * The <code>doFilter</code> method of the Filter is called by the container each time a request/response pair is
     * passed through the chain due to a client request for a resource at the end of the chain. The FilterChain passed
     * in to this method allows the Filter to pass on the request and response to the next entity in the chain.
     * <p>
     * A typical implementation of this method would follow the following pattern:- <br>
     * 1. Examine the request<br>
     * 2. Optionally wrap the request object with a custom implementation to filter content or headers for input
     * filtering <br>
     * 3. Optionally wrap the response object with a custom implementation to filter content or headers for output
     * filtering <br>
     * 4. a) <strong>Either</strong> invoke the next entity in the chain using the FilterChain object
     * (<code>chain.doFilter()</code>), <br>
     * 4. b) <strong>or</strong> not pass on the request/response pair to the next entity in the filter chain to block
     * the request processing<br>
     * 5. Directly set headers on the response after invocation of the next entity in the filter chain.
     *
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this filter to pass the request and response
     *                 to for further processing
     * @throws IOException      if an I/O error occurs during this filter's processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String servletPath = httpRequest.getServletPath();

        if (matchesAnyPattern(servletPath) || servletPath.startsWith("/api/minio/download/")) {
            chain.doFilter(request, response);
            return;
        }

        String apiKey = httpRequest.getHeader("X-API-KEY");

        if (apiKey == null || !isValidApiKey(apiKey, httpResponse)) {
            if (apiKey != null) {
                Optional<ApiKey> optionalApiKey = apiKeyRepository.findByKey(apiKey);
                if (optionalApiKey.isPresent()) {
                    ApiKey key = optionalApiKey.get();
                    if (key.getExpirationDate().isBefore(LocalDateTime.now())) {
                        key.setExpired(true);
                        apiKeyRepository.save(key);
                        apiKeyRepository.delete(key);

                        apiKeyService.generateUniqueApiKey();
                    }
                }
            }
            httpResponse.setContentType("text/html");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.sendRedirect("https://unauthorized-api.vercel.app/");
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * Called by the web container to indicate to a filter that it is being taken out of service. This method is only
     * called once all threads within the filter's doFilter method have exited or after a timeout period has passed.
     * After the web container calls this method, it will not call the doFilter method again on this instance of the
     * filter. <br>
     * <br>
     * This method gives the filter an opportunity to clean up any resources that are being held (for example, memory,
     * file handles, threads) and make sure that any persistent state is synchronized with the filter's current state in
     * memory. The default implementation is a NO-OP.
     */
    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private boolean isValidApiKey(String apiKey, HttpServletResponse response) throws IOException {
        Optional<ApiKey> optionalApiKey = apiKeyRepository.findByKey(apiKey);
        if (optionalApiKey.isPresent()) {
            ApiKey key = optionalApiKey.get();
            if (!key.getExpired() && key.getExpirationDate().isAfter(LocalDateTime.now())) {
                return true;
            } else {
                response.setContentType("text/html");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("API key expired");
                key.setExpired(true);
                return false;
            }
        }
        return false;
    }

    private boolean matchesAnyPattern(String servletPath) {
        return URL_PATTERNS.stream().anyMatch(servletPath::startsWith);
    }
}