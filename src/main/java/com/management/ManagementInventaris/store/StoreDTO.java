package com.management.ManagementInventaris.store;

import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.village.Village;
import com.management.ManagementInventaris.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreDTO {

    private String id;

    private String storeName;

    private Province province;

    private Regency regency;

    private District district;

    private Village village;

    private User user;

    private String establishedSince;

    private String timezoneLabel;

    public static StoreDTO fromEntity(Store store) {
        return StoreDTO.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .province(store.getProvince())
                .regency(store.getRegency())
                .district(store.getDistrict())
                .village(store.getVillage())
                .user(store.getUser())
                .establishedSince(store.getEstablishedSince())
                .timezoneLabel(store.getTimezoneLabel())
                .build();
    }
}