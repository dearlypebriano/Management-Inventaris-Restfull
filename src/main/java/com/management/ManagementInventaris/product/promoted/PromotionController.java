package com.management.ManagementInventaris.product.promoted;

import com.management.ManagementInventaris.handler.WebResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promoted")
public class PromotionController {

    @Autowired
    private IPromotionService promotionService;

    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PromotionResponse> createNewPromotion(
            @Valid @ModelAttribute PromotionRequest request,
            @RequestParam("file") List<MultipartFile> files
    ) {
        PromotionResponse response = promotionService.createPromotion(request, files);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping(path = "/updatePromoted/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PromotionResponse> updatePromotion(
            @PathVariable String id,
            @ModelAttribute PromotionRequest request,
            @RequestParam("file") List<MultipartFile> files
    ) {
        PromotionResponse response = promotionService.updatePromotion(id, request, files);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/{promotionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PromotionResponse> getPromotionById(@PathVariable String promotionId) {
        PromotionResponse response = promotionService.getPromotionById(promotionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/allPromoted", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<PromotionResponse>>> findAllPromoted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        WebResponse<List<PromotionResponse>> responses = promotionService.getAllPromotions(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @DeleteMapping(path = "/deletePromoted/{promotionId}")
    public ResponseEntity<Void> deletePromotion(@PathVariable String promotionId) {
        promotionService.deletePromotion(promotionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PromotionResponse>> getPromotedByName(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("discountType") String discountType,
            @RequestParam("discountValue") BigDecimal discountValue
    ) {
        SearchPromotionRequest request = SearchPromotionRequest.builder()
                .name(name)
                .description(description)
                .discountType(discountType)
                .discountValue(discountValue)
                .build();
        List<PromotionResponse> response = promotionService.searchPromotion(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}