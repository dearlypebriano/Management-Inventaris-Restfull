package com.management.ManagementInventaris.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {

    @Query(value = "SELECT * FROM store ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Store> findAllWithPagination(int offset, int size);

    Optional<Store> findByUserEmail(String email);

    @Query("SELECT s FROM Store s " +
            "JOIN s.province p " +
            "JOIN s.regency r " +
            "JOIN s.district d " +
            "JOIN s.village v " +
            "WHERE (:provinceName IS NULL OR p.name = :provinceName) " +
            "AND (:regencyName IS NULL OR r.name = :regencyName) " +
            "AND (:districtName IS NULL OR d.name = :districtName) " +
            "AND (:villageName IS NULL OR v.name = :villageName)")
    List<Store> findStoresByLocation(
            @Param("provinceName") String provinceName,
            @Param("regencyName") String regencyName,
            @Param("districtName") String districtName,
            @Param("villageName") String villageName
    );
}