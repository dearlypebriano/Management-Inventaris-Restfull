package com.management.ManagementInventaris.file;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Controller for handling file operations using Minio storage.
 */
@RestController
@RequestMapping("/api/minio")
public class MinioStorageController {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * Endpoint for downloading a file from Minio storage.
     *
     * @param request The HttpServletRequest object containing the full path to the file.
     * @return ResponseEntity with the downloaded file as InputStreamResource.
     */
    @GetMapping("/download/**")
    public ResponseEntity<InputStreamResource> downloadFile(HttpServletRequest request) {
        String fullPath = extractFullPath(request, "/api/minio/download/");
        String fileName = getFileNameFromPath(fullPath);

        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPath)
                            .build()
            );
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint for generating a temporary URL for a file in Minio storage.
     *
     * @param request The HttpServletRequest object containing the full path to the file.
     * @return ResponseEntity with the temporary URL as String.
     */
    @GetMapping("/temporary-url/**")
    public ResponseEntity<String> getTemporaryFileUrl(HttpServletRequest request) {
        String fullPath = extractFullPath(request, "/api/minio/temporary-url/");

        try {
            String url = temporaryFileUrl(fullPath);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate temporary URL for file.");
        }
    }

    /**
     * Endpoint for deleting a file from Minio storage.
     *
     * @param request The HttpServletRequest object containing the full path to the file.
     * @return ResponseEntity with a message indicating whether the deletion was successful.
     */
    @DeleteMapping("/delete/**")
    public ResponseEntity<String> deleteFile(HttpServletRequest request) {
        String fullPath = extractFullPath(request, "/api/minio/delete/");

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPath)
                            .build()
            );
            return ResponseEntity.ok("File deleted successfully from MinIO: " + fullPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file from MinIO: " + e.getMessage());
        }
    }

    /**
     * Method for generating a temporary URL for a file in Minio storage.
     *
     * @param objectPath The full path to the file, including any folders and file name.
     * @return Temporary URL as String.
     */
    public String temporaryFileUrl(String objectPath) {
        try {
            String url = getTempUrl(bucketName, objectPath);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download file: " + e.getMessage());
        }
    }

    /**
     * Private method for generating a temporary URL for a file in Minio storage.
     *
     * @param bucketName The name of the bucket.
     * @param objectName The name of the file.
     * @return Temporary URL as String.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws MinioException
     */
    private String getTempUrl(String bucketName, String objectName) throws IOException, NoSuchAlgorithmException, InvalidKeyException, MinioException {
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("response-content-type", "application/json");
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(2, TimeUnit.HOURS)
                        .extraQueryParams(reqParams)
                        .build());
    }

    /**
     * Helper method to extract the full path from the HttpServletRequest.
     *
     * @param request The HttpServletRequest object.
     * @param prefix  The prefix to remove from the request URI.
     * @return The full path to the file.
     */
    private String extractFullPath(HttpServletRequest request, String prefix) {
        return request.getRequestURI().substring(prefix.length());
    }

    /**
     * Helper method to extract the file name from the full path.
     *
     * @param fullPath The full path to the file, including any folders and file name.
     * @return The file name.
     */
    private String getFileNameFromPath(String fullPath) {
        int lastSlashIndex = fullPath.lastIndexOf('/');
        return lastSlashIndex != -1 ? fullPath.substring(lastSlashIndex + 1) : fullPath;
    }
}