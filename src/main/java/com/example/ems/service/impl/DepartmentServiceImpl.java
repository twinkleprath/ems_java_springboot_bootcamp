package com.example.ems.service.impl;

import com.example.ems.dto.DepartmentRequestDTO;
import com.example.ems.dto.DepartmentResponseDTO;
import com.example.ems.dto.PageResponse;
import com.example.ems.entity.Department;
import com.example.ems.exception.DuplicateResourceException;
import com.example.ems.exception.ResourceNotFoundException;
import com.example.ems.repository.DepartmentRepository;
import com.example.ems.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DepartmentResponseDTO addDepartment(DepartmentRequestDTO departmentRequestDTO) {
        if (departmentRepository.existsByDepartmentNameIgnoreCase(departmentRequestDTO.getDepartmentName())) {
            throw new DuplicateResourceException("Department already exists with name: " + departmentRequestDTO.getDepartmentName());
        }
        Department department = new Department();
        department.setDepartmentName(departmentRequestDTO.getDepartmentName());
        department.setDescription(departmentRequestDTO.getDescription());
        Department saved = departmentRepository.save(department);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponseDTO> getAllDepartments(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<DepartmentResponseDTO> page = departmentRepository.findAll(pageable).map(this::toResponseDTO);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return toResponseDTO(department);
    }

    @Override
    @Transactional
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO departmentRequestDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        department.setDepartmentName(departmentRequestDTO.getDepartmentName());
        department.setDescription(departmentRequestDTO.getDescription());
        Department updated = departmentRepository.save(department);
        return toResponseDTO(updated);
    }

    @Override
    @Transactional
    public String deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        departmentRepository.delete(department);
        return "Successfully deleted the department with id: " + id;
    }

    private DepartmentResponseDTO toResponseDTO(Department department) {
        return new DepartmentResponseDTO(
                department.getDepartmentId(),
                department.getDepartmentName(),
                department.getDescription(),
                department.getEmployees() == null ? 0 : department.getEmployees().size()
        );
    }
}
