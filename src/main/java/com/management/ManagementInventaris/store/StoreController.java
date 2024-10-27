package com.management.ManagementInventaris.store;

import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.utils.Cryptographic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoreResponse> createNewStore(
            @ModelAttribute StoreRequest request
    ) {
        StoreResponse response = storeService.createNewStore(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping(path = "/store-accounting")
    public ResponseEntity<StoreAccountingResponse> recordDailyIncome(
            @RequestParam double dailyIncome
    ) {
        StoreAccountingResponse response = storeService.recordDailyIncome(dailyIncome);
        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping(path = "/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StoreResponse> updateStore(
            @RequestBody StoreRequest request
    ) {
        StoreResponse response = storeService.updateStore(request);
        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping(path = "/update/store-accounting", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StoreAccountingResponse> updateRecordDailyIncome(
            @RequestParam double dailyIncome
    ) {
        StoreAccountingResponse response = storeService.updateRecordDailyIncome(dailyIncome);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping(path = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteStore(
    ) {
        storeService.deleteStore();
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping(path = "/delete/store-accounting", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteStoreAccounting(
            @RequestParam String storeAccountingId
    ) {
        String decryptedStoreAccountingId;
        try {
            decryptedStoreAccountingId = Cryptographic.decrypt(storeAccountingId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        storeService.deleteStoreAccounting(decryptedStoreAccountingId);
        return ResponseEntity.status(204).build();
    }

    @GetMapping(path = "/find/{storeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StoreResponse> findStoreById(@PathVariable String storeId) {
        String decryptedStoreId;
        try {
            decryptedStoreId = Cryptographic.decrypt(storeId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        StoreResponse response = storeService.findStoreById(decryptedStoreId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<StoreResponse>>> getAllStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        WebResponse<List<StoreResponse>> response = storeService.findAllStore(page, size);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(path = "/all-store-accounting", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<StoreAccountingResponse>>> getAllStoreAccounting(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        WebResponse<List<StoreAccountingResponse>> response = storeService.findAllStoreAccounting(page, size);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(path = "/find/{storeAccountingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StoreAccountingResponse> findStoreAccountingById(@PathVariable String storeAccountingId) {
        String decryptedStoreId;
        try {
            decryptedStoreId = Cryptographic.decrypt(storeAccountingId);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        StoreAccountingResponse response = storeService.getStoreAccountingResponse(decryptedStoreId);
        return ResponseEntity.status(200).body(response);
    }
}