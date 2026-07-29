package com.example.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String designation;
    private Double salary;
    private LocalDate dateOfJoining;
    private String profilePicture;
    private Long departmentId;
    private String departmentName;
}
