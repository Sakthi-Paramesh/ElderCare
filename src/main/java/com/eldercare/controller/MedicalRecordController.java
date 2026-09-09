package com.eldercare.controller;

import com.eldercare.dto.MedicalRecordRequest;
import com.eldercare.dto.MedicalRecordResponse;
import com.eldercare.security.CustomUserDetails;
import com.eldercare.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordResponse> createRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MedicalRecordRequest request
    ) {
        return ResponseEntity.ok(medicalRecordService.createMedicalRecord(userDetails.getUser(), request));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordResponse> getByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(medicalRecordService.getByAppointmentId(appointmentId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsForPatient(patientId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<MedicalRecordResponse>> getMyRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(medicalRecordService.getRecordsForCurrentPatient(userDetails.getUser()));
    }
}
