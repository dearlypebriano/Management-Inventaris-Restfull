package com.management.ManagementInventaris.job;

import com.management.ManagementInventaris.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {

    @Query(value = "SELECT * FROM jobs ORDER BY id LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Job> findAllWithPagination(int offset, int size);

    List<Job> findByJobNameContaining(String jobName);

    Optional<Job> findByJobName(String jobName);
}