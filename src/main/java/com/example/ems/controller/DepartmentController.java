package com.example.ems.controller;

import com.example.ems.dto.DepartmentRequestDTO;
import com.example.ems.dto.DepartmentResponseDTO;
import com.example.ems.dto.PageResponse;
import com.example.ems.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Validated
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> addDepartment(@Valid @RequestBody DepartmentRequestDTO departmentRequestDTO) {
        DepartmentResponseDTO response = departmentService.addDepartment(departmentRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        return new ResponseEntity<>(departmentService.getAllDepartments(), HttpStatus.OK);
    }

    @GetMapping("/paged")
    public ResponseEntity<PageResponse<DepartmentResponseDTO>> getAllDepartmentsPaged(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "departmentId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return new ResponseEntity<>(departmentService.getAllDepartments(pageNo, pageSize, sortBy, sortDir), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable Long id) {
        return new ResponseEntity<>(departmentService.getDepartmentById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@PathVariable Long id,
                                                                   @Valid @RequestBody DepartmentRequestDTO departmentRequestDTO) {
        return new ResponseEntity<>(departmentService.updateDepartment(id, departmentRequestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        return new ResponseEntity<>(departmentService.deleteDepartment(id), HttpStatus.OK);
    }
}
