package com.management.ManagementInventaris.product.promoted;

import com.management.ManagementInventaris.handler.WebResponse;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPromotionService {

    PromotionResponse createPromotion(PromotionRequest request, List<MultipartFile> files);

    PromotionResponse updatePromotion(String id, PromotionRequest request, List<MultipartFile> files);

    PromotionResponse getPromotionById(String id);

    void deletePromotion(String id);

    WebResponse<List<PromotionResponse>> getAllPromotions(int page, int size);

    List<PromotionResponse> searchPromotion(SearchPromotionRequest request);

    default void deleteFileFromMinIO(Promotion promotion) {
        final MinioClient minioClient = null;
        final String bucketName = "inventaris";
        try {
            List<String> objectNames = promotion.getFileImage();
            for (String objectName: objectNames) {
                String fullPath = "/uploaded/promotion/" + objectName;
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fullPath)
                        .build());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from MinIO: " + e.getMessage());
        }
    }

    default PromotionResponse toPromotionResponse(Promotion promotion) {

        List<String> fileUrls = promotion.getFileImage().stream()
                .map(filename -> "http://localhost/api/minio/download/uploaded/promotion/" + filename)
                .toList();

        PromotionResponse response = PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType().name())
                .discountValue(promotion.getDiscountValue())
                .imageUrls(fileUrls)
                .startDate(promotion.getStartDate().toString())
                .endDate(promotion.getEndDate().toString())
                .build();
        response.formatDiscountValue();

        return response;
    }
}