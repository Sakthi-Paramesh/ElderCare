package com.eldercare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRegistrationRequest {

    @NotBlank(message = "Full Name is required")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 15, message = "Mobile number should be between 10 to 15 digits")
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String city;

    private String emergencyContactName;

    private String emergencyContactNumber;
}
