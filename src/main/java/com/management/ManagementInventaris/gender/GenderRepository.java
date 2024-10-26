package com.management.ManagementInventaris.gender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Integer> {

    @Query(value = "SELECT * FROM gender ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Gender> findAllWithPagination(int offset, int size);

    Optional<Gender> findByName(String name);
}