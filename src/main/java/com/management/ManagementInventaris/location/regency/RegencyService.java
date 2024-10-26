package com.management.ManagementInventaris.location.regency;

import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.province.ProvinceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegencyService {

    @Autowired
    private RegencyRepository regencyRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'getAllRegenciesByProvinceName:' + #provinceName")
    public List<RegencyResponse> getAllRegenciesByNameProvinceName(String provinceName) {
        List<Regency> regencies = regencyRepository.findByProvinceName(provinceName);

        if (regencies.isEmpty()) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found");
        }

        return regencies.stream()
                .map(this::toRegencyResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'getRegenciesByProvinceNameAndRegencyName:' + #provinceName + '-' + #regencyName")
    public RegencyResponse findByName(String provinceName, String regencyName) {
        Province province = provinceRepository.findProvinceByName(provinceName);

        Regency regency = regencyRepository.findByProvinceNameAndName(provinceName, regencyName);

        if (province == null || regency == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found! And Regency With Name: " + regencyName + " Not Found!");
        }

        return toRegencyResponse(regency);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'getDRegenciesByProvinceNameAndRegencyName:' + #provinceName + ' - ' + #regencyName")
    public Regency findByNames(String provinceName, String regencyName) {
        Province province = provinceRepository.findProvinceByName(provinceName);

        Regency regency = regencyRepository.findByProvinceNameAndName(provinceName, regencyName);

        if (province == null || regency == null) {
            throw new IllegalArgumentException("Province with Name : " + provinceName + " Not Found! And Regency With Name: " + regencyName + " Not Found!");
        }

        return regency;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'allRegencies'")
    public List<RegencyResponse> findAllRegencies() {
        List<Regency> regencies = regencyRepository.findAll();

        return regencies.stream().map(this::toRegencyResponse).collect(Collectors.toList());
    }

    private RegencyResponse toRegencyResponse(Regency regency) {
        return RegencyResponse.builder()
                .id(regency.getId())
                .province(regency.getProvince().getName())
                .name(regency.getName())
                .build();
    }
}