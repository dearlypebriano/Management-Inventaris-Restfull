package com.management.ManagementInventaris.store;

import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.district.DistrictService;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.province.ProvinceService;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.regency.RegencyService;
import com.management.ManagementInventaris.location.village.Village;
import com.management.ManagementInventaris.location.village.VillageService;
import com.management.ManagementInventaris.user.Role;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserRepository;
import com.management.ManagementInventaris.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreAccountingRepository storeAccountingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private RegencyService regencyService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private VillageService villageService;

    @Autowired
    private UserDetailToken userDetailToken;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @CacheEvict(value = "store", allEntries = true)
    @CachePut(value = "store", key = "#result.id")
    public StoreResponse createNewStore(StoreRequest request) {
        User user = userDetailToken.dataUserEmail();
        Province province = provinceService.findByName(user.getProvince().getName());
        Regency regency = regencyService.findByNames(province.getName(), user.getRegency().getName());
        District district = districtService.findDistrictByNames(province.getName(), regency.getName(), user.getDistrict().getName());
        Village village = villageService.getVillageByDistrictAndNames(province.getName(), regency.getName(), district.getName(), user.getVillage().getName());

        Store store = new Store();
        store.setId(UUID.randomUUID().toString());
        store.setStoreName(request.getStoreName());
        store.setProvince(province);
        store.setRegency(regency);
        store.setDistrict(district);
        store.setVillage(village);
        store.setStreet(request.getStreet());
        store.setUser(user);

        storeRepository.save(store);
        StoreDTO storeDTO = StoreDTO.fromEntity(store);
        redisTemplate.opsForValue().set("store:" + storeDTO.getId(), storeDTO);

        return toStoreResponse(store);
    }

    @Transactional
    @CacheEvict(value = "store", allEntries = true)
    @CachePut(value = "store", key = "#result.id")
    public StoreResponse updateStore(String storeId, StoreRequest request) {
        User user = userDetailToken.dataUserEmail();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store with id " + storeId + " does not exist"));

        if (!store.getUser().getEmail().equals(user.getEmail()) && !(user.getRole().name().equals(Role.ADMIN) || user.getRole().name().equals(Role.MANAGER))) throw new IllegalStateException("You cannot update the store!");

        if (request.getStoreName() != null) store.setStoreName(request.getStoreName());
        if (request.getStreet() != null) store.setStreet(request.getStreet());

        storeRepository.save(store);
        StoreDTO storeDTO = StoreDTO.fromEntity(store);
        redisTemplate.opsForValue().set("store:" + storeDTO.getId(), storeDTO);

        return toStoreResponse(store);
    }

    @Transactional
    @CacheEvict(value = "store", allEntries = true)
    public void deleteStore(String storeId) {
        User user = userDetailToken.dataUserEmail();

        Store store = storeRepository.findById(storeId)
               .orElseThrow(() -> new IllegalArgumentException("Store with id " + storeId + " does not exist"));

        if (!store.getUser().getEmail().equals(user.getEmail()) || user.getRole().name().equals(Role.USER)) throw new IllegalArgumentException("User with email " + user.getEmail() + " does not author in the store");

        storeRepository.delete(store);

        String redisKey = "store:" + store.getId();
        redisTemplate.delete(redisKey);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "store", key = "'getStoreById:' + #id")
    public StoreResponse findStoreById(String storeId) {
        Store store = storeRepository.findById(storeId)
               .orElseThrow(() -> new IllegalArgumentException("Store with id " + storeId + " does not exist"));

        return toStoreResponse(store);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "store", key = "#page + '-' + #size")
    public WebResponse<List<StoreResponse>> findAllStore(int page, int size) {
        int offset = page * size;
        List<Store> stores = storeRepository.findAllWithPagination(offset, size);

        List<StoreResponse> storeResponses = stores.stream()
                .map(this::toStoreResponse)
                .toList();

        CalculatePages calculatePages = new CalculatePages(storeRepository.count(), size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(calculatePages.calculateTotalPages())
                .size(storeResponses.size())
                .build();

        return WebResponse.<List<StoreResponse>>builder()
                .data(storeResponses)
                .paging(pagingResponse)
                .build();
    }

    @Transactional
    @CacheEvict(value = "store", allEntries = true)
    @CachePut(value = "store", key = "'storeAccounting:' + #result.id")
    public StoreAccountingResponse recordDailyIncome(double dailyIncome) {
        User user = userDetailToken.dataUserEmail();
        Store store = storeRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Tidak ada store terkait dengan email ini"));

        StoreAccounting storeAccounting = StoreAccounting.builder()
                .id(UUID.randomUUID().toString())
                .store(store)
                .incomeDate(DateTimeUtil.formatToIndonesianDateWithDay(LocalDate.now()))
                .dailyIncome(dailyIncome)
                .savingPercentage(0.1)
                .build();

        storeAccountingRepository.save(storeAccounting);

        StoreAccountingDTO storeAccountingDTO = StoreAccountingDTO.fromEntity(storeAccounting);
        redisTemplate.opsForValue().set("store-accounting:" + storeAccountingDTO.getId(), storeAccountingDTO);

        return toStoreAccountingResponse(storeAccounting);
    }

    @Transactional
    @CacheEvict(value = "store", allEntries = true)
    @CachePut(value = "store", key = "'storeAccounting:' + #result.id")
    public StoreAccountingResponse updateRecordDailyIncome(double dailyIncome) {
        User user = userDetailToken.dataUserEmail();
        Store store = storeRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Tidak ada store terkait dengan email ini"));

        if (!store.getUser().getEmail().equals(user.getEmail())) throw new IllegalArgumentException("This store not for you");

        StoreAccounting storeAccounting  = new StoreAccounting();
        if (dailyIncome != 0) storeAccounting.setDailyIncome(dailyIncome);
        storeAccountingRepository.save(storeAccounting);

        StoreAccountingDTO storeAccountingDTO = StoreAccountingDTO.fromEntity(storeAccounting);
        redisTemplate.opsForValue().set("store-accounting:" + storeAccountingDTO.getId(), storeAccountingDTO);

        return toStoreAccountingResponse(storeAccounting);
    }

    @Transactional
    @CacheEvict(value = "store", allEntries = true)
    public void deleteStoreAccounting(String storeAccountingId) {
        User user = userDetailToken.dataUserEmail();
        StoreAccounting storeAccounting = storeAccountingRepository.findById(storeAccountingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store accounting not found"));

        if (!storeAccounting.getStore().getUser().equals(user) || storeAccounting.getStore().getUser().getRole().name().equals(Role.USER)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Anda tidak memiliki akses untuk menghapus store accounting tersebut!");

        storeAccountingRepository.delete(storeAccounting);
        String redisKey = "store-accounting:" + storeAccounting.getId();
        redisTemplate.delete(redisKey);
    }

    @Transactional
    @Cacheable(value = "store", key = "'storeAccounting:' + #page + '-' + #size")
    public WebResponse<List<StoreAccountingResponse>> findAllStoreAccounting(int page, int size) {
        int offset = page * size;
        List<StoreAccounting> storeAccountings = storeAccountingRepository.findAllWithPagination(offset, size);

        List<StoreAccountingResponse> storeAccountingResponses = storeAccountings.stream()
                .map(this::toStoreAccountingResponse)
                .toList();

        CalculatePages calculatePages = new CalculatePages(storeAccountingRepository.count(), size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(calculatePages.calculateTotalPages())
                .size(storeAccountingResponses.size())
                .build();

        return WebResponse.<List<StoreAccountingResponse>>builder()
                .data(storeAccountingResponses)
                .paging(pagingResponse)
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "store", key = "'getStoreAccountingById:' + #id")
    public StoreAccountingResponse getStoreAccountingResponse(String storeAccountingId) {
        StoreAccounting storeAccounting = storeAccountingRepository.findById(storeAccountingId)
               .orElseThrow(() -> new IllegalArgumentException("Store accounting with id " + storeAccountingId + " does not exist"));

        return toStoreAccountingResponse(storeAccounting);
    }

    private StoreAccountingResponse toStoreAccountingResponse(StoreAccounting storeAccounting) {
        String dailyIncomes = CurrencyFormatter.formatIDR(BigDecimal.valueOf(storeAccounting.getDailyIncome()));
        String savings = CurrencyFormatter.formatIDR(BigDecimal.valueOf(storeAccounting.calculateSavings()));
        String newStockBudget = CurrencyFormatter.formatIDR(BigDecimal.valueOf(storeAccounting.calculateNewStockBudget()));
        String formatSavingPercentage = String.format("%.0f%%", storeAccounting.getSavingPercentage() * 100);

        String encId = "";
        try {
            encId = Cryptographic.encrypt(storeAccounting.getId());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        return StoreAccountingResponse.builder()
                .id(encId)
                .storeName(storeAccounting.getStore().getStoreName())
                .incomeDate(storeAccounting.getIncomeDate())
                .dailyIncome(dailyIncomes)
                .savings(savings)
                .savingPercentage(formatSavingPercentage)
                .newStockBudget(newStockBudget)
                .build();
    }

    private StoreResponse toStoreResponse(Store store) {
        String location = String.format("%s", capitalizeFirstLetter(store.getUser().getRegency().getName()));

        String encId = "";
        try {
            encId = Cryptographic.encrypt(store.getId());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        return StoreResponse.builder()
                .id(encId)
                .storeName(store.getStoreName())
                .branch(location)
                .street(store.getStreet())
                .sales(store.getUser().displayName())
                .establishedSince(store.getEstablishedSince())
                .build();
    }

    private String capitalizeFirstLetter(String word) {
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}