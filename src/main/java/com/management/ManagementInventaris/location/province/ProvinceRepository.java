package com.management.ManagementInventaris.location.province;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, String> {

    Province findProvinceByName(String name);
}