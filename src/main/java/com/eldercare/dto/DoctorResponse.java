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
public class DoctorResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private Long departmentId;
    private String departmentName;
    private String qualification;
    private Integer experienceYears;
    private String about;
    private String profilePhotoUrl;
    private Long hospitalId;
    private String hospitalName;
    private String hospitalCity;
    private BigDecimal consultationFee;
    private boolean isApproved;
}
