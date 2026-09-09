package com.eldercare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordResponse {
    private Long id;
    private Long appointmentId;
    private LocalDate appointmentDate;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private String hospitalName;
    private String diagnosis;
    private String consultationNotes;
    private LocalDateTime createdAt;
    @Builder.Default
    private List<PrescriptionResponse> prescriptions = new ArrayList<>();
}
