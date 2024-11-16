package com.management.ManagementInventaris;

import com.management.ManagementInventaris.utils.Cryptographic;
import com.management.ManagementInventaris.utils.Zone;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Yeaisa {

    @Test
    void testAja() throws GeneralSecurityException {
        String encrypted = Cryptographic.encrypt("dearlyfebrianoi@gmail.com");
        System.out.println("Ini adalh Encrypt nya: " + encrypted);
        System.out.println("Ini adalh Decrypt nya: " + Cryptographic.decrypt(encrypted));
    }
}
