package com.management.ManagementInventaris.user;

import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.utils.Cryptographic;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.List;

public interface IUserService {

    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    void followUser(String userIdToFollow);

    void unfollowUser(String userId);

    void like(String userIdToLike);

    void unlike(String userIdToUnlike);

    WebResponse<List<UserProfile>> getFollowers(String userId);

    WebResponse<List<UserProfile>> getCurrentUserFollowers(User currentUser);

    WebResponse<List<UserProfile>> getFollowing(String userId);

    WebResponse<List<UserProfile>> getCurrentUserFollowing(User currentUser);

    void deleteAccount(String userId);

    default void deleteFileFromMinIO(User user) {
        try {
            final MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9001")
                    .credentials("w53sVDQLEi8J8gJW5xYZ", "rJz0Ck9BKKRJplk4o923RILI1we2iyr4Ibosdqhy")
                    .build();

            final String bucketName = "inventaris";

            String objectName = user.getImageUrl();
            if (objectName.equals("default_profile.jpg")) {
                return;
            }
            String fullPath = "/profile/uploaded" + objectName;
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullPath)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from MinIO: " + e.getMessage());
        }
    }

    default UserProfile convertToUserProfile(User user) {
        String encrypt;
        try {
            encrypt = Cryptographic.encrypt(user.getId());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        String profileUrl = "http://localhost/api/minio/download/profile/uploaded/" + user.getImageUrl();

        return UserProfile.builder()
                .id(encrypt)
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .phone(user.getPhone())
                .followersCount(user.getFollowersCount())
                .followingCount(user.getFollowingCount())
                .role(user.getRole())
                .imageUrl(profileUrl)
                .location(user.getVillage() != null ? user.getVillage().getName() : "Unknown")
                .build();
    }
}