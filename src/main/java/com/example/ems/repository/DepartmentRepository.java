package com.example.ems.repository;

import com.example.ems.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findByDepartmentNameIgnoreCase(String departmentName);
    boolean existsByDepartmentNameIgnoreCase(String departmentName);
}
