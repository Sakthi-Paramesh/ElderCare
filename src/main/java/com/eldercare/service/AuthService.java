package com.eldercare.service;

import com.eldercare.dto.*;
import com.eldercare.entity.*;
import com.eldercare.repository.*;
import com.eldercare.security.CustomUserDetails;
import com.eldercare.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);
        User user = userDetails.getUser();

        String name = "";
        if (user.getRole() == Role.ROLE_PATIENT) {
            Patient patient = patientRepository.findByUserId(user.getId()).orElse(null);
            if (patient != null) name = patient.getFullName();
        } else if (user.getRole() == Role.ROLE_DOCTOR) {
            Doctor doctor = doctorRepository.findByUserId(user.getId()).orElse(null);
            if (doctor != null) name = doctor.getFullName();
        } else {
            name = "Admin";
        }

        return JwtResponse.builder()
                .token(jwt)
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(name)
                .build();
    }

    @Transactional
    public ApiResponse registerPatient(PatientRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email is already taken!");
        }
        if (patientRepository.existsByMobileNumber(request.getMobileNumber())) {
            return new ApiResponse(false, "Mobile number is already registered!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_PATIENT)
                .isActive(true)
                .build();

        Patient patient = Patient.builder()
                .user(user)
                .fullName(request.getFullName())
                .mobileNumber(request.getMobileNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .city(request.getCity())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactNumber(request.getEmergencyContactNumber())
                .build();

        patientRepository.save(patient);
        return new ApiResponse(true, "Patient registered successfully");
    }

    @Transactional
    public ApiResponse registerDoctor(DoctorRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email is already taken!");
        }

        Department specialization = departmentRepository.findById(request.getSpecializationId())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));
        
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_DOCTOR)
                .isActive(true)
                .build();

        Doctor doctor = Doctor.builder()
                .user(user)
                .fullName(request.getFullName())
                .specialization(specialization)
                .qualification(request.getQualification())
                .experienceYears(request.getExperienceYears())
                .hospital(hospital)
                .consultationFee(request.getConsultationFee())
                .isApproved(false) // Needs admin approval
                .build();

        doctorRepository.save(doctor);
        return new ApiResponse(true, "Doctor registered successfully. Awaiting Admin approval.");
    }
}
