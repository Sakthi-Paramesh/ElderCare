package com.eldercare.service;

import com.eldercare.dto.PatientProfileResponse;
import com.eldercare.dto.PatientProfileUpdateRequest;
import com.eldercare.entity.Patient;
import com.eldercare.entity.User;
import com.eldercare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Patient getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found for user: " + userId));
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    public PatientProfileResponse getProfile(User user) {
        Patient patient = getPatientByUserId(user.getId());
        return mapToResponse(patient);
    }

    @Transactional
    public PatientProfileResponse updateProfile(User user, PatientProfileUpdateRequest request) {
        Patient patient = getPatientByUserId(user.getId());
        patient.setFullName(request.getFullName());
        if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());
        if (request.getCity() != null) patient.setCity(request.getCity());
        if (request.getEmergencyContactName() != null) patient.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactNumber() != null) patient.setEmergencyContactNumber(request.getEmergencyContactNumber());
        if (request.getProfilePhotoUrl() != null) patient.setProfilePhotoUrl(request.getProfilePhotoUrl());

        return mapToResponse(patientRepository.save(patient));
    }

    private PatientProfileResponse mapToResponse(Patient patient) {
        return PatientProfileResponse.builder()
                .id(patient.getId())
                .userId(patient.getUser() != null ? patient.getUser().getId() : null)
                .email(patient.getUser() != null ? patient.getUser().getEmail() : "")
                .fullName(patient.getFullName())
                .mobileNumber(patient.getMobileNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .city(patient.getCity())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactNumber(patient.getEmergencyContactNumber())
                .profilePhotoUrl(patient.getProfilePhotoUrl())
                .build();
    }
}
