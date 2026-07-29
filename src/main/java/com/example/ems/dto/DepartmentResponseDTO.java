package com.example.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDTO {
    private Long departmentId;
    private String departmentName;
    private String description;
    private int employeeCount;
}
