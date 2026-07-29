package com.example.ems.service;

import com.example.ems.dto.DepartmentRequestDTO;
import com.example.ems.dto.DepartmentResponseDTO;
import com.example.ems.dto.PageResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponseDTO addDepartment(DepartmentRequestDTO departmentRequestDTO);
    List<DepartmentResponseDTO> getAllDepartments();
    PageResponse<DepartmentResponseDTO> getAllDepartments(int pageNo, int pageSize, String sortBy, String sortDir);
    DepartmentResponseDTO getDepartmentById(Long id);
    DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO departmentRequestDTO);
    String deleteDepartment(Long id);
}
