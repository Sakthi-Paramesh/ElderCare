package com.eldercare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileResponse {
    private Long id;
    private Long userId;
    private String email;
    private String fullName;
    private String mobileNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String profilePhotoUrl;
}
