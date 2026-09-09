package com.eldercare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileUpdateRequest {

    @NotBlank(message = "Full Name is required")
    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String city;

    private String emergencyContactName;

    private String emergencyContactNumber;

    private String profilePhotoUrl;
}
