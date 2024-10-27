package com.management.ManagementInventaris.product;

import com.management.ManagementInventaris.categories.Categories;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.product.coupon.Coupon;
import com.management.ManagementInventaris.product.variant.VariantInfo;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserConverter;
import com.management.ManagementInventaris.utils.Cryptographic;
import com.management.ManagementInventaris.utils.CurrencyFormatter;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public interface IProductService {

    /**
     * Creates a new product with the given request and file list.
     *
     * @param request the product request containing product details
     * @param files the list of multipart files to be associated with the product
     * @return the created {@link ProductResponse}
     */
    ProductResponse createProduct(ProductRequest request, List<MultipartFile> files);

    /**
     * Updates an existing product identified by its ID with the given request and file list.
     *
     * @param productId the ID of the product to be updated
     * @param request the product request containing updated product details
     * @param files the list of multipart files to be associated with the product
     * @return the updated {@link ProductResponse}
     */
    ProductResponse updateProduct(String productId, ProductRequest request, List<MultipartFile> files);

    /**
     * Deletes a product identified by its ID.
     *
     * @param productId the ID of the product to be deleted
     */
    void delete(String productId);

    /**
     * Retrieves a paginated list of all products.
     *
     * @param page the page number to retrieve
     * @param size the number of products per page
     * @return a {@link WebResponse} containing a list of {@link ProductResponse} objects
     */
    WebResponse<List<ProductResponse>> findAllProduct(int page, int size, User user);

    /**
     * Searches for products based on the specified search request.
     *
     * @param request the search request containing search criteria
     * @return a list of {@link ProductResponse} objects matching the search criteria
     */
    List<ProductResponse> search(SearchProductRequest request, User user);

    /**
     * Retrieves a list of products by category name.
     *
     * @param categoryName the name of the category to filter products by
     * @return a list of {@link ProductResponse} objects belonging to the specified category
     */
    List<ProductResponse> findProductsByCategory(String categoryName, User user);

    /**
     * Toggles the rating (like or dislike) of a product by the currently authenticated user.
     *
     * @param productId the ID of the product to toggle the rating for
     * @param increment true to like the product, false to dislike it
     * @return the updated {@link Product}
     */
    Product toggleRating(String productId, boolean increment);

    /**
     * Retrieves a product by its ID and returns the corresponding product response.
     *
     * @param id the ID of the product to retrieve
     * @return the {@link ProductResponse} object representing the product
     */
    ProductResponse getProductById(String id, User user);

    /**
     * Retrieves products uploaded by a specific user and returns a list of product responses.
     *
     * @param userId the ID of the user whose uploaded products are to be retrieved
     * @return a list of {@link ProductResponse} objects representing the products uploaded by the user
     */
    List<ProductResponse> getProductsUploadedByUser(String userId, User user);

    /**
     * Retrieves the cheapest products based on a keyword and a maximum price, and returns a list of product responses.
     *
     * @param keyword the keyword to search for in product titles, descriptions, and categories
     * @param maxPrice the maximum price of the products to be retrieved
     * @return a list of {@link ProductResponse} objects representing the cheapest products
     */
    List<ProductResponse> getCheapestProducts(String keyword, BigDecimal maxPrice);

    /**
     * Checks the stock levels of products and sends notification emails if the quantity is below a certain threshold.
     *
     * <p>This method is scheduled to run daily at 8 AM using a cron expression.</p>
     */
    void checkStockAndNotify();

    boolean isCouponValid(Coupon coupon);

    /**
     * Updates the rating of a product by incrementing or decrementing the rating value.
     *
     * <p>This method adjusts the product's rating string by updating the last component based on the provided flag.</p>
     *
     * @param product the product to update the rating for
     * @param increment true to increment the rating, false to decrement it
     */
    default void updateProductRating(Product product, boolean increment) {
        String[] ratings = product.getRating().split("\\.");
        int lastIndex = ratings.length - 1;

        if (increment) {
            ratings[lastIndex] = String.valueOf(Integer.parseInt(ratings[lastIndex]) + 1);
        } else {
            ratings[lastIndex] = String.valueOf(Math.max(0, Integer.parseInt(ratings[lastIndex]) - 1));
        }

        product.setRating(String.join(".", ratings));
    }

    /**
     * Deletes the image files and barcode associated with the given product from MinIO storage.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *     <li>Retrieves the list of image URLs associated with the product.</li>
     *     <li>Constructs the full path for each image and deletes it from the MinIO bucket.</li>
     *     <li>Constructs the path for the product's barcode and deletes it from the MinIO bucket.</li>
     * </ol>
     *
     * <p>If any operation fails, an exception is caught and rethrown as a runtime exception with an appropriate error message.</p>
     *
     * @param product the product whose associated image files and barcode are to be deleted
     * @throws RuntimeException if an error occurs during the deletion of image files or barcode from MinIO
     */
    default void deleteFileFromMinIO(Product product) {
        try {
            final MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9001")
                    .credentials("w53sVDQLEi8J8gJW5xYZ", "rJz0Ck9BKKRJplk4o923RILI1we2iyr4Ibosdqhy")
                    .build();

            final String bucketName = "inventaris";

            List<String> objectNames = product.getImageUrls();
            for (String objectName: objectNames) {
                String fullPath = "/uploaded/product/" + objectName;
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fullPath)
                        .build());
            }
            String barcodePath = "barcodes/" + product.getBarcodeUrl();
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(barcodePath)
                    .build());

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from MinIO: " + e.getMessage());
        }
    }

    /**
     * Returns the formatted rating of a product, displaying only the first two components of the rating string.
     *
     * @param product the product to get the formatted rating for
     * @return the formatted rating string
     */
    default String getFormattedRating(Product product) {
        String[] ratings = product.getRating().split("\\.");
        return ratings.length > 1? ratings[0] + "." + ratings[1] : ratings[0];
    }

    default BigDecimal applyDiscount(BigDecimal originalPrice, Coupon coupon) {
        switch (coupon.getDiscountType()) {
            case PERCENTAGE ->  {
                return originalPrice.multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100))));
            }
            case BUY_ONE_GET_ONE -> {
                return originalPrice.divide(BigDecimal.valueOf(2));
            }
            case FIXED_AMOUNT -> {
                return originalPrice.subtract(BigDecimal.valueOf(coupon.getDiscountValue()));
            }
            default -> {
                return originalPrice;
            }
        }
    }

    /**
     * Converts a Product entity to a ProductResponse DTO.
     * <p>
     * This method performs the following operations:
     * 1. Retrieves the list of categories from the Product entity.
     * 2. Builds a ProductResponse object using the builder pattern with the following fields:
     * - id: The unique identifier of the product.
     * - units: A list of unit strings converted from the product's units.
     * - title: The title of the product.
     * - description: The description of the product.
     * - price: The price of the product converted to a different salary format.
     * - quantity: The available quantity of the product.
     * - details: Additional details about the product.
     * - rating: The formatted rating of the product.
     * - categories: A list of category names extracted from the product's categories.
     * - uploadedBy: The email of the user who uploaded the product.
     *
     * @param product The Product entity to be converted.
     * @return A ProductResponse DTO containing the product's information.
     */
    @SneakyThrows
    default ProductResponse toProductResponse(Product product) {
        List<Categories> categories = product.getCategories();

        List<String> fileUrls = product.getImageUrls().stream()
                .map(filename -> "http://localhost/api/minio/download/uploaded/product/" + filename)
                .toList();

        String shareLink = "http://localhost/api/v1/products/findById/" + Cryptographic.encrypt(product.getId());

        String barcodeUrl = "http://localhost/api/minio/download/barcodes/" + product.getId() + ".png";

        ProductResponse.ProductResponseBuilder responseBuilder = ProductResponse.builder()
                .id(Cryptographic.encrypt(product.getId()))
                .units(product.getUnits().stream().map(Unit::toString).toList())
                .title(product.getTitle())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .viewers(product.getViewers())
                .rating(getFormattedRating(product))
                .variants(product.getVariants().stream()
                        .map(variant -> VariantInfo.builder()
                                .name(variant.getName())
                                .price(variant.getPrice())
                                .formattedPrice(CurrencyFormatter.formatIDR(variant.getPrice()))
                                .build())
                        .toList())
                .categories(categories.stream().map(Categories::getCategoryName).collect(Collectors.toList()))
                .imageUrls(fileUrls)
                .barcodeProduct(barcodeUrl)
                .shareLink(shareLink)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .timezoneLabel(product.getTimezoneLabel())
                .uploadedBy(UserConverter.toUserProfile(product.getUploadedBy()));

        if (product.getPrice() != null) {
            responseBuilder
                    .price(product.getPrice())
                    .priceRange(product.getPriceRange())
                    .formattedPrice(CurrencyFormatter.formatIDR(product.getPrice()));
        }

        return responseBuilder.build();
    }
}