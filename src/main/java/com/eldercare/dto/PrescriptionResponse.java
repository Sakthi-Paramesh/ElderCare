package com.eldercare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {
    private Long id;
    private String medicineName;
    private String dosage;
    private String duration;
    private String instructions;
    private LocalDateTime createdAt;
}
