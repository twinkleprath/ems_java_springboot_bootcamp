package com.example.ems.repository;

import com.example.ems.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Employee> findByDepartment_DepartmentId(Long departmentId);
    List<Employee> findByDesignationContainingIgnoreCase(String designation);
}
