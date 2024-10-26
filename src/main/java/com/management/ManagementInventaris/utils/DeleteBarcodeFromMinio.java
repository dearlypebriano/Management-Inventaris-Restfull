package com.management.ManagementInventaris.utils;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class DeleteBarcodeFromMinio {

    @Autowired
    private MinioClient minioClient;

    public void deleteBarcodeFromMinio(String filename) {
        try {
            String fullPath = "/barcodes/" + filename;
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket("inventaris")
                    .object(fullPath)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from MinIO: " + e.getMessage());
        }
    }
}