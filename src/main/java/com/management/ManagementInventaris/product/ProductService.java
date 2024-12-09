package com.management.ManagementInventaris.product;

import com.management.ManagementInventaris.barcode.BarcodeService;
import com.management.ManagementInventaris.categories.Categories;
import com.management.ManagementInventaris.categories.CategoriesRepository;
import com.management.ManagementInventaris.email.EmailDetails;
import com.management.ManagementInventaris.email.EmailService;
import com.management.ManagementInventaris.exception.AuthorizationException;
import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.order.OrderRepository;
import com.management.ManagementInventaris.order.cart.CartRepository;
import com.management.ManagementInventaris.order.history.OrderHistoryRepository;
import com.management.ManagementInventaris.product.coupon.Coupon;
import com.management.ManagementInventaris.product.coupon.CouponRepository;
import com.management.ManagementInventaris.product.coupon.UserCoupon;
import com.management.ManagementInventaris.product.coupon.UserCouponRepository;
import com.management.ManagementInventaris.product.variant.Variant;
import com.management.ManagementInventaris.product.variant.VariantInfo;
import com.management.ManagementInventaris.product.variant.VariantRequest;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserRepository;
import com.management.ManagementInventaris.utils.*;
import static com.management.ManagementInventaris.utils.CurrencyFormatter.formatIDR;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.persistence.criteria.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService implements IProductService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private UserDetailToken userDetailToken;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DeleteBarcodeFromMinio deleteBarcode;

    @Value("${minio.bucketName}")
    private String bucketName;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final int MINIMUM_VOTE_THRESHOLD = 10;

    /**
     * Creates a new product with the specified details and associated images.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *     <li>Retrieves the user details from the token.</li>
     *     <li>Validates and retrieves the categories from the request. Throws an exception if a category is not found.</li>
     *     <li>Validates and converts unit strings to {@link Unit} enum values. Throws an exception for invalid units.</li>
     *     <li>Uploads each provided image file to a MinIO bucket and generates a list of image URLs. Throws an exception if image upload fails.</li>
     *     <li>Determines the time zone ID based on the user's province.</li>
     *     <li>Creates a {@link Product} entity with the provided details, categories, units, and image URLs.</li>
     *     <li>Processes variant requests, calculates the minimum and maximum variant prices, and sets the product's price range and price. If no variants are provided, sets the product price range to the product's price.</li>
     *     <li>Saves the product entity to the repository.</li>
     *     <li>Generates a QR code for the product's barcode, uploads it to MinIO, and updates the product entity with the barcode URL.</li>
     *     <li>Saves the updated product entity to the repository again.</li>
     *     <li>Caches the product DTO in Redis with a key based on the product ID.</li>
     * </ol>
     *
     * <p>If any operation fails, an exception is caught, logged, and rethrown as a runtime exception with an appropriate error message.</p>
     *
     * @param request the details of the product to be created, including title, price, quantity, description, categories, and units
     * @param files a list of image files to be associated with the product
     * @return a {@link ProductResponse} containing the details of the newly created product
     * @throws RuntimeException if an error occurs during product creation, category retrieval, unit validation, image upload, or barcode generation
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    @CachePut(value = "products", key = "#result.id")
    public ProductResponse createProduct(ProductRequest request, List<MultipartFile> files) {
        try {
            User user = userDetailToken.dataUserEmail();

            List<Categories> categoryList = new ArrayList<>();
            for (String categoryName : request.getCategories()) {
                Categories existingCategory = categoriesRepository.findByCategoryName(categoryName);
                if (existingCategory != null) {
                    categoryList.add(existingCategory);
                } else {
                    throw new RuntimeException("Category dengan nama : " + categoryName + " tidak ditemukan!");
                }
            }

            List<String> validUnits = Arrays.stream(Unit.values())
                    .map(Enum::name)
                    .toList();

            List<Unit> unitList = new ArrayList<>();
            for (String unit : request.getUnits()) {
                if (!validUnits.contains(unit)) {
                    throw new RuntimeException("Unit " + unit + " tidak valid");
                }
                unitList.add(Unit.valueOf(unit));
            }

            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                try {
                    String hashedFileName = ImageCompressor.hashFileName(file.getOriginalFilename(), file.getBytes());
                    String objectName = "uploaded/product/" + hashedFileName;
                    InputStream inputStream = file.getInputStream();
                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .build());
                    imageUrls.add(hashedFileName);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload image: " + e.getMessage());
                }
            }

            Product product = Product.builder()
                    .id(UUID.randomUUID().toString())
                    .title(request.getTitle())
                    .rating(0.0)
                    .price(request.getPrice())
                    .quantity(request.getQuantity())
                    .description(request.getDescription())
                    .categories(categoryList)
                    .units(unitList)
                    .imageUrls(!imageUrls.isEmpty() ? imageUrls : new ArrayList<>())
                    .uploadedBy(user)
                    .build();

            List<VariantRequest> variantRequests = request.getVariants();
            List<Variant> variants = new ArrayList<>();
            if (variantRequests != null && !variantRequests.isEmpty()) {
                for (VariantRequest variantRequest : variantRequests) {
                    Variant variant = Variant.builder()
                            .id(UUID.randomUUID().toString())
                            .name(variantRequest.getName())
                            .price(variantRequest.getPrice())
                            .product(product)
                            .build();
                    variants.add(variant);
                }

                BigDecimal minPrice = variants.stream()
                        .map(Variant::getPrice)
                        .min(BigDecimal::compareTo)
                        .orElseThrow(() -> new RuntimeException("Tidak ada harga variant yang ditemukan"));

                BigDecimal maxPrice = variants.stream()
                        .map(Variant::getPrice)
                        .max(BigDecimal::compareTo)
                        .orElseThrow(() -> new RuntimeException("Tidak ada harga variant yang ditemukan"));

                String formattedMinPrice = formatIDR(minPrice);
                String formattedMaxPrice = formatIDR(maxPrice);
                String formattedPriceRange = formattedMinPrice + " - " + formattedMaxPrice;

                product.setVariants(variants);
                product.setPriceRange(formattedPriceRange);
                product.setPrice(minPrice);
            } else {
                String formattedPrice = formatIDR(product.getPrice());
                product.setPriceRange(formattedPrice);
                product.setPrice(request.getPrice());
            }

            productRepository.save(product);

            byte[] barcodeImage = barcodeService.getQRCodeImage(product.getId());
            if (barcodeImage == null) throw new RuntimeException("Failed to generate barcode image");

            barcodeService.uploadBarcodeToMinio(product.getId(), barcodeImage);
            product.setBarcodeUrl(product.getId() + ".png");
            productRepository.save(product);

            ProductDTO productDTO = ProductDTO.fromEntity(product);
            redisTemplate.opsForValue().set("product:" + productDTO.getId(), productDTO);
            return toProductResponse(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Internal Server Error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing product with the specified details and associated images.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *     <li>Retrieves the user details from the token.</li>
     *     <li>Finds the product by its ID and checks if the user is authorized to update it.</li>
     *     <li>Validates and updates units if provided in the request. Throws an exception for invalid units.</li>
     *     <li>Updates the product's title, price, quantity, and description if provided in the request.</li>
     *     <li>Processes and updates variant requests, calculates the minimum and maximum variant prices, and sets the product's price range and price.</li>
     *     <li>Validates and updates categories if provided in the request. Throws an exception if a category is not found.</li>
     *     <li>Uploads each provided image file to a MinIO bucket and updates the product's image URLs. Throws an exception if image upload fails.</li>
     *     <li>Updates the product's timestamps based on the provided time zone.</li>
     *     <li>Deletes the existing barcode from MinIO, generates a new QR code for the product's barcode, uploads it to MinIO, and updates the product entity with the new barcode URL.</li>
     *     <li>Saves the updated product entity to the repository.</li>
     *     <li>Caches the product DTO in Redis with a key based on the product ID.</li>
     * </ol>
     *
     * <p>If any operation fails, an exception is caught, logged, and rethrown as a runtime exception with an appropriate error message.</p>
     *
     * @param productId the ID of the product to be updated
     * @param request the details of the product to be updated, including title, price, quantity, description, categories, and units
     * @param files a list of image files to be associated with the product
     * @return a {@link ProductResponse} containing the details of the updated product
     * @throws RuntimeException if an error occurs during product update, category retrieval, unit validation, image upload, or barcode generation
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    @CachePut(value = "products", key = "#result.id")
    public ProductResponse updateProduct(String productId, ProductRequest request, List<MultipartFile> files) {
        try {
            User user = userDetailToken.dataUserEmail();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product dengan ID : " + productId + " tidak ditemukan!"));

            if (!product.getUploadedBy().getEmail().equals(user.getEmail())) {
                throw new AuthorizationException("Anda tidak diizinkan untuk mengupdate produk ini");
            }

            if (request.getUnits() != null) {
                List<String> validUnits = Arrays.stream(Unit.values())
                        .map(Enum::name)
                        .toList();
                List<Unit> unitList = new ArrayList<>();
                for (String unit : request.getUnits()) {
                    if (!validUnits.contains(unit)) {
                        throw new RuntimeException("Unit " + unit + " tidak valid");
                    }
                    unitList.add(Unit.valueOf(unit));
                }
                product.setUnits(unitList);
            }
            if (request.getTitle() != null) {
                product.setTitle(request.getTitle());
            }
            if (request.getPrice() != null) {
                product.setPrice(request.getPrice());
            }
            if (request.getQuantity() != null) {
                product.setQuantity(request.getQuantity());
            }
            if (request.getDescription() != null) {
                product.setDescription(request.getDescription());
            }
            if (request.getVariants() != null) {
                List<Variant> existingVariants = product.getVariants();
                if (existingVariants == null) {
                    existingVariants = new ArrayList<>();
                    product.setVariants(existingVariants);
                }
                for (int i = 0; i < request.getVariants().size(); i++) {
                    VariantRequest variantRequest = request.getVariants().get(i);
                    if (variantRequest == null || variantRequest.getName() == null || variantRequest.getPrice() == null) {
                        continue;
                    }
                    if (i < existingVariants.size()) {
                        Variant existingVariant = existingVariants.get(i);
                        if (existingVariant != null) {
                            existingVariant.setName(variantRequest.getName());
                            existingVariant.setPrice(variantRequest.getPrice());
                        }
                    } else {
                        Variant newVariant = Variant.builder()
                                .id(UUID.randomUUID().toString())
                                .name(variantRequest.getName())
                                .price(variantRequest.getPrice())
                                .product(product)
                                .build();
                        existingVariants.add(newVariant);
                    }
                }
                if (!existingVariants.isEmpty()) {
                    BigDecimal minPrice = existingVariants.stream()
                            .map(Variant::getPrice)
                            .filter(Objects::nonNull)
                            .min(BigDecimal::compareTo)
                            .orElseThrow(() -> new RuntimeException("Tidak ada harga varian yang ditemukan"));
                    BigDecimal maxPrice = existingVariants.stream()
                            .map(Variant::getPrice)
                            .filter(Objects::nonNull)
                            .max(BigDecimal::compareTo)
                            .orElseThrow(() -> new RuntimeException("Tidak ada harga varian yang ditemukan"));

                    String formattedMinPrice = formatIDR(minPrice);
                    String formattedMaxPrice = formatIDR(maxPrice);
                    String formattedPriceRange = formattedMinPrice + " - " + formattedMaxPrice;

                    product.setPriceRange(formattedPriceRange);
                    product.setPrice(minPrice);
                }
            }
            if (request.getCategories() != null) {
                List<Categories> categoriesList = new ArrayList<>();
                for (String categoryName : request.getCategories()) {
                    Categories existingCategory = categoriesRepository.findByCategoryName(categoryName);
                    if (existingCategory != null) {
                        categoriesList.add(existingCategory);
                    } else {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Category dengan nama : " + categoryName + " tidak ditemukan!");
                    }
                }
                product.setCategories(categoriesList);
            }
            if (files != null) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile file : files) {
                    try {
                        String hashedFileName = ImageCompressor.hashFileName(Objects.requireNonNull(file.getOriginalFilename()), file.getBytes());
                        InputStream inputStream = file.getInputStream();
                        String objectPath = "uploaded/product/" + hashedFileName;
                        minioClient.putObject(PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectPath)
                                .stream(inputStream, inputStream.available(), -1)
                                .build());
                        imageUrls.add(hashedFileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Failed to upload image: " + e.getMessage());
                    }
                }
                product.setImageUrls(imageUrls);
            }
            product.updateTimestampsWithZone();

            deleteBarcode.deleteBarcodeFromMinio(product.getBarcodeUrl());

            byte[] barcodeImage = barcodeService.getQRCodeImage(product.getId());
            if (barcodeImage == null) throw new RuntimeException("Failed to generate barcode image");

            barcodeService.uploadBarcodeToMinio(product.getId(), barcodeImage);
            product.setBarcodeUrl(product.getId() + ".png");

            productRepository.save(product);
            ProductDTO productDTO = ProductDTO.fromEntity(product);
            redisTemplate.opsForValue().set("product:" + productDTO.getId(), productDTO);
            return toProductResponse(product);
        } catch (Exception e) {
            throw new RuntimeException("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Deletes an existing product by its ID.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *     <li>Retrieves the user details from the token.</li>
     *     <li>Finds the product by its ID and checks if the user is authorized to delete it.</li>
     *     <li>Deletes the product's associated files from MinIO.</li>
     *     <li>Marks orders and order histories associated with the product as deleted.</li>
     *     <li>Deletes orders and order histories associated with the product.</li>
     *     <li>Deletes the product entity from the repository.</li>
     *     <li>Removes the product from the Redis cache.</li>
     * </ol>
     *
     * <p>If any operation fails, an exception is caught, logged, and rethrown as a runtime exception with an appropriate error message.</p>
     *
     * @param productId the ID of the product to be deleted
     * @throws RuntimeException if an error occurs during product deletion
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    @Override
    public void delete(String productId) {
        User user = userDetailToken.dataUserEmail();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product With ID : " + productId + " Not Found!"));

        if (!product.getUploadedBy().getEmail().equals(user.getEmail())) throw new AuthorizationException("User yang membuat produk ini bukan yang mengakses produk ini");

        deleteFileFromMinIO(product);

        orderRepository.markOrdersAsDeletedByOwner(product);
        orderHistoryRepository.markOrderHistoriesAsDeletedByOwner(product.getId());

        orderRepository.deleteOrdersByProduct(product);
        orderHistoryRepository.deleteOrderHistoriesByProduct(product.getId());

        productRepository.delete(product);

        String redisKey = "product:" + productId;
        Boolean isDeleted = redisTemplate.delete(redisKey);
        if (Boolean.FALSE.equals(isDeleted)) {
            log.warn("Produk dengan ID: {} tidak ditemukan di Redis atau gagal dihapus.", productId);
        } else {
            log.info("Produk dengan ID: {} berhasil dihapus dari Redis.", productId);
        }
    }

    /**
     * Retrieves a paginated list of products and constructs a response containing product details and paging information.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *     <li>Calculates the offset based on the provided page number and size.</li>
     *     <li>Fetches a list of products from the repository using pagination.</li>
     *     <li>Maps the list of products to a list of {@link ProductResponse} objects.</li>
     *     <li>Calculates the total number of pages based on the total product count and page size.</li>
     *     <li>Constructs a {@link PagingResponse} object with the current page, total pages, and size of the product responses.</li>
     *     <li>Builds and returns a {@link WebResponse} containing the list of product responses and the paging response.</li>
     * </ol>
     *
     * <p>If any operation fails, an exception is caught and rethrown as a runtime exception with an appropriate error message.</p>
     *
     * @param page the page number to be retrieved
     * @param size the number of products per page
     * @return a {@link WebResponse} containing a list of {@link ProductResponse} objects and paging information
     * @throws RuntimeException if an error occurs during product retrieval or mapping
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#user != null ? #user.id + '-' + #page + '-' + #size : 'guest-' + #page + '-' + #size")
    public WebResponse<List<ProductResponse>> findAllProduct(int page, int size, User user) {
        Optional<UserCoupon> optionalUserCoupon = (user != null) ? userCouponRepository.findActiveCouponByUserId(user.getId()) : Optional.empty();

        int offset = page * size;
        List<Product> products = productRepository.findAllWithPagination(offset, size);

        List<ProductResponse> productResponses = products.stream()
                .map(product -> createProductResponse(product, optionalUserCoupon))
                .toList();

        CalculatePages calculatePages = new CalculatePages(productRepository.count(), size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(calculatePages.calculateTotalPages())
                .size(productResponses.size())
                .build();

        return WebResponse.<List<ProductResponse>>builder()
                .data(productResponses)
                .paging(pagingResponse)
                .build();
    }

    /**
     * Searches for products based on the specified search criteria and returns a list of product responses.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *     <li>Builds a specification based on the provided search criteria in the {@link SearchProductRequest}.</li>
     *     <li>Fetches a paginated list of products from the repository using the specification and pagination information.</li>
     *     <li>Maps the list of products to a list of {@link ProductResponse} objects.</li>
     *     <li>Returns the list of product responses.</li>
     * </ol>
     *
     * <p>This method is cached using Spring's {@link Cacheable} annotation to improve performance for repeated queries.</p>
     *
     * @param request the search criteria encapsulated in a {@link SearchProductRequest} object
     * @return a list of {@link ProductResponse} objects matching the search criteria
     * @throws ResponseStatusException if no products are found for the specified criteria
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "T(String).valueOf(#user != null ? 'user-' + #user.id : 'guest') + '-search-' + #request.toString()")
    public List<ProductResponse> search(SearchProductRequest request, User user) {
        Specification<Product> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(request.getTitle())) {
                predicates.add(builder.like(builder.lower(root.get("title")), "%" + request.getTitle().toLowerCase() + "%"));
            }
            if (Objects.nonNull(request.getDescription())) {
                predicates.add(builder.like(builder.lower(root.get("description")), "%" + request.getDescription().toLowerCase() + "%"));
            }
            if (Objects.nonNull(request.getCategory())) {
                predicates.add(builder.like(builder.lower(root.get("category")), "%" + request.getCategory().toLowerCase() + "%"));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Product> products = productRepository.findAll(specification, pageable);

        Optional<UserCoupon> optionalUserCoupon = (user != null) ? userCouponRepository.findActiveCouponByUserId(user.getId()) : Optional.empty();
        return products.getContent().stream()
                .map(product -> toProductResponseWithDiscount(product, optionalUserCoupon))
                .toList();
    }

    /**
     * Finds products by the specified category name and returns a list of product responses.
     *
     * <p>This method performs the following operations:</p>
     * <ol>
     *     <li>Fetches a list of products from the repository that match the specified category name.</li>
     *     <li>Maps the list of products to a list of {@link ProductResponse} objects.</li>
     *     <li>Returns the list of product responses.</li>
     * </ol>
     *
     * <p>This method is cached using Spring's {@link Cacheable} annotation to improve performance for repeated queries.</p>
     *
     * @param categoryName the name of the category to search for products
     * @return a list of {@link ProductResponse} objects matching the specified category name
     * @throws ResponseStatusException if no products are found for the specified category name
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'findProductsByCategory:' + #categoryName + '-' + (#user != null ? #user.id : 'guest')")
    public List<ProductResponse> findProductsByCategory(String categoryName, User user) {
        List<Product> productList = productRepository.findByCategoryName(categoryName);
        if (productList.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with category " + categoryName + " not found!");

        Optional<UserCoupon> optionalUserCoupon = (user != null)
                ? userCouponRepository.findActiveCouponByUserId(user.getId())
                : Optional.empty();

        return productList.stream()
                .map(product -> toProductResponseWithDiscount(product, optionalUserCoupon))
                .toList();
    }

    /**
     * Toggles the rating (like or dislike) of a product by the currently authenticated user.
     *
     * <p>If the user has already liked the product and tries to like it again, an exception is thrown.
     * Similarly, if the user tries to dislike a product they haven't liked, an exception is thrown.</p>
     *
     * <p>This method updates the product's rating and saves the changes to the repository.</p>
     *
     * @param productId the ID of the product to toggle the rating for
     * @param userRating value rating for user
     * @return the updated {@link Product} object
     * @throws ResponseStatusException if the product is not found or if the rating action is invalid
     */
    @Override
    @SneakyThrows
    @Transactional
    public Product toggleRating(String productId, double userRating) {
        if (userRating < 1.0 || userRating > 5.0) throw new IllegalArgumentException("Rating harus berada di antara 1.0 dan 5.0");
        User user = userDetailToken.dataUserEmail();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found!"));

        Map<User, Double> userRatings = product.getUserRatings();
        userRatings.put(user, userRating);

        // Hitung rata-rata rating dari pengguna
        double averageRating = userRatings.values().stream().mapToDouble(Double::doubleValue).sum();
        int totalVotes = userRatings.size();

        double updatedRating = averageRating / totalVotes;

        if (updatedRating >= 4.5 && totalVotes < MINIMUM_VOTE_THRESHOLD) product.setRating(2.0);
        product.setRating(updatedRating);
        product.setRatingCount(totalVotes);
        product.setUserRatings(userRatings);

        return productRepository.save(product);
    }

    /**
     * Checks the stock levels of products and sends notification emails if the quantity is below a certain threshold.
     *
     * <p>This method is scheduled to run daily at 8 AM using a cron expression.</p>
     */
    @Override
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkStockAndNotify() {
        List<Product> lowStockProducts = productRepository.findByQuantityLessThan(7);

        for (Product product : lowStockProducts) {
            String msgBody = String.format(
                    "Product with name: %s, ID: %s, currently has a quantity of: %d",
                    product.getTitle(),
                    product.getId(),
                    product.getQuantity()
            );

            EmailDetails emailDetails = new EmailDetails("dearlyfebrianoi@gmail.com", msgBody, "Low Stock Notification");

            emailService.sendSimpleMail(emailDetails);
        }
    }

    /**
     * Retrieves a product by its ID and returns the corresponding product response.
     *
     * <p>This method is cached to improve performance for repeated queries.</p>
     *
     * @param id the ID of the product to retrieve
     * @return the {@link ProductResponse} object representing the product
     * @throws ResponseStatusException if the product is not found
     */
    @Override
    @Transactional
    @Cacheable(value = "products", key = "'getProductById:' + T(String).valueOf(#user != null ? 'user-' + #user.id : 'guest') + #id")
    public ProductResponse getProductById(String id, User user) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id : " + id + " Not Found!"));

        if (user != null && !user.getId().equals(product.getUploadedBy().getId())) {
            if (!product.getViewedUsers().contains(user)) {
                product.getViewedUsers().add(user);
                product.setViewers(product.getViewers() + 1);
                productRepository.save(product);
            }
        }

        ProductResponse response = toProductResponse(product);

        if (user != null) {
            Optional<UserCoupon> optionalUserCoupon = userCouponRepository.findActiveCouponByUserId(user.getId());
            if (optionalUserCoupon.isPresent()) {
                applyDiscountAndPriceRange(response, product, optionalUserCoupon.get().getCoupon());
            } else {
                applyNoDiscountAndPriceRange(response, product);
            }
        } else {
            applyNoDiscountAndPriceRange(response, product);
        }

        return response;
    }

    /**
     * Retrieves products uploaded by a specific user and returns a list of product responses.
     *
     * <p>This method is cached to improve performance for repeated queries.</p>
     *
     * @param userId the ID of the user whose uploaded products are to be retrieved
     * @return a list of {@link ProductResponse} objects representing the products uploaded by the user
     * @throws ResponseStatusException if the user or their products are not found
     */
    @Override
    @SneakyThrows
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'getProductsUploadedByUser:' + #userId")
    public List<ProductResponse> getProductsUploadedByUser(String userId, User user) {
        try {
            List<Product> products = productRepository.findByUploadedBy(user);
            if (products.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Products uploaded by: " + user.getEmail() + " Not Found!");
            }

            Optional<UserCoupon> optionalUserCoupon = userCouponRepository.findActiveCouponByUserId(userId);

            return products.stream()
                    .map(product -> toProductResponseWithDiscount(product, optionalUserCoupon))
                    .toList();
        } catch (Exception e) {
            logger.error("Error fetching products for user ID {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieves the cheapest products based on a keyword and a maximum price, and returns a list of product responses.
     *
     * <p>This method filters products based on the keyword and price, and sorts the results by price in ascending order.</p>
     *
     * <p>This method is cached to improve performance for repeated queries.</p>
     *
     * @param keyword the keyword to search for in product titles, descriptions, and categories
     * @param maxPrice the maximum price of the products to be retrieved
     * @return a list of {@link ProductResponse} objects representing the cheapest products
     * @throws ResponseStatusException if an error occurs while fetching the products
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'getCheapestProducts:' + #keyword + ':' + #maxPrice")
    public List<ProductResponse> getCheapestProducts(String keyword, BigDecimal maxPrice) {
        try {
            Specification<Product> specification = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (keyword != null && !keyword.isEmpty()) {
                    String likePattern = "%" + keyword.toLowerCase() + "%";
                    predicates.add(builder.or(
                            builder.like(builder.lower(root.get("title")), likePattern),
                            builder.like(builder.lower(root.get("description")), likePattern),
                            builder.like(builder.lower(root.get("categories")), likePattern)
                    ));
                }
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            List<Product> products = productRepository.findAll(specification);
            List<ProductResponse> result = new ArrayList<>();
            for (Product product : products) {
                boolean productWithinPrice = product.getPrice().compareTo(maxPrice) <= 0;
                if (productWithinPrice) {
                    ProductResponse productResponse = toProductResponse(product);
                    List<VariantInfo> variantInfos = product.getVariants().stream()
                            .map(variant -> {
                                String note = variant.getPrice().compareTo(maxPrice) > 0 ? "Harga lebih tinggi dari yang anda cari" : null;
                                return VariantInfo.builder()
                                        .name(variant.getName())
                                        .price(variant.getPrice())
                                        .formattedPrice(formatIDR(variant.getPrice()))
                                        .note(note)
                                        .build();
                            })
                            .collect(Collectors.toList());
                    productResponse.setVariants(variantInfos);
                    result.add(productResponse);
                } else {
                    List<VariantInfo> variantInfos = product.getVariants().stream()
                            .filter(variant -> variant.getPrice().compareTo(maxPrice) <= 0)
                            .map(variant -> VariantInfo.builder()
                                    .name(variant.getName())
                                    .price(variant.getPrice())
                                    .formattedPrice(formatIDR(variant.getPrice()))
                                    .build())
                            .collect(Collectors.toList());

                    if (!variantInfos.isEmpty()) {
                        ProductResponse productResponse = toProductResponse(product);
                        productResponse.setVariants(variantInfos);
                        result.add(productResponse);
                    }
                }
            }
            return result.stream()
                    .sorted(Comparator.comparing(ProductResponse::getPrice))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching products.");
        }
    }

    public boolean isCouponValid(Coupon coupon) {
        if (coupon.getIsExpired() || !coupon.getIsActive()) {
            return false;
        }

        if (coupon.getEndDate() != null && coupon.getEndDate().isBefore(LocalDateTime.now())) {
            coupon.setIsExpired(true);
            coupon.setIsActive(false);
            couponRepository.save(coupon);
            return false;
        }

        return true;
    }

    private ProductResponse createProductResponse(Product product, Optional<UserCoupon> optionalUserCoupon) {
        ProductResponse response = toProductResponse(product);

        if (optionalUserCoupon.isPresent()) {
            applyDiscountAndPriceRange(response, product, optionalUserCoupon.get().getCoupon());
        } else {
            applyNoDiscountAndPriceRange(response, product);
        }

        return response;
    }

    private ProductResponse toProductResponseWithDiscount(Product product, Optional<UserCoupon> optionalUserCoupon) {
        ProductResponse response = toProductResponse(product);

        if (optionalUserCoupon.isPresent()) {
            Coupon coupon = optionalUserCoupon.get().getCoupon();

            if (isCouponValid(coupon)) {
                applyDiscountToProduct(response, product, coupon);
            } else {
                applyNoDiscount(response, product);
            }
        } else {
            applyNoDiscount(response, product);
        }

        return response;
    }

    private void applyDiscountAndPriceRange(ProductResponse response, Product product, Coupon coupon) {
        if (isCouponValid(coupon)) {
            BigDecimal discountedPriceProduct = applyDiscount(product.getPrice(), coupon);
            response.setPrice(discountedPriceProduct);
            response.setFormattedPrice(formatIDR(discountedPriceProduct));

            List<VariantInfo> discountedVariants = product.getVariants().stream()
                    .map(variant -> createVariantInfo(variant, coupon))
                    .toList();

            response.setVariants(discountedVariants);
            response.setPriceRange(calculatePriceRange(discountedVariants));
        } else {
            applyNoDiscountAndPriceRange(response, product);
        }
    }

    private void applyNoDiscountAndPriceRange(ProductResponse response, Product product) {
        response.setPrice(product.getPrice());
        response.setFormattedPrice(formatIDR(product.getPrice()));

        List<VariantInfo> originalVariants = product.getVariants().stream()
                .map(this::createVariantInfo)
                .toList();

        response.setVariants(originalVariants);
        response.setPriceRange(calculatePriceRange(originalVariants));
    }

    private VariantInfo createVariantInfo(Variant variant, Coupon coupon) {
        BigDecimal discountedPrice = applyDiscount(variant.getPrice(), coupon);
        return VariantInfo.builder()
                .name(variant.getName())
                .price(discountedPrice)
                .formattedPrice(formatIDR(discountedPrice))
                .note(null)
                .build();
    }

    private VariantInfo createVariantInfo(Variant variant) {
        return VariantInfo.builder()
                .name(variant.getName())
                .price(variant.getPrice())
                .formattedPrice(formatIDR(variant.getPrice()))
                .note(null)
                .build();
    }

    private String calculatePriceRange(List<VariantInfo> variants) {
        BigDecimal minPrice = variants.stream()
                .map(VariantInfo::getPrice)
                .min(BigDecimal::compareTo)
                .orElseThrow(() -> new RuntimeException("No variant prices found"));

        BigDecimal maxPrice = variants.stream()
                .map(VariantInfo::getPrice)
                .max(BigDecimal::compareTo)
                .orElseThrow(() -> new RuntimeException("No variant prices found"));

        String formattedMinPrice = formatIDR(minPrice);
        String formattedMaxPrice = formatIDR(maxPrice);
        return formattedMinPrice + " - " + formattedMaxPrice;
    }

    private void applyDiscountToProduct(ProductResponse response, Product product, Coupon coupon) {
        BigDecimal discountedPriceProduct = applyDiscount(product.getPrice(), coupon);
        response.setPrice(discountedPriceProduct);
        response.setFormattedPrice(formatIDR(discountedPriceProduct));

        List<VariantInfo> discountedVariants = product.getVariants().stream()
                .map(variant -> {
                    BigDecimal discountedPrice = applyDiscount(variant.getPrice(), coupon);
                    return VariantInfo.builder()
                            .name(variant.getName())
                            .price(discountedPrice)
                            .formattedPrice(formatIDR(discountedPrice))
                            .note(null)
                            .build();
                }).toList();

        response.setVariants(discountedVariants);
        response.setPriceRange(calculatePriceRange(discountedVariants));
    }

    private void applyNoDiscount(ProductResponse response, Product product) {
        response.setPrice(product.getPrice());
        response.setFormattedPrice(formatIDR(product.getPrice()));

        List<VariantInfo> originalVariants = product.getVariants().stream()
                .map(variant -> VariantInfo.builder()
                        .name(variant.getName())
                        .price(variant.getPrice())
                        .formattedPrice(formatIDR(variant.getPrice()))
                        .note(null)
                        .build())
                .toList();

        response.setVariants(originalVariants);
        response.setPriceRange(calculatePriceRange(originalVariants));
    }
}