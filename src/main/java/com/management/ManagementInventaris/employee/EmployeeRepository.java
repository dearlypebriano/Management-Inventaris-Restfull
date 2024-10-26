package com.management.ManagementInventaris.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query("SELECT e FROM Employee e JOIN e.job j WHERE j.jobName LIKE %:jobName%")
    List<Employee> findAllByJobNameContaining(@Param("jobName") String jobName);

    @Modifying
    @Query("UPDATE Employee e SET e.job = null WHERE e.job.jobName LIKE %:jobName%")
    void clearJobByJobNameContaining(@Param("jobName") String jobName);

    @Query("SELECT e FROM Employee e ORDER BY e.id ASC")
    List<Employee> findAllWithPagination(int offset, int size);

    Employee findByNip(Integer nip);
}