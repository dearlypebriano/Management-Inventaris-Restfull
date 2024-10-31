package com.management.ManagementInventaris.store;

import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.village.Village;
import com.management.ManagementInventaris.store.review.ReviewStore;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.utils.DateTimeUtil;
import com.management.ManagementInventaris.utils.Zone;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "store", indexes = {
        @Index(name = "idx_store_name", columnList = "store_name")
})
public class Store implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @Column(name = "store_name", unique = true, nullable = false)
    private String storeName;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne
    @JoinColumn(name = "regency_id")
    private Regency regency;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "village_id", referencedColumnName = "id")
    private Village village;

    @Column(name = "street", nullable = false, unique = true)
    private String street;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Column(name = "established_since", nullable = false)
    private String establishedSince;

    @Column(name = "timezone_label", nullable = false)
    private String timezoneLabel;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StoreAccounting> storeAccountings = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewStore> reviews = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        String dateTime = DateTimeUtil.getCurrentDateTime(ZoneId.systemDefault());
        ZonedDateTime time = ZonedDateTime.now();
        String zoneId = Zone.getZoneLabel(time);

        this.establishedSince = dateTime;
        this.timezoneLabel = zoneId;
    }
}