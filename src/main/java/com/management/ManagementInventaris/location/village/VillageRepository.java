package com.management.ManagementInventaris.location.village;

import com.management.ManagementInventaris.location.district.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillageRepository extends JpaRepository<Village, String> {

    List<Village> findAllByDistrict(District district);

    Village findVillageByDistrictAndName(District district, String name);
}