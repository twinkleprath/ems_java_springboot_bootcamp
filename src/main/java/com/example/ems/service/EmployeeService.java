package com.example.ems.service;

import com.example.ems.dto.EmployeeRequestDTO;
import com.example.ems.dto.EmployeeResponseDTO;
import com.example.ems.dto.PageResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO addEmployee(EmployeeRequestDTO employeeRequestDTO);
    List<EmployeeResponseDTO> getAllEmployees();
    PageResponse<EmployeeResponseDTO> getAllEmployees(int pageNo, int pageSize, String sortBy, String sortDir);
    EmployeeResponseDTO getEmployeeById(Long id);
    List<EmployeeResponseDTO> getEmployeesByDepartment(Long departmentId);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO employeeRequestDTO);
    String deleteEmployee(Long id);
    EmployeeResponseDTO uploadProfilePicture(Long id, MultipartFile file);
    Resource loadProfilePicture(Long id);
}
