package com.management.ManagementInventaris.location.province;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProvinceService {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'allProvince'")
    public List<Province> findAllProvince() {
        return provinceRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "location", key = "'getProvinceByName:' + #name")
    public Province findByName(String name) {
        Province province = provinceRepository.findProvinceByName(name);

        if (province == null) {
            throw new IllegalArgumentException("Province with Name : " + name + " Not Found");
        }

        return toProvinceResponse(province);
    }

    private Province toProvinceResponse(Province province) {
        return Province.builder()
                .id(province.getId())
                .name(province.getName())
                .build();
    }
}