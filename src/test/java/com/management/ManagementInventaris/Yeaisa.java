package com.management.ManagementInventaris;

import com.management.ManagementInventaris.utils.Zone;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Yeaisa {

    @Test
    void test() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formattedDate = now.format(formatter) + " " + Zone.getZoneLabel(now);
        System.out.println("Formatted Date: " + formattedDate);
    }

    @Test
    void sdoa() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("SDJS: " + now);
    }

    @Test
    void sasa() {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.systemDefault());
        String zoneId = Zone.getZoneLabel(dateTime);

        String anu = dateTime + " " + zoneId;
        System.out.println(anu);
    }
}
