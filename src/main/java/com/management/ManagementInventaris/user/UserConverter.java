package com.management.ManagementInventaris.user;

import com.management.ManagementInventaris.utils.Cryptographic;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class UserConverter {
    @SneakyThrows
    public static UserProfile toUserProfile(User user) {
        String location = String.format("%s, %s, %s, %s",
                user.getProvince().getName(),
                user.getRegency().getName(),
                user.getDistrict().getName(),
                user.getVillage().getName());

        location = Arrays.stream(location.split(", "))
                .map(word -> {
                    if (word.equals(word.toUpperCase())) {
                        return word.substring(0, 1) + word.substring(1).toLowerCase();
                    } else {
                        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                    }
                })
                .collect(Collectors.joining(", "));

        String profileUrl = "http://localhost/api/minio/download/profile/uploaded/" + user.getImageUrl();
        if (!fileExists(profileUrl)) {
            profileUrl = "http://localhost/api/minio/download/profile/default/" + user.getImageUrl();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return UserProfile.builder()
                .id(Cryptographic.encrypt(user.getId()))
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsernameUser())
                .email(user.getEmail())
                .bioProfile(user.getBioProfile())
                .whatsappUrl("https://wa.me/" + user.getPhone())
                .userUrl("http://localhost/api/v1/auth/findUserById/" + Cryptographic.encrypt(user.getId()))
                .imageUrl(profileUrl)
                .location(location)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .timezoneLabel(user.getTimezoneLabel())
                .build();
    }

    private static boolean fileExists(String imageFile) {
        try {
            URL url = new URL(imageFile);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}