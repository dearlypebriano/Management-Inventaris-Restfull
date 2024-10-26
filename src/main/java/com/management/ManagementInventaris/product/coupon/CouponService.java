package com.management.ManagementInventaris.product.coupon;

import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.product.promoted.DiscountType;
import com.management.ManagementInventaris.utils.CalculatePages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class responsible for managing Coupon operations within the system.
 * This service provides methods for generating, updating, retrieving, and validating coupons,
 * as well as handling expiration and deactivation logic for coupons based on usage and time constraints.
 *
 * <p>The class is annotated with {@code @Service}, indicating that it is a Spring service component,
 * and {@code @Slf4j}, enabling logging functionality.</p>
 *
 * <p>This class uses Spring's transaction management, caching, and scheduling capabilities:</p>
 * <ul>
 *   <li>{@code @Transactional}: Ensures that methods either complete successfully or not at all,
 *   maintaining data integrity.</li>
 *   <li>{@code @CacheEvict}, {@code @CachePut}, {@code @Cacheable}: Manage the caching behavior of coupons
 *   to improve performance and reduce database load.</li>
 *   <li>{@code @Scheduled}: Automates the deactivation and expiration checks for coupons at specified intervals.</li>
 * </ul>
 *
 * <p>The class also interacts with a Redis cache via the {@code RedisTemplate} to store and retrieve coupon data efficiently.</p>
 *
 * <p>This class is designed to be thread-safe and is intended for use in a concurrent environment,
 * particularly within the context of a Spring Boot application.</p>
 *
 * <p><strong>Key Responsibilities:</strong></p>
 * <ul>
 *   <li>Generating unique, secure coupon codes.</li>
 *   <li>Creating and persisting new coupons in the database.</li>
 *   <li>Updating existing coupons with new information.</li>
 *   <li>Fetching and paginating coupons for display or management purposes.</li>
 *   <li>Validating coupon codes to ensure they are active, not expired, and within usage limits.</li>
 *   <li>Automatically deactivating coupons that have reached their maximum usage or have expired.</li>
 * </ul>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *   <li>{@code CouponRepository}: Provides access to the database for CRUD operations on the {@code Coupon} entity.</li>
 *   <li>{@code RedisTemplate}: Facilitates caching of coupon data in Redis for quick access and performance improvement.</li>
 * </ul>
 *
 * @see com.management.ManagementInventaris.product.coupon.Coupon
 * @see com.management.ManagementInventaris.product.coupon.CouponRepository
 * @see org.springframework.transaction.annotation.Transactional
 * @see org.springframework.cache.annotation.CacheEvict
 * @see org.springframework.cache.annotation.CachePut
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.scheduling.annotation.Scheduled
 * @see org.springframework.data.redis.core.RedisTemplate
 * @see java.security.SecureRandom
 * @see java.util.Base64
 * @see java.util.UUID
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64encoder = Base64.getEncoder();
    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);

    /**
     * Generates a new coupon based on the provided {@code CouponRequest}.
     *
     * <p>This method creates a unique coupon with a generated code, sets the discount type and value,
     * and assigns the start and end dates based on the time unit and duration provided in the request.
     * The newly created coupon is saved to the database and cached in Redis for quick access.</p>
     *
     * <p>Transactional boundaries ensure that the entire operation is atomic, meaning either all changes
     * are applied or none at all, preventing partial updates.</p>
     *
     * <p>The coupon is automatically activated and set to be used for the specified period.
     * Caching is managed with {@code @CacheEvict} to clear outdated entries and {@code @CachePut}
     * to update the cache with the new coupon data.</p>
     *
     * @param request The {@code CouponRequest} object containing details about the coupon to be created,
     *                including the discount type, value, maximum uses, and duration.
     * @return A {@code CouponResponse} object containing the details of the newly created coupon.
     * @throws IllegalArgumentException if the provided discount type or time unit is invalid.
     * @throws IllegalStateException if an invalid time unit is provided.
     */
    @Transactional
    @CacheEvict(value = "coupon", allEntries = true)
    @CachePut(value = "coupon", key = "#result.id")
    public CouponResponse generateCoupon(CouponRequest request) {
        Coupon coupon = new Coupon();
        coupon.setId(UUID.randomUUID().toString());
        String code = generateCodeCoupon();
        coupon.setCode(code);
        DiscountType discountType;
        try {
            discountType = DiscountType.valueOf(request.getDiscountType().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid discount type");
        }
        coupon.setDiscountType(discountType);
        if (request.getDiscountValue() == null || request.getDiscountValue() <= 0) {
            throw new IllegalArgumentException("Discount value must be a positive number");
        }
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMaxUses(request.getMaxUses());
        coupon.setUsesCount(0);
        coupon.setStartDate(LocalDateTime.now());
        TimeUnit timeUnit;
        try {
            timeUnit = TimeUnit.valueOf(request.getTimeUnit().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid time unit");
        }
        LocalDateTime endDate = LocalDateTime.now();
        endDate = switch (timeUnit) {
            case DAY -> endDate.plusDays(request.getNumberOfDays());
            case WEEK -> endDate.plusWeeks(request.getNumberOfDays());
            case MONTH -> endDate.plusMonths(request.getNumberOfDays());
            default -> throw new IllegalStateException("Invalid time unit");
        };
        coupon.setEndDate(endDate);

        couponRepository.save(coupon);
        logger.debug("Coupon successfully created with ID: {}", coupon.getId());

        CouponDTO couponDTO = CouponDTO.fromEntity(coupon);
        redisTemplate.opsForValue().set("coupon:" + couponDTO.getId(), couponDTO);
        return toCouponResponse(coupon);
    }

    /**
     * Updates an existing coupon with the provided details.
     *
     * <p>This method fetches the coupon from the database using its unique ID and updates the relevant fields
     * such as discount type, discount value, and maximum uses, based on the information provided in the request.
     * The coupon is then saved back to the database and updated in the Redis cache.</p>
     *
     * <p>The method is marked as {@code @Transactional} to ensure that the update process is atomic,
     * meaning that either all changes are applied successfully or none at all. This helps maintain data integrity.</p>
     *
     * <p>Cache eviction is performed to clear outdated cache entries, ensuring that the most recent data
     * is available for subsequent requests.</p>
     *
     * @param couponId The unique ID of the coupon to be updated.
     * @param request The {@code CouponRequest} object containing the updated coupon details.
     * @return A {@code CouponResponse} object reflecting the updated coupon information.
     * @throws IllegalArgumentException if the coupon is not found or the provided discount type is invalid.
     */
    @Transactional
    @CacheEvict(value = "coupon", allEntries = true)
    public CouponResponse updateCoupon(String couponId, CouponRequest request) {
        Coupon coupon = couponRepository.findById(couponId)
               .orElseThrow(() -> new IllegalArgumentException("Coupon not found: " + couponId));

        if (request.getDiscountType()!= null) {
            DiscountType discountType;
            try {
                discountType = DiscountType.valueOf(request.getDiscountType().toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid discount type");
            }
            coupon.setDiscountType(discountType);
        }
        if (request.getDiscountValue() != null) {
            coupon.setDiscountValue(request.getDiscountValue());
        }
        if (request.getMaxUses() != null) {
            coupon.setMaxUses(request.getMaxUses());
        }

        couponRepository.save(coupon);

        CouponDTO couponDTO = CouponDTO.fromEntity(coupon);
        redisTemplate.opsForValue().set("coupon:" + couponDTO.getId(), couponDTO);
        return toCouponResponse(coupon);
    }

    /**
     * Retrieves all coupons with pagination support.
     *
     * <p>This method fetches a paginated list of coupons from the database based on the specified page number and size.
     * It then converts the list of coupons into {@code CouponResponse} objects and calculates pagination details.</p>
     *
     * <p>The method is annotated with {@code @Cacheable} to cache the result, reducing the need for repeated database
     * queries when the same page is requested multiple times. The cache key is based on the combination of page and size.</p>
     *
     * @param page The page number to retrieve, starting from 0.
     * @param size The number of coupons per page.
     * @return A {@code WebResponse} containing the list of {@code CouponResponse} objects and pagination information.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "coupon", key = "'allCoupons:' + #page + '-' + #size")
    public WebResponse<List<CouponResponse>> getAllCoupons(int page, int size) {
        int offset = page * size;
        List<Coupon> coupons = couponRepository.findAllWithPagination(offset, size);

        List<CouponResponse> couponResponses = coupons.stream()
                .map(this::toCouponResponse)
                .toList();

        CalculatePages calculatePages = new CalculatePages(couponRepository.count(), size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(calculatePages.calculateTotalPages())
                .build();

        return WebResponse.<List<CouponResponse>>builder()
                .data(couponResponses)
                .paging(pagingResponse)
                .build();
    }

    @Transactional
    public void deleteCoupon(String couponId) {
        Optional<Coupon> couponOpt = couponRepository.findById(couponId);
        if (couponOpt.isPresent()) {
            Coupon coupon = couponOpt.get();
            userCouponRepository.deleteByCouponId(couponId);
            couponRepository.delete(coupon);

            String redisKey = "coupon:" + coupon.getId();
            redisTemplate.delete(redisKey);
        } else {
            throw new EntityNotFoundException("Coupon not found");
        }
    }

    /**
     * Validates a coupon based on its code.
     *
     * <p>This method checks whether a coupon with the specified code exists in the database, is active, and has not expired.
     * If the coupon meets these conditions, it is returned wrapped in an {@code Optional}. Otherwise, an empty {@code Optional}
     * is returned, indicating that the coupon is either invalid, expired, or inactive.</p>
     *
     * <p>This method is read-only and does not modify the state of the coupon or the database.</p>
     *
     * @param code The unique code of the coupon to be validated.
     * @return An {@code Optional} containing the valid {@code Coupon} if found and valid; otherwise, an empty {@code Optional}.
     */
    @Transactional(readOnly = true)
    public Optional<Coupon> validateCoupon(String code) {
        Optional<Coupon> coupon = couponRepository.findByCode(code);
        if (coupon.isPresent() && !coupon.get().getIsExpired() && coupon.get().getIsActive()) {
            return coupon;
        }
        return Optional.empty();
    }

    /**
     * Generates a secure and unique coupon code.
     *
     * <p>This method uses a {@code SecureRandom} instance to generate a random sequence of bytes,
     * which is then encoded into a Base64 string. Non-alphanumeric characters are removed, and the code
     * is truncated to a length of 8 characters, ensuring uniqueness and validity for use as a coupon code.</p>
     *
     * <p>The method is private and intended for internal use within the {@code CouponService} class.</p>
     *
     * @return A string representing the generated coupon code, consisting of 8 uppercase alphanumeric characters.
     */
    public String generateCodeCoupon() {
        byte[] randomBytes = new byte[6];
        secureRandom.nextBytes(randomBytes);
        String couponCode = base64encoder.encodeToString(randomBytes);
        couponCode = couponCode.replaceAll("[^A-Za-z0-9]", "");
        return couponCode.substring(0, 8).toUpperCase();
    }

    /**
     * Deactivates coupons that have reached their maximum usage limit.
     *
     * <p>This method is scheduled to run daily at midnight (00:00) and identifies coupons that have been used
     * up to their maximum allowed usage. These coupons are marked as inactive in the database and logged for
     * auditing purposes.</p>
     *
     * <p>The method uses the {@code CouponRepository} to fetch the coupons, updates their status,
     * and persists the changes back to the database.</p>
     *
     * <p>Regular execution of this method ensures that no coupon is used beyond its intended limit,
     * maintaining the integrity of promotions and discounts.</p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    protected void deactivateMaxUsedCoupons() {
        List<Coupon> maxUsedCoupons = couponRepository.findByMaxUsesReached();

        if (!maxUsedCoupons.isEmpty()) {
            maxUsedCoupons.forEach(coupon -> {
                coupon.setIsActive(false);
                coupon.setIsExpired(true);
                couponRepository.save(coupon);
                logger.info("Coupon {} has been deactivated due to reaching its max usage.", coupon.getCode());
            });
        } else {
            logger.info("No coupons have reached their max usage.");
        }
    }

    /**
     * Revokes and marks as expired any coupons that have passed their end date.
     *
     * <p>This method is scheduled to run daily at midnight (00:00) and identifies coupons whose end date
     * has passed. These coupons are marked as expired and deactivated in the database.</p>
     *
     * <p>This operation ensures that expired coupons are no longer considered valid for transactions,
     * thereby maintaining the integrity of active promotions.</p>
     *
     * <p>The method uses the {@code CouponRepository} to fetch the expired coupons, updates their status,
     * and persists the changes back to the database.</p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    protected void revokeAndDeleteCodeExpired() {
        List<Coupon> expiredCoupons = couponRepository.findByEndDateBefore(LocalDateTime.now());

        if (!expiredCoupons.isEmpty()) {
            expiredCoupons.forEach(coupon -> {
                coupon.setIsExpired(true);
                coupon.setIsActive(false);
                couponRepository.save(coupon);

                String redisKey = "coupon:" + coupon.getId();
                redisTemplate.delete(redisKey);
            });
        } else {
            logger.info("No Expired Coupon In Here");
        }
    }

    /**
     * Converts a {@code Coupon} entity to a {@code CouponResponse} DTO.
     *
     * <p>This method takes a {@code Coupon} entity and maps its fields to a corresponding {@code CouponResponse}
     * object, which is then used for returning data to the client. The method also determines the current active
     * and expired status of the coupon based on its start and end dates and usage limits.</p>
     *
     * <p>The method is private and intended for internal use within the {@code CouponService} class.</p>
     *
     * @param coupon The {@code Coupon} entity to be converted.
     * @return A {@code CouponResponse} DTO representing the coupon data.
     */
    private CouponResponse toCouponResponse(Coupon coupon) {
        CouponResponse response = CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType().toString())
                .discountValue(coupon.getDiscountValue())
                .maxUses(coupon.getMaxUses())
                .usesCount(coupon.getUsesCount())
                .startDate(coupon.getStartDate().toString())
                .endDate(coupon.getEndDate().toString())
                .isActive(coupon.getStartDate().isBefore(LocalDateTime.now()) &&
                        coupon.getEndDate().isAfter(LocalDateTime.now()) &&
                        coupon.getMaxUses() > coupon.getUsesCount())
                .isExpired(coupon.getEndDate().isBefore(LocalDateTime.now()))
                .build();
        response.formatDiscountValue();

        return response;
    }
}