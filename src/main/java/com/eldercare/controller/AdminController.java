package com.eldercare.controller;

import com.eldercare.dto.AdminStatsResponse;
import com.eldercare.dto.DoctorResponse;
import com.eldercare.dto.HospitalRequest;
import com.eldercare.entity.Hospital;
import com.eldercare.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/doctors/pending")
    public ResponseEntity<List<DoctorResponse>> getPendingDoctors() {
        return ResponseEntity.ok(adminService.getPendingDoctors());
    }

    @PatchMapping("/doctors/{id}/approve")
    public ResponseEntity<DoctorResponse> approveDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.setDoctorApproval(id, true));
    }

    @PatchMapping("/doctors/{id}/reject")
    public ResponseEntity<DoctorResponse> rejectDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.setDoctorApproval(id, false));
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @PostMapping("/hospitals")
    public ResponseEntity<Hospital> createHospital(@Valid @RequestBody HospitalRequest request) {
        return ResponseEntity.ok(adminService.createHospital(request));
    }
}
