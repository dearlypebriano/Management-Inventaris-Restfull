package com.management.ManagementInventaris.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ZonedDateTime parseToZonedDateTime(String dateTimeString, ZoneId zoneId) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        return localDateTime.atZone(zoneId);
    }

    public static LocalDateTime parseToLocalDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public static String getCurrentDateTime(ZoneId zoneId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return ZonedDateTime.now(zoneId).format(formatter);
    }

    public static String formatToIndonesianDateWithDay(LocalDate date) {
        Locale indonesiaLocale = new Locale("id", "ID");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy", indonesiaLocale);
        return date.format(formatter);
    }
}