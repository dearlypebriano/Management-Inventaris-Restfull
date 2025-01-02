package com.management.ManagementInventaris.utils;

import com.management.ManagementInventaris.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceInfoService {

    @Autowired
    private UserRepository userRepository;

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public void updateUserDeviceInfo(String email, HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String userAgent = getUserAgent(request);

        userRepository.updateUserDeviceInfo(email, ipAddress, userAgent);
    }
}