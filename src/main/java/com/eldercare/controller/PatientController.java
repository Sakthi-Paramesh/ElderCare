package com.eldercare.controller;

import com.eldercare.dto.PatientProfileResponse;
import com.eldercare.dto.PatientProfileUpdateRequest;
import com.eldercare.security.CustomUserDetails;
import com.eldercare.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.getProfile(userDetails.getUser()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientProfileResponse> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PatientProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(patientService.updateProfile(userDetails.getUser(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientProfileResponse> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getProfile(patientService.getPatientById(id).getUser()));
    }
}
