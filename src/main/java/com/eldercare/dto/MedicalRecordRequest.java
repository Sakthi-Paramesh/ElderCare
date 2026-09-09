package com.eldercare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordRequest {

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String consultationNotes;

    @Builder.Default
    private List<PrescriptionItemRequest> prescriptions = new ArrayList<>();
}
