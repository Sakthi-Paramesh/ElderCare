package com.eldercare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItemRequest {

    @NotBlank(message = "Medicine name is required")
    private String medicineName;

    private String dosage; // e.g. "1-0-1 (After meals)"

    private String duration; // e.g. "5 days", "1 month"

    private String instructions; // e.g. "Take with warm water before sleeping"
}
