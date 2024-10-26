package com.management.ManagementInventaris.user;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * This method is used to retrieve a paginated list of data user.
     * It uses a native SQL query to perform the pagination.
     *
     * @param offset The offset for pagination. It represents the starting index of the user data to retrieve.
     * @param size   The size of the page. It represents the number of users to retrieve per page.
     * @return A list of users retrieved based on the provided offset and size.
     */
    @Query(value = "SELECT * FROM _user ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<User> findAllWithPagination(int offset, int size);

    @Query(value = "SELECT * FROM _user WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    List<User> findByProvinceNameAndRegencyName(String provinceName, String regencyName);
}