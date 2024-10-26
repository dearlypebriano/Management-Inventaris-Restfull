package com.management.ManagementInventaris.utils;

import java.time.ZonedDateTime;

public enum Zone {
    WIB("WIB", "+07:00"),
    WITA("WITA", "+08:00"),
    WIT("WIT", "+09:00");

    private final String label;
    private final String offset;

    Zone(String label, String offset) {
        this.label = label;
        this.offset = offset;
    }

    public String getLabel() {
        return label;
    }

    public String getOffset() {
        return offset;
    }

    public static String getZoneLabel(ZonedDateTime dateTime) {
        String zoneId = dateTime.getZone().getId();
        switch (zoneId) {
            case "Asia/Jakarta":
                return WIB.getLabel();
            case "Asia/Makassar":
                return WITA.getLabel();
            case "Asia/Jayapura":
                return WIT.getLabel();
            default:
                return zoneId; // Jika tidak ada yang sesuai, kembalikan id zona
        }
    }
}