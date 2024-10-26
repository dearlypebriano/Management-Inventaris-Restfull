package com.management.ManagementInventaris.product.promoted;

import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.utils.CalculatePages;
import com.management.ManagementInventaris.utils.ImageCompressor;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service class for managing promotions within the inventory management system.
 * Provides methods for creating, updating, retrieving, deleting, and searching promotions.
 * Implements caching and handles Redis operations for promotion data.
 */
@Slf4j
@Service
public class PromotionService implements IPromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${minio.bucketName}")
    private String bucketName;

    private static final Logger logger = LoggerFactory.getLogger(PromotionService.class);

    /**
     * Creates a new promotion with the specified details.
     *
     * @param request The request object containing details of the promotion to be created.
     *                It includes fields like name, description, discount type, discount value, and number of days.
     * @return A {@link PromotionResponse} object containing details of the created promotion.
     * @throws IllegalArgumentException if the discount type is invalid.
     */
    @Override
    @Transactional
    @CacheEvict(value = "promotion", allEntries = true)
    @CachePut(value = "promotion", key = "#result.id")
    public PromotionResponse createPromotion(PromotionRequest request, List<MultipartFile> files) {
        Promotion promotion = new Promotion();
        promotion.setId(UUID.randomUUID().toString());
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());

        DiscountType discountType;
        try {
            discountType = DiscountType.valueOf(request.getDiscountType().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid discount type: " + request.getDiscountType());
        }
        promotion.setDiscountType(discountType);
        promotion.setDiscountValue(request.getDiscountValue());

        List<String> fileImages = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String hashedFileName = ImageCompressor.hashFileName(file.getOriginalFilename(), file.getBytes());
                String objectName = "uploaded/promotion/" + hashedFileName;
                InputStream stream = file.getInputStream();
                minioClient.putObject(PutObjectArgs.builder()
                       .bucket(bucketName)
                       .object(objectName)
                       .stream(stream, stream.available(), -1)
                       .build());
                fileImages.add(hashedFileName);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file: " + e.getMessage());
            }
        }
        promotion.setFileImage(!fileImages.isEmpty() ? fileImages : new ArrayList<>());
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().plusWeeks(request.getNumberOfDays()));
        System.out.println("Received discountValue: " + request.getDiscountValue());
        promotionRepository.save(promotion);

        PromotionDTO promotionDTO = PromotionDTO.fromEntity(promotion);
        redisTemplate.opsForValue().set("promotion:" + promotionDTO.getId(), promotionDTO);

        return toPromotionResponse(promotion);
    }

    /**
     * Updates an existing promotion with the specified ID.
     *
     * @param id      The unique identifier of the promotion to be updated.
     * @param request The request object containing the updated details of the promotion.
     *                Only the provided fields will be updated (e.g., name, description, discount type, discount value, number of days).
     * @return A {@link PromotionResponse} object containing the updated promotion details.
     * @throws IllegalArgumentException if the promotion is not found or the discount type is invalid.
     */
    @Override
    @Transactional
    @CacheEvict(value = "promotion", allEntries = true)
    @CachePut(value = "promotion", key = "#result.id")
    public PromotionResponse updatePromotion(String id, PromotionRequest request, List<MultipartFile> files) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found: " + id));

        if (request.getName() != null) {
            promotion.setName(request.getName());
        }

        if (request.getDescription() != null) {
            promotion.setDescription(request.getDescription());
        }

        if (request.getDiscountType() != null) {
            DiscountType discountType;
            try {
                discountType = DiscountType.valueOf(request.getDiscountType().toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid discount type: " + request.getDiscountType());
            }
            promotion.setDiscountType(discountType);
        }

        if (request.getDiscountValue() != null) {
            promotion.setDiscountValue(request.getDiscountValue());
        }

        if (request.getNumberOfDays() != null) {
            promotion.setEndDate(LocalDateTime.now().plusWeeks(request.getNumberOfDays()));
        }

        if (!files.isEmpty()) {
            List<String> fileImages = promotion.getFileImage() != null ? promotion.getFileImage() : new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                try {
                    String hashedFileName = ImageCompressor.hashFileName(file.getOriginalFilename(), file.getBytes());
                    String objectPath = "uploaded/promotion/" + hashedFileName;
                    InputStream inputStream = file.getInputStream();

                    if (i < fileImages.size()) deleteFileFromMinIO(promotion);
                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .stream(inputStream, inputStream.available(), -1)
                            .build());
                    fileImages.add(hashedFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to upload image: " + e.getMessage());
                }
            }
            promotion.setFileImage(fileImages);
        }

        promotionRepository.save(promotion);

        PromotionDTO promotionDTO = PromotionDTO.fromEntity(promotion);
        redisTemplate.opsForValue().set("promotion:" + promotionDTO.getId(), promotionDTO);

        return toPromotionResponse(promotion);
    }

    /**
     * Retrieves the details of a promotion by its ID.
     *
     * @param id The unique identifier of the promotion to retrieve.
     * @return A {@link PromotionResponse} object containing the promotion details.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "promotion", key = "'getPromotionById:' + #id")
    public PromotionResponse getPromotionById(String id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElse(null);
        return toPromotionResponse(promotion);
    }

    /**
     * Deletes a promotion by its ID.
     *
     * @param id The unique identifier of the promotion to delete.
     * @throws IllegalArgumentException if the promotion is not found.
     */
    @Override
    @Transactional
    @CacheEvict(value = "promotion", allEntries = true)
    public void deletePromotion(String id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promotion not found: " + id));
        promotionRepository.delete(promotion);

        String redisKey = "promotion:" + promotion.getId();
        redisTemplate.delete(redisKey);
    }

    /**
     * Retrieves all promotions with pagination.
     *
     * @param page The page number to retrieve.
     * @param size The number of promotions per page.
     * @return A {@link WebResponse} object containing a list of {@link PromotionResponse} objects and pagination information.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "promotion", key = "'allPromotions'")
    public WebResponse<List<PromotionResponse>> getAllPromotions(int page, int size) {
        int offset = page * size;
        List<Promotion> promotions = promotionRepository.findAllWithPagination(offset, size);

        List<PromotionResponse> promotionResponses = promotions.stream()
                .map(this::toPromotionResponse)
                .toList();

        CalculatePages calculatePages = new CalculatePages(promotionRepository.count(), size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(calculatePages.calculateTotalPages())
                .build();

        return WebResponse.<List<PromotionResponse>>builder()
                .data(promotionResponses)
                .paging(pagingResponse)
                .build();
    }

    /**
     * Searches for promotions based on the provided criteria.
     *
     * @param request The search criteria, including name, description, discount type, and discount value.
     * @return A list of {@link PromotionResponse} objects matching the search criteria.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "promotion", key = "'search-' + #request.toString()")
    public List<PromotionResponse> searchPromotion(SearchPromotionRequest request) {
        Specification<Promotion> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(request.getName())) {
                predicates.add(builder.like(builder.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }
            if (Objects.nonNull(request.getDescription())) {
                predicates.add(builder.like(builder.lower(root.get("description")), "%" + request.getDescription().toLowerCase() + "%"));
            }
            if (Objects.nonNull(request.getDiscountType())) {
                predicates.add(builder.equal(root.get("discountType"), "%" + request.getDiscountType().toUpperCase().replace(" ", "_")));
            }
            if (Objects.nonNull(request.getDiscountValue())) {
                predicates.add(builder.equal(root.get("discountValue"), request.getDiscountValue()));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };

        List<Promotion> promotions = promotionRepository.findAll(specification);
        return promotions.stream()
                .map(this::toPromotionResponse)
                .toList();
    }

    /**
     * Scheduled task that removes expired promotions from the repository and Redis cache.
     * Runs daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    protected void removeExpiredPromotions() {
        List<Promotion> expiredPromotions = promotionRepository.findByEndDateBefore(LocalDateTime.now());

        if (!expiredPromotions.isEmpty()) {
            expiredPromotions.forEach(promotion -> {
                promotionRepository.delete(promotion);
                redisTemplate.delete("promotion:" + promotion.getId());
            });
        } else {
            logger.info("No expired promotions found!.");
        }
    }
}