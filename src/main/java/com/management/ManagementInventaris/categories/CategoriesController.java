package com.management.ManagementInventaris.categories;

import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller class for managing categories.
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    /**
     * Creates a new category based on the provided request.
     *
     * @param  request The request object containing the category name.
     * @return         The response entity containing the created category.
     */
    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoriesResponse> create(
            @ModelAttribute CategoriesRequest request,
            @RequestParam MultipartFile file
    ) {
        CategoriesResponse categoriesResponse = categoriesService.createCategories(request, file);
        return new ResponseEntity<>(categoriesResponse, HttpStatus.CREATED);
    }

    /**
     * Updates an existing category based on the id and request provided.
     *
     * @param  id      The id of the category to be updated.
     * @param  request The request object containing the new category name.
     * @return         The response entity containing the updated category.
     */
    @PatchMapping(path = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoriesResponse> update(
            @PathVariable String id,
            @ModelAttribute CategoriesRequest request,
            @RequestParam("file") MultipartFile file
    ) {
        CategoriesResponse categoriesResponse = categoriesService.updateCategories(id, request, file);
        return new ResponseEntity<>(categoriesResponse, HttpStatus.OK);
    }

    /**
     * Deletes a category based on the provided id.
     *
     * @param  id The id of the category to be deleted.
     * @return    The response entity confirming the deletion.
     */
    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delete(
            @PathVariable String id
    ) {
        categoriesService.deleteCategories(id);
        return new ResponseEntity<>("Categories deleted successfully", HttpStatus.OK);
    }

    /**
     * Retrieves a category based on the provided id.
     *
     * @param  id The id of the category to retrieve.
     * @return    The response entity containing the requested category.
     */
    @GetMapping(path = "/findById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesResponse> findById(@PathVariable String id) {
        CategoriesResponse categoriesResponse = categoriesService.getCategoriesById(id);
        if (categoriesResponse != null) {
            return new ResponseEntity<>(categoriesResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves a category based on the provided name.
     *
     * @param  categoryName The name of the category to retrieve.
     * @return              The response entity containing the requested category.
     * @throws IllegalArgumentException
     * if the requested category does not exist
     */
    @GetMapping(path = "/findByName/{categoryName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoriesResponse> findByName(@PathVariable String categoryName) {
        CategoriesResponse categoriesResponse = categoriesService.findByName(categoryName);
        if (categoriesResponse!= null) {
            return new ResponseEntity<>(categoriesResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves a category based on the provided name.
     *
     * @param  {categoryName, description}  The keyword of the category to retrieve.
     * @return              The response entity containing the requested category.
     * @throws IllegalArgumentException
     * if the requested category does not exist
     */
    @GetMapping(path = "/findByKeyword/{keyword}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<CategoriesResponse>>> findByKeyword(
            @RequestParam(value = "categoryName", required = false) String categoryName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ) {
        SearchCategoriesRequest categoriesRequest = SearchCategoriesRequest.builder()
                .page(page)
                .size(size)
                .categoryName(categoryName)
                .description(description)
                .build();
        List<CategoriesResponse> responses = categoriesService.search(categoriesRequest);
        WebResponse<List<CategoriesResponse>> response = WebResponse.<List<CategoriesResponse>>builder()
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
     * Retrieves a list of categories.
     *
     * @param  page The page number to retrieve.
     * @param  size The number of categories per page.
     * @return      The response entity containing a page of category objects.
     */
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<CategoriesResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        WebResponse<List<CategoriesResponse>> categoriesResponse = categoriesService.findAllCategories(page, size);
        return new ResponseEntity<>(categoriesResponse, HttpStatus.OK);
    }
}