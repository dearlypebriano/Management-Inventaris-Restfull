package com.management.ManagementInventaris.location.village;

import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.district.DistrictRepository;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.province.ProvinceRepository;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.regency.RegencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VillageService {

    @Autowired
    private VillageRepository villageRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private RegencyRepository regencyRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'getAllVillagesByDistrict:' + #provinceName + ' - ' + #regencyName + ' - ' + #districtName")
    public List<VillageResponse> getAllVillagesByDistrict(String provinceName, String regencyName, String districtName) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found");
        }

        Regency regency = regencyRepository.findRegencyByName(regencyName);
        if (regency == null) {
            throw new IllegalArgumentException("Regency with Name : " + regencyName + " Not Found");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);
        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        List<Village> villages = villageRepository.findAllByDistrict(district);

        return villages.stream()
                .map(this::toVillageResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'getAllVillagesByDistrictAndName:' + #provinceName + ' - ' + #regencyName + ' - ' + #districtName + ' - ' + #name")
    public VillageResponse getVillageByDistrictAndName(String provinceName, String regencyName, String districtName, String name) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found");
        }

        Regency regency = regencyRepository.findRegencyByName(regencyName);
        if (regency == null) {
            throw new IllegalArgumentException("Regency with Name : " + regencyName + " Not Found");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);
        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        Village village = villageRepository.findVillageByDistrictAndName(district, name);
        if (village == null) {
            throw new IllegalArgumentException("Village with Name : " + name + " Not Found in District: " + districtName);
        }

        return toVillageResponse(village);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'getAllVillagesByDistrictAndNames:' + #provinceName + ' - ' + #regencyName + ' - ' + #districtName + ' - ' + #name")
    public Village getVillageByDistrictAndNames(String provinceName, String regencyName, String districtName, String name) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found");
        }

        Regency regency = regencyRepository.findRegencyByName(regencyName);
        if (regency == null) {
            throw new IllegalArgumentException("Regency with Name : " + regencyName + " Not Found");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);
        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        Village village = villageRepository.findVillageByDistrictAndName(district, name);
        if (village == null) {
            throw new IllegalArgumentException("Village with Name : " + name + " Not Found in District: " + districtName);
        }

        return village;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'allVillage'")
    public List<VillageResponse> findAll() {
        List<Village> villages = villageRepository.findAll();

        return villages.stream()
                .map(this::toVillageResponse)
                .collect(Collectors.toList());
    }

    private VillageResponse toVillageResponse(Village village) {
        return VillageResponse.builder()
                .id(village.getId())
                .district(village.getDistrict().getName())
                .name(village.getName())
                .build();
    }
}