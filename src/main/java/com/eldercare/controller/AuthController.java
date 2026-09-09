package com.eldercare.controller;

import com.eldercare.dto.ApiResponse;
import com.eldercare.dto.DoctorRegistrationRequest;
import com.eldercare.dto.JwtResponse;
import com.eldercare.dto.LoginRequest;
import com.eldercare.dto.PatientRegistrationRequest;
import com.eldercare.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/patient")
    public ResponseEntity<ApiResponse> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        ApiResponse response = authService.registerPatient(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<ApiResponse> registerDoctor(@Valid @RequestBody DoctorRegistrationRequest request) {
        ApiResponse response = authService.registerDoctor(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
