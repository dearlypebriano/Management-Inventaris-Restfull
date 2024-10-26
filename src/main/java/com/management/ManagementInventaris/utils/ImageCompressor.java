package com.management.ManagementInventaris.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

/**
 * Utility class for image-related operations such as compression, decompression, and filename hashing.
 * <p>
 * The class provides methods to generate a unique and secure hashed filename using the SHA-256 algorithm.
 * The generated filename incorporates randomness, the original file content, and a timestamp to ensure
 * uniqueness and prevent collisions.
 * </p>
 */
public final class ImageCompressor {

    /**
     * Generates a unique and secure hashed filename for the given original filename and file content using
     * the SHA-256 hashing algorithm. The resulting filename includes a random UUID, a timestamp, and the
     * original file's extension to ensure that it is unique and non-conflicting.
     *
     * Example:
     * <pre>
     * byte[] fileContent = ...; // File content as a byte array
     * String originalFileName = "image.png";
     * String hashedFileName = ImageCompressor.hashFileName(originalFileName, fileContent);
     * System.out.println(hashedFileName); // Outputs: 7e57d004_abc123e7_20240821_1f0b3c7dabc4.png
     * </pre>
     *
     * @param originalFileName The original filename. This must not be null and should include the file extension.
     * @param fileContent      The content of the file as a byte array. This must not be null.
     * @return The unique and hashed filename, or null if an error occurs during hashing.
     * @throws IllegalArgumentException if the originalFileName or fileContent is null.
     */
    public static String hashFileName(String originalFileName, byte[] fileContent) {
        Objects.requireNonNull(originalFileName, "Original filename must not be null");
        Objects.requireNonNull(fileContent, "File content must not be null");

        try {
            String extension = "";
            int lastIndexOfDot = originalFileName.lastIndexOf('.');
            if (lastIndexOfDot > 0) {
                extension = originalFileName.substring(lastIndexOfDot);
            }

            String uniquePrefix = generateUniquePrefix();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(fileContent);
            byte[] digest = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            String hashedFileName = uniquePrefix + hexString.substring(0, 12);
            if (!hashedFileName.endsWith(extension)) {
                hashedFileName += extension;
            }
            return hashedFileName;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a unique prefix using a combination of a random UUID and the current timestamp.
     *
     * @return A string representing a unique prefix.
     */
    private static String generateUniquePrefix() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        long timestamp = System.currentTimeMillis();
        return uuid + "_" + Long.toHexString(timestamp) + "_";
    }
}