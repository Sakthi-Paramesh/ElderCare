package com.eldercare.controller;

import com.eldercare.dto.AppointmentBookingRequest;
import com.eldercare.dto.AppointmentResponse;
import com.eldercare.dto.AppointmentStatusUpdateRequest;
import com.eldercare.entity.AppointmentStatus;
import com.eldercare.security.CustomUserDetails;
import com.eldercare.service.AppointmentService;
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
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AppointmentBookingRequest request
    ) {
        AppointmentResponse response = appointmentService.bookAppointment(userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(userDetails.getUser()));
    }

    @GetMapping("/doctor/my")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForDoctor(userDetails.getUser(), date, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AppointmentStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, userDetails.getUser(), request.getStatus()));
    }
}
