package com.example.ems.service.impl;

import com.example.ems.dto.EmployeeRequestDTO;
import com.example.ems.dto.EmployeeResponseDTO;
import com.example.ems.dto.PageResponse;
import com.example.ems.entity.Department;
import com.example.ems.entity.Employee;
import com.example.ems.exception.DuplicateResourceException;
import com.example.ems.exception.ResourceNotFoundException;
import com.example.ems.repository.DepartmentRepository;
import com.example.ems.repository.EmployeeRepository;
import com.example.ems.service.EmployeeService;
import com.example.ems.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional
    public EmployeeResponseDTO addEmployee(EmployeeRequestDTO employeeRequestDTO) {

        log.info("Adding new employee with email: {}", employeeRequestDTO.getEmail());

        if (employeeRepository.existsByEmailIgnoreCase(employeeRequestDTO.getEmail())) {
            log.warn("Employee already exists with email: {}", employeeRequestDTO.getEmail());
            throw new DuplicateResourceException("Employee already exists with email: " + employeeRequestDTO.getEmail());
        }

        Department department = departmentRepository.findById(employeeRequestDTO.getDepartmentId())
                .orElseThrow(() -> {
                    log.warn("Department not found with id: {}", employeeRequestDTO.getDepartmentId());
                    return new ResourceNotFoundException(
                            "Department not found with id: " + employeeRequestDTO.getDepartmentId());
                });

        Employee employee = new Employee();
        mapRequestToEntity(employeeRequestDTO, employee, department);

        Employee saved = employeeRepository.save(employee);

        log.info("Employee created successfully with ID: {}", saved.getEmployeeId());

        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {

        log.info("Fetching all employees");

        List<EmployeeResponseDTO> employees = employeeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        log.info("Total employees found: {}", employees.size());

        return employees;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponseDTO> getAllEmployees(int pageNo, int pageSize,
                                                             String sortBy, String sortDir) {

        log.info("Fetching employees page={}, size={}, sortBy={}, sortDir={}",
                pageNo, pageSize, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<EmployeeResponseDTO> page = employeeRepository.findAll(pageable)
                .map(this::toResponseDTO);

        log.info("Fetched {} employees from page {}", page.getNumberOfElements(), pageNo);

        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {

        log.info("Fetching employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });

        log.info("Employee found with ID: {}", id);

        return toResponseDTO(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getEmployeesByDepartment(Long departmentId) {

        log.info("Fetching employees for department ID: {}", departmentId);

        if (!departmentRepository.existsById(departmentId)) {
            log.warn("Department not found with ID: {}", departmentId);
            throw new ResourceNotFoundException("Department not found with id: " + departmentId);
        }

        List<EmployeeResponseDTO> employees = employeeRepository
                .findByDepartment_DepartmentId(departmentId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        log.info("Found {} employees in department {}", employees.size(), departmentId);

        return employees;
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO employeeRequestDTO) {

        log.info("Updating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });

        Department department = departmentRepository.findById(employeeRequestDTO.getDepartmentId())
                .orElseThrow(() -> {
                    log.warn("Department not found with ID: {}", employeeRequestDTO.getDepartmentId());
                    return new ResourceNotFoundException(
                            "Department not found with id: " + employeeRequestDTO.getDepartmentId());
                });

        mapRequestToEntity(employeeRequestDTO, employee, department);

        Employee updated = employeeRepository.save(employee);

        log.info("Employee updated successfully with ID: {}", updated.getEmployeeId());

        return toResponseDTO(updated);
    }

    @Override
    @Transactional
    public String deleteEmployee(Long id) {

        log.info("Deleting employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });

        if (employee.getProfilePicture() != null) {
            log.info("Deleting profile picture: {}", employee.getProfilePicture());
            fileStorageService.delete(employee.getProfilePicture());
        }

        employeeRepository.delete(employee);

        log.info("Employee deleted successfully with ID: {}", id);

        return "Successfully deleted the employee with id: " + id;
    }

    @Override
    @Transactional
    public EmployeeResponseDTO uploadProfilePicture(Long id, MultipartFile file) {

        log.info("Uploading profile picture for employee ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });

        String previousPicture = employee.getProfilePicture();

        String storedFileName = fileStorageService.store(file, id);

        employee.setProfilePicture(storedFileName);

        Employee updated = employeeRepository.save(employee);

        if (previousPicture != null) {
            log.info("Deleting previous profile picture: {}", previousPicture);
            fileStorageService.delete(previousPicture);
        }

        log.info("Profile picture uploaded successfully for employee ID: {}", id);

        return toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadProfilePicture(Long id) {

        log.info("Loading profile picture for employee ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with id: " + id);
                });

        if (employee.getProfilePicture() == null) {
            log.warn("Profile picture not found for employee ID: {}", id);
            throw new ResourceNotFoundException(
                    "No profile picture uploaded for employee id: " + id);
        }

        log.info("Profile picture loaded successfully for employee ID: {}", id);

        return fileStorageService.load(employee.getProfilePicture());
    }

    private void mapRequestToEntity(EmployeeRequestDTO dto,
                                    Employee employee,
                                    Department department) {

        log.debug("Mapping EmployeeRequestDTO to Employee entity");

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setDesignation(dto.getDesignation());
        employee.setSalary(dto.getSalary());
        employee.setDateOfJoining(dto.getDateOfJoining());
        employee.setDepartment(department);
    }

    private EmployeeResponseDTO toResponseDTO(Employee employee) {

        log.debug("Converting Employee entity to EmployeeResponseDTO");

        return new EmployeeResponseDTO(
                employee.getEmployeeId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getDesignation(),
                employee.getSalary(),
                employee.getDateOfJoining(),
                employee.getProfilePicture(),
                employee.getDepartment().getDepartmentId(),
                employee.getDepartment().getDepartmentName()
        );
    }
}