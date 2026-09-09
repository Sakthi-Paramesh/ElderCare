package com.eldercare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileUpdateRequest {
    private String fullName;
    private String qualification;
    private Integer experienceYears;
    private String about;
    private BigDecimal consultationFee;
    private String profilePhotoUrl;
}
