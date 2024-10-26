package com.management.ManagementInventaris.categories;

import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.product.Product;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface ICategoriesService {

    CategoriesResponse createCategories(CategoriesRequest request, MultipartFile file);

    CategoriesResponse updateCategories(String id, CategoriesRequest request, MultipartFile file);

    void deleteCategories(String id);

    CategoriesResponse getCategoriesById(String id);

    CategoriesResponse findByName(String categoryName);

    List<CategoriesResponse> search(SearchCategoriesRequest request);

    WebResponse<List<CategoriesResponse>> findAllCategories(int page, int size);

    /**
     * Converts a Categories object to a CategoriesResponse object.
     *
     * @param  categories The Categories object to convert.
     * @return            The converted CategoriesResponse object.
     */
    default CategoriesResponse toCategoriesResponse(Categories categories) {
        String fileUrl = "http://localhost/api/minio/download/uploaded/categories/" + categories.getImageUrl();
        return CategoriesResponse.builder()
                .id(categories.getId())
                .categoryName(categories.getCategoryName())
                .description(categories.getDescription())
                .imageUrl(fileUrl)
                .isConstant(categories.getIsConstant())
                .build();
    }
}