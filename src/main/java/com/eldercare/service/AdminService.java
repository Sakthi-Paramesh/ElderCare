package com.eldercare.service;

import com.eldercare.dto.AdminStatsResponse;
import com.eldercare.dto.DoctorResponse;
import com.eldercare.dto.HospitalRequest;
import com.eldercare.entity.AppointmentStatus;
import com.eldercare.entity.Doctor;
import com.eldercare.entity.Hospital;
import com.eldercare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorService doctorService;
    private final NotificationService notificationService;

    public List<DoctorResponse> getPendingDoctors() {
        return doctorRepository.findByIsApprovedFalse().stream()
                .map(doctorService::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DoctorResponse setDoctorApproval(Long doctorId, boolean approve) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        doctor.setApproved(approve);
        Doctor updated = doctorRepository.save(doctor);

        if (doctor.getUser() != null) {
            String msg = approve
                    ? "Congratulations! Your Doctor account on ElderCare Connect has been approved by the Admin."
                    : "Your Doctor account approval request was rejected by the Admin.";
            notificationService.createNotification(doctor.getUser(), msg);
        }

        return doctorService.mapToDoctorResponse(updated);
    }

    public AdminStatsResponse getStats() {
        return AdminStatsResponse.builder()
                .totalPatients(patientRepository.count())
                .totalDoctors(doctorRepository.countByIsApprovedTrue())
                .pendingDoctors(doctorRepository.countByIsApprovedFalse())
                .totalAppointments(appointmentRepository.count())
                .completedAppointments(appointmentRepository.countByStatus(AppointmentStatus.COMPLETED))
                .totalHospitals(hospitalRepository.count())
                .totalDepartments(departmentRepository.count())
                .build();
    }

    @Transactional
    public Hospital createHospital(HospitalRequest request) {
        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .phone(request.getPhone())
                .email(request.getEmail())
                .about(request.getAbout())
                .imageUrl(request.getImageUrl())
                .openingHours(request.getOpeningHours() != null ? request.getOpeningHours() : "Mon-Sun, 24 Hours")
                .build();
        return hospitalRepository.save(hospital);
    }
}
