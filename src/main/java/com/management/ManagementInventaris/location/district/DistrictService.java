package com.management.ManagementInventaris.location.district;

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
public class DistrictService {

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private RegencyRepository regencyRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'getAllDistrictByProvinceNameAndRegencyName:' + #provinceName + ' - ' + #regencyName")
    public List<DistrictResponse> getAllDistrictsByRegency(String provinceName, String regencyName) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found");
        }

        Regency regency = regencyRepository.findRegencyByName(regencyName);
        if (regency == null) {
            throw new IllegalArgumentException("Regency with Name : " + regencyName + " Not Found");
        }

        List<District> districts = districtRepository.findAllByRegency(regency);

        return districts.stream()
                .map(this::toDistrictResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'getDistrictByProvinceNameAndRegencyNameAndDistrictName:' + #provinceName + ' - ' + #regencyName + ' - ' + #districtName")
    public DistrictResponse findDistrictByName(String provinceName, String regencyName, String districtName) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        Regency regency = regencyRepository.findRegencyByName(regencyName);

        if (province == null || regency == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " or Regency with Name: " + regencyName + " Not Found!");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);

        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        return toDistrictResponse(district);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'findDistrictByName:' + #provinceName + ' - ' + #regencyName + ' - ' + #districtName")
    public District findDistrictByNames(String provinceName, String regencyName, String districtName) {
        Province province = provinceRepository.findProvinceByName(provinceName);
        Regency regency = regencyRepository.findRegencyByName(regencyName);

        if (province == null || regency == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " or Regency with Name: " + regencyName + " Not Found!");
        }

        District district = districtRepository.findDistrictByRegencyAndName(regency, districtName);

        if (district == null) {
            throw new IllegalArgumentException("District with Name : " + districtName + " Not Found in Regency: " + regencyName);
        }

        return district;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'allDistrict'")
    public List<DistrictResponse> getAllDistrictsByProvince() {
        List<District> districts = districtRepository.findAll();

        return districts.stream()
                .map(this::toDistrictResponse)
                .collect(Collectors.toList());
    }

    private DistrictResponse toDistrictResponse(District district) {
        return DistrictResponse.builder()
                .id(district.getId())
                .regency(district.getRegency().getName())
                .name(district.getName())
                .build();
    }
}