package com.management.ManagementInventaris.location;

import com.management.ManagementInventaris.location.district.DistrictResponse;
import com.management.ManagementInventaris.location.district.DistrictService;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.province.ProvinceService;
import com.management.ManagementInventaris.location.regency.RegencyResponse;
import com.management.ManagementInventaris.location.regency.RegencyService;
import com.management.ManagementInventaris.location.village.VillageResponse;
import com.management.ManagementInventaris.location.village.VillageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location/indonesia")
public class LocationController {

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private RegencyService regencyService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private VillageService villageService;

    // Start - Configuration Route Province
    @GetMapping(path = "/province/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Province>> findAllProvince() {
        return ResponseEntity.ok().body(provinceService.findAllProvince());
    }

    @GetMapping(path = "/province/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Province> findByName(@PathVariable String name) {
        Province response = provinceService.findByName(name);
        return ResponseEntity.ok().body(response);
    }

    // End - Configuration Route Province

    // Start Configuration Route Regency
    @GetMapping(path = "/regencyList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RegencyResponse>> findAllRegencies() {
        List<RegencyResponse> responses = regencyService.findAllRegencies();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping(path = "/{provinceName}/regencyAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RegencyResponse>> getAllRegenciesByNameProvinceName(@PathVariable String provinceName) {
        List<RegencyResponse> responses = regencyService.getAllRegenciesByNameProvinceName(provinceName);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping(path = "/{provinceName}/{regencyName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegencyResponse> getNameRegency(
            @PathVariable String provinceName,
            @PathVariable String regencyName
    ) {
        RegencyResponse response = regencyService.findByName(provinceName, regencyName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // End Configuration Route Regency

    // Start Configuration Route District
    @GetMapping(path = "/districtList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DistrictResponse>> findAllDistricts() {
        List<DistrictResponse> responses = districtService.getAllDistrictsByProvince();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/{provinceName}/{regencyName}/districtAll")
    public ResponseEntity<List<DistrictResponse>> getAllDistrictsByRegency(
            @PathVariable String provinceName,
            @PathVariable String regencyName
    ) {
        List<DistrictResponse> response = districtService.getAllDistrictsByRegency(provinceName, regencyName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{provinceName}/{regencyName}/{districtName}")
    public ResponseEntity<DistrictResponse> getDistrictByName(
            @PathVariable String provinceName,
            @PathVariable String regencyName,
            @PathVariable String districtName
    ) {
        DistrictResponse response = districtService.findDistrictByName(provinceName, regencyName, districtName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // End Configuration Route District

    // Start Configuration Route Village
    @GetMapping(path = "/villageList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VillageResponse>> findAllByVillage() {
        List<VillageResponse> responses = villageService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
    @GetMapping("/{provinceName}/{regencyName}/{districtName}/villageAll")
    public ResponseEntity<List<VillageResponse>> getAllVillagesByDistrict(
            @PathVariable String provinceName,
            @PathVariable String regencyName,
            @PathVariable String districtName
    ) {
        List<VillageResponse> response = villageService.getAllVillagesByDistrict(provinceName, regencyName, districtName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{provinceName}/{regencyName}/{districtName}/{villageName}")
    public ResponseEntity<VillageResponse> getVillageByDistrictAndName(
            @PathVariable String provinceName,
            @PathVariable String regencyName,
            @PathVariable String districtName,
            @PathVariable String villageName
    ) {
        VillageResponse response = villageService.getVillageByDistrictAndName(provinceName, regencyName, districtName, villageName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // End Configuration Route Village
}