package com.management.ManagementInventaris.product.saved;

import com.management.ManagementInventaris.exception.AuthorizationException;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing saved products.
 *
 * @author Dearly Febriano Irwansyah
 * @since 4.0
 */
@RestController
@RequestMapping("/api/saved-products")
public class SavedProductController {

    @Autowired
    private SavedProductService savedProductService;

    /**
     * Handles a POST request to save a product for the current user.
     *
     * @param productId The ID of the product to be saved.
     * @return A ResponseEntity with the status code and body.
     * @throws ServletException If a servlet-related error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @PostMapping(path = "/save/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savedProduct(@RequestParam String productId) throws ServletException, IOException {
        try {
            SavedProductResponse response = savedProductService.createSavedProduct(productId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof AuthorizationException) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                errorMessage = "Unauthorized Request!";
            } else if (e instanceof ResponseStatusException) {
                ResponseStatusException ex = (ResponseStatusException) e;
                httpStatus = HttpStatus.valueOf(ex.getStatusCode().value());
                errorMessage = ex.getReason();
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                errorMessage = "Internal Server Error!";
            }
            return ResponseEntity.status(httpStatus).body(Map.of("status", false, "error", Map.of("code", httpStatus.value(), "message", errorMessage)));
        }
    }

    /**
     * Handles a GET request to retrieve the saved products for the current user.
     *
     * @return A ResponseEntity with the status code and body.
     */
    @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getSavedProducts() {
        try {
            List<SavedProductResponse> responses = savedProductService.getSavedProductForUser();
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", true, "data", responses));
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof AuthorizationException) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                errorMessage = "Unauthorized Request!";
            } else if (e instanceof AccessDeniedException) {
                httpStatus = HttpStatus.FORBIDDEN;
                errorMessage = "Access Denied!";
            } else if (e instanceof ResponseStatusException) {
                ResponseStatusException ex = (ResponseStatusException) e;
                httpStatus = HttpStatus.valueOf(ex.getStatusCode().value());
                errorMessage = ex.getReason();
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                errorMessage = "Internal Server Error!";
            }
            return ResponseEntity.status(httpStatus).body(Map.of("status", false, "error", Map.of("code", httpStatus.value(), "message", errorMessage)));
        }
    }

    /**
     * Handles a DELETE request to delete a saved product for the current user.
     *
     * @param productId The ID of the product to be deleted.
     * @return A ResponseEntity with the status code.
     */
    @DeleteMapping(path = "/delete/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteSavedProduct(@RequestParam String productId) {
        try {
            savedProductService.deleteSavedProductFromUser(productId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}