package com.eldercare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoctorRegistrationRequest {

    @NotBlank(message = "Full Name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull(message = "Specialization ID is required")
    private Long specializationId;

    private String qualification;

    @NotNull(message = "Experience years is required")
    private Integer experienceYears;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;

    @NotNull(message = "Consultation fee is required")
    private BigDecimal consultationFee;
}
