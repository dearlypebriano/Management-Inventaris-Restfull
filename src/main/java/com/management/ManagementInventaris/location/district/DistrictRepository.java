package com.management.ManagementInventaris.location.district;

import com.management.ManagementInventaris.location.regency.Regency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {

    List<District> findAllByRegency(Regency regency);

    District findDistrictByRegencyAndName(Regency regency, String name);
}