package com.management.ManagementInventaris.product;

import com.management.ManagementInventaris.exception.AuthorizationException;
import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.utils.Cryptographic;
import com.management.ManagementInventaris.utils.UserDetailToken;
import io.minio.MinioClient;
import jakarta.servlet.ServletException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private UserDetailToken userDetailToken;

    @Autowired
    private ProductService productService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * Create a new product.
     *
     * @param request the product request containing the product details
     * @return ResponseEntity containing the created product response
     * @throws ServletException in case of a servlet exception
     * @throws IOException in case of an IO exception
     *
     */
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @Valid @ModelAttribute ProductRequest request,
            @RequestParam("file") List<MultipartFile> files
    ) {
        try {
            ProductResponse response = productService.createProduct(request, files);
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
     * Update an existing product.
     *
     * @param productId the ID of the product to be updated
     * @param request the product request containing the updated product details
     * @return ResponseEntity containing the updated product response
     *
     */
    @PatchMapping(path = "/update/{productId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productId,
            @ModelAttribute ProductRequest request,
            @RequestParam(value = "file", required = false) List<MultipartFile> files
    ) {
        String decryptedProductId;
        try {
            decryptedProductId = Cryptographic.decrypt(productId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        ProductResponse response = productService.updateProduct(decryptedProductId, request, files);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Toggles the star rating of a product.
     *
     * @param productId The ID of the product to toggle the rating for.
     * @return ResponseEntity containing the updated ProductResponse after toggling the rating.
     */
    @PostMapping(path = "/{productId}/toggleRating")
    public ResponseEntity<ProductResponse> toggleRating(@PathVariable String productId, @RequestParam boolean increment) {
        Product updateProduct = productService.toggleRating(productId, increment);
        ProductResponse response = productService.toProductResponse(updateProduct);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a product from the inventory.
     *
     * @param productId The ID of the product to delete.
     * @return ResponseEntity with status NO_CONTENT if successful.
     */
    @DeleteMapping(path = "/delete/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String productId
    ) {
        String decryptedProductId;
        try {
            decryptedProductId = Cryptographic.decrypt(productId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        productService.delete(decryptedProductId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Retrieves a paginated list of all products.
     *
     * @param page The page number for pagination. The default value is 0.
     * @param size The page size for pagination. The default value is 10.
     * @return ResponseEntity containing a {@link WebResponse} object.
     *         The {@link WebResponse} contains a list of {@link ProductResponse} objects and pagination information.
     *         The HTTP status code is set to OK (200).
     */
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<ProductResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = userDetailToken.dataUserEmail();
        WebResponse<List<ProductResponse>> response = productService.findAllProduct(page, size, user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/findById/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
        String decryptedProductId;
        try {
            decryptedProductId = Cryptographic.decrypt(productId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        User user = userDetailToken.dataUserEmail();
        ProductResponse productResponse = productService.getProductById(decryptedProductId, user);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    /**
     * Searches for products based on the provided search criteria.
     *
     * @param title The title of the product to search for. If not provided, all products will be considered.
     * @param description The description of the product to search for. If not provided, all products will be considered.
     * @param category The category of the product to search for. If not provided, all products will be considered.
     * @param page The page number for pagination. The default value is 0.
     * @param size The page size for pagination. The default value is 10.
     * @return A {@link WebResponse} object containing a list of {@link ProductResponse} objects that match the search criteria.
     *         The {@link WebResponse} also includes pagination information.
     */
    @GetMapping(path = "/search/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<ProductResponse>>> searchProducts(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ) {
        User user = userDetailToken.dataUserEmail();
        SearchProductRequest productRequest = SearchProductRequest.builder()
                .page(page)
                .size(size)
                .title(title)
                .description(description)
                .category(category)
                .build();
        List<ProductResponse> responses = productService.search(productRequest, user);
        WebResponse<List<ProductResponse>> response = WebResponse.<List<ProductResponse>>builder()
                .data(responses)
                .paging(PagingResponse.builder()
                        .currentPage(page)
                        .totalPage((responses.size() + size - 1) / size)
                        .size(size)
                        .build())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Searches for products belonging to a specific category.
     *
     * @param categoryName The name of the category to search for.
     * @return ResponseEntity containing a List of ProductResponse objects belonging to the specified category.
     */
    @GetMapping(path = "/search/productsByCategory/{categoryName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponse>> searchProductsByCategory(@PathVariable String categoryName) {
        User user = userDetailToken.dataUserEmail();
        List<ProductResponse> productResponses = productService.findProductsByCategory(categoryName, user);
        return ResponseEntity.status(HttpStatus.OK).body(productResponses);
    }

    /**
     * Triggers a check for low stock products and sends notifications if necessary.
     *
     * @return ResponseEntity with status OK.
     */
    @GetMapping("/checkStock")
    public ResponseEntity<Void> checkStockAndNotify() {
        productService.checkStockAndNotify();
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves a list of products uploaded by the currently authenticated user.
     *
     * @return ResponseEntity containing a List of ProductResponse objects.
     *         If an error occurs during the retrieval process, a ResponseEntity with an appropriate error status and message is returned.
     */
    @GetMapping(path = "/getProductsWithUser/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<?>> getProductsUploadedByUser(@RequestParam("userId") String userId) {
        User user = userDetailToken.dataUserEmail();
        try {
            List<ProductResponse> productResponses = productService.getProductsUploadedByUser(userId, user);
            return ResponseEntity.status(HttpStatus.OK).body(productResponses);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof ResponseStatusException) {
                ResponseStatusException ex = (ResponseStatusException) e;
                httpStatus = HttpStatus.valueOf(ex.getStatusCode().value());
                errorMessage = ex.getReason();
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                errorMessage = "Internal Server Error!";
            }
            Map<String, Object> errorResponse = Map.of(
                    "status", false,
                    "error", Map.of(
                            "code", httpStatus.value(),
                            "message", errorMessage
                    )
            );
            return ResponseEntity.status(httpStatus).body(Collections.singletonList(errorResponse));
        }
    }

    /**
     * Retrieves a list of the cheapest products based on their price.
     *
     * @param maxPrice The maximum price for the products to be included in the result.
     *                 If not provided, the default value is set to infinity.
     * @return ResponseEntity containing a List of ProductResponse objects.
     *         Each ProductResponse represents a product with its details.
     *         The list is sorted in ascending order based on the product price.
     *         If no products are found or an error occurs during the retrieval process,
     *         a ResponseEntity with an appropriate error status and message is returned.
     */
    @GetMapping(path = "/search/price/cheap", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponse>> searchCheapestProducts(
            @RequestParam(name = "mp", defaultValue = "Infinity") BigDecimal maxPrice,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        List<ProductResponse> cheapestProducts = productService.getCheapestProducts(keyword, maxPrice);
        return ResponseEntity.ok(cheapestProducts);
    }
}
