package com.eldercare.controller;

import com.eldercare.dto.*;
import com.eldercare.entity.Doctor;
import com.eldercare.entity.DoctorAvailability;
import com.eldercare.security.CustomUserDetails;
import com.eldercare.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getDoctors(
            @RequestParam(required = false) Long specializationId,
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name
    ) {
        return ResponseEntity.ok(doctorService.searchDoctors(specializationId, hospitalId, city, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorResponseById(id));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<DoctorAvailability>> getDoctorAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getAvailability(id));
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<AvailableSlotResponse> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(doctorService.getAvailableSlots(id, date));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        return ResponseEntity.ok(doctorService.mapToDoctorResponse(doctor));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DoctorProfileUpdateRequest request
    ) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        return ResponseEntity.ok(doctorService.updateProfile(doctor.getId(), request));
    }

    @PostMapping("/availability")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<DoctorAvailability>> setAvailability(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody List<DoctorAvailabilityRequest> requestList
    ) {
        Doctor doctor = doctorService.getDoctorByUserId(userDetails.getUser().getId());
        return ResponseEntity.ok(doctorService.setAvailability(doctor.getId(), requestList));
    }
}
