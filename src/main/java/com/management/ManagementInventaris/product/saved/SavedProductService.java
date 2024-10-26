package com.management.ManagementInventaris.product.saved;

import com.management.ManagementInventaris.product.Product;
import com.management.ManagementInventaris.product.ProductRepository;
import com.management.ManagementInventaris.product.SavedProduct;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserRepository;
import com.management.ManagementInventaris.utils.UserDetailToken;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Service
@Slf4j
public class SavedProductService {

    @Autowired
    private SavedProductRepository savedProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private UserDetailToken userDetailToken;

    /**
     * Creates a new saved product for the user associated with the provided JWT token.
     *
     * @param productId The ID of the product to be saved.
     * @return A {@link SavedProductResponse} containing the saved product or validation errors.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an input or output error occurs.
     */
    @Transactional
    public SavedProductResponse createSavedProduct(String productId) {
        User user = userDetailToken.dataUserEmail();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        SavedProduct savedProduct = new SavedProduct();
        savedProduct.setUser(user);
        savedProduct.setProduct(product);

        Set<ConstraintViolation<SavedProduct>> violations = validator.validate(savedProduct);
        if (!violations.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            for (ConstraintViolation<SavedProduct> violation : violations) {
                error.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return SavedProductResponse.builder()
                    .status(false)
                    .errors(error)
                    .build();
        }

        SavedProduct saved = savedProductRepository.save(savedProduct);
        return toSavedProductResponse(saved);
    }

    /**
     * Deletes a saved product from the user's saved products list.
     *
     * @param productId The ID of the product to be deleted.
     * @throws IllegalArgumentException If the saved product is not found for the user and product ID.
     */
    @Transactional(readOnly = true)
    public void deleteSavedProductFromUser(String productId) {
        User user = userDetailToken.dataUserEmail();

        Optional<SavedProduct> savedProduct = savedProductRepository.findByProductId(productId);
        if (!user.getEmail().equals(savedProduct.get().getUser().getEmail())) throw new IllegalStateException("Product saved with User Not Found!");
        if (savedProduct.isPresent()) {
            savedProductRepository.delete(savedProduct.get());
        } else {
            throw new IllegalArgumentException("Saved product not found for user and product id");
        }
    }

    /**
     * Retrieves a list of saved products for the user associated with the provided JWT token.
     *
     * @return A list of {@link SavedProductResponse} objects containing the saved products.
     */
    @Transactional(readOnly = true)
    public List<SavedProductResponse> getSavedProductForUser() {
        User user = userDetailToken.dataUserEmail();

        List<SavedProduct> savedProducts = savedProductRepository.findByUserId(user.getId());
        List<SavedProductResponse> responses = new ArrayList<>();
        for (SavedProduct savedProduct : savedProducts) {
            responses.add(toSavedProductResponse(savedProduct));
        }
        return responses;
    }

    private SavedProductResponse toSavedProductResponse(SavedProduct savedProduct) {
        return SavedProductResponse.builder()
                .status(true)
                .product(savedProduct.getProduct())
                .user(savedProduct.getUser())
                .build();
    }
}