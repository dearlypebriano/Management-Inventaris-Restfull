package com.management.ManagementInventaris.categories;

import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.utils.CalculatePages;
import com.management.ManagementInventaris.utils.ImageCompressor;
import com.management.ManagementInventaris.utils.UserDetailToken;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service class for managing categories.
 */
@Service
@Slf4j
public class CategoriesService implements ICategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private UserDetailToken userDetailToken;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * Creates a new category based on the provided request.
     *
     * @param  request  The request object containing the category name.
     * @return                    The response object representing the created category.
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    @CachePut(value = "products", key = "'categoriesForProduct' + #result.id")
    public CategoriesResponse createCategories(CategoriesRequest request, MultipartFile file) {
        User user = userDetailToken.dataUserEmail();
        boolean isConstant = user.getRole().name().equals("ADMIN") || user.getRole().name().equals("MANAGER");

        Categories categories = new Categories();
        categories.setCategoryName(request.getCategoryName());
        categories.setDescription(request.getDescription());
        categories.setIsConstant(isConstant);
        try {
            String hashedFileName = ImageCompressor.hashFileName(Objects.requireNonNull(file.getOriginalFilename()), file.getBytes());
            String objectName = "uploaded/categories/" + hashedFileName;
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
            categories.setImageUrl(hashedFileName);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
        categoriesRepository.save(categories);

        CategoriesDTO categoriesDTO = CategoriesDTO.fromEntity(categories);
        redisTemplate.opsForValue().set("categories:" + categoriesDTO.getId(), categoriesDTO);
        return toCategoriesResponse(categories);
    }

    /**
     * Updates an existing category based on the id and request provided.
     *
     * @param  id      The id of the category to be updated.
     * @param  request The request object containing the new category name.
     * @return         The response object representing the updated category.
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    @CachePut(value = "products", key = "'categoriesForProduct' + #result.id")
    public CategoriesResponse updateCategories(String id, CategoriesRequest request, MultipartFile file) {
        Categories categories = categoriesRepository.findById(id)
                .orElse(null);
        if (categories != null && request.getCategoryName() != null) categories.setCategoryName(request.getCategoryName());
        if (categories != null && request.getDescription() != null) categories.setDescription(request.getDescription());
        if (categories != null && file != null) {
            try {
                String hashedFileName = ImageCompressor.hashFileName(file.getOriginalFilename(), file.getBytes());
                InputStream inputStream = file.getInputStream();
                String object = "uploaded/categories/" + hashedFileName;
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(object)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
                categories.setImageUrl(hashedFileName);
            } catch (Exception e) {
                log.error(String.valueOf(e));
                e.printStackTrace();
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        categoriesRepository.save(categories);
        CategoriesDTO categoriesDTO = CategoriesDTO.fromEntity(categories);
        redisTemplate.opsForValue().set("categories:" + categoriesDTO.getId(), categoriesDTO);
        return toCategoriesResponse(categories);
    }

    /**
     * Deletes a category based on the provided id.
     *
     * @param  id The id of the category to be deleted.
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteCategories(String id) {
        Categories categories = categoriesRepository.findById(id)
                .orElse(null);
        User user = userDetailToken.dataUserEmail();

        if (categories.getIsConstant()) {
            if (!user.getRole().name().equals("ADMIN") && !user.getRole().name().equals("MANAGER")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not allowed to delete constant category");
            }
        }

        deleteFileFromMinIO(categories);

        categoriesRepository.delete(categories);

        String redisKey = "categories:" + id;
        Boolean isDeleted = redisTemplate.delete(redisKey);
        if (Boolean.FALSE.equals(isDeleted)) {
            log.warn("Categories dengan ID: {} tidak ditemukan di Redis atau gagal dihapus.", id);
        } else {
            log.info("Categories dengan ID: {} berhasil dihapus dari Redis.", id);
        }
    }

    /**
     * Retrieves a category based on the provided id.
     *
     * @param  id The id of the category to retrieve.
     * @return    The response object representing the requested category.
     */
    @Override
    @Transactional
    @Cacheable(value = "products", key = "'getCategoriesByIdForProduct:' + #id")
    public CategoriesResponse getCategoriesById(String id) {
        Categories categories = categoriesRepository.findById(id)
                .orElse(null);
        return toCategoriesResponse(categories);
    }

    /**
     * Retrieves a list of categories.
     *
     * @param  page The page number to retrieve.
     * @param  size The number of categories per page.
     * @return      A page containing a list of category response objects.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'categoriesForProduct' + '-' + #page + '-' + #size")
    public WebResponse<List<CategoriesResponse>> findAllCategories(int page, int size) {
        int offset = page * size;
        List<Categories> categories = categoriesRepository.findAllWithPagination(offset, size);

        List<CategoriesResponse> categoriesResponses = categories.stream()
                .map(this::toCategoriesResponse)
                .toList();

        CalculatePages calculatePages = new CalculatePages(categoriesRepository.count(), size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(calculatePages.calculateTotalPages())
                .size(categoriesResponses.size())
                .build();

        return WebResponse.<List<CategoriesResponse>>builder()
                .data(categoriesResponses)
                .paging(pagingResponse)
                .build();
    }

    /**
     * Retrieves a list of categories based on the provided category name.
     *
     * @param  categoryName The category name to search for.
     * @return              A list of category response objects.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'getCategoriesByName:' + #categoryName")
    public CategoriesResponse findByName(String categoryName) {
        Categories categories = categoriesRepository.findByCategoryName(categoryName);
        if (categories == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categories With Name " + categoryName + " Not Found");
        }
        return toCategoriesResponse(categories);
    }

    /**
     * Retrieves a list of categories based on the provided keyword.
     * The keyword is used to search for categories in the category name or description.
     *
     * @param  request The keyword to search for.
     * @return         A list of category response objects.
     * @throws         ResponseStatusException If no categories are found with the given keyword.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'searchCategoriesProduct-' + #request.toString()")
    public List<CategoriesResponse> search(SearchCategoriesRequest request) {
        Specification<Categories> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Objects.nonNull(request.getCategoryName())) {
                predicates.add(builder.like(builder.lower(root.get("categoryName")), "%" + request.getCategoryName().toLowerCase() + "%"));
            }
            if (Objects.nonNull(request.getDescription())) {
                predicates.add(builder.like(builder.lower(root.get("description")), "%" + request.getDescription().toLowerCase() + "%"));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Categories> products = categoriesRepository.findAll(specification, pageable);
        return products.getContent().stream()
                .map(this::toCategoriesResponse)
                .collect(Collectors.toList());
    }


    private void deleteFileFromMinIO(Categories categories) {
        try {
            String objectNames = categories.getImageUrl();
            String fullPath = "/uploaded/categories/" + objectNames;
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullPath)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from MinIO: " + e.getMessage());
        }
    }
}