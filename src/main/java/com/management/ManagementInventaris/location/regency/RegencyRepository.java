package com.management.ManagementInventaris.location.regency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegencyRepository extends JpaRepository<Regency, String> {

    List<Regency> findByProvinceName(String provinceName);

    Regency findByProvinceNameAndName(String provinceName, String regencyName);

    Regency findProvinceByName(String name);

    Regency findRegencyByName(String regencyName);

    Optional<Regency> findById(char newId);
}