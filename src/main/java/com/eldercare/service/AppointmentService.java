package com.eldercare.service;

import com.eldercare.dto.AppointmentBookingRequest;
import com.eldercare.dto.AppointmentResponse;
import com.eldercare.entity.*;
import com.eldercare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final NotificationService notificationService;

    @Transactional
    public AppointmentResponse bookAppointment(User user, AppointmentBookingRequest request) {
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found."));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + request.getDoctorId()));

        if (!doctor.isApproved()) {
            throw new RuntimeException("Doctor is not currently approved for appointments.");
        }

        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found with id: " + request.getHospitalId()));

        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot book an appointment for a past date.");
        }

        // Check for existing overlapping active appointment
        boolean isConflict = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                doctor.getId(), request.getAppointmentDate(), request.getAppointmentTime(), AppointmentStatus.CANCELLED
        );

        if (isConflict) {
            throw new RuntimeException("Selected time slot is already booked. Please select another slot.");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .hospital(hospital)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.PENDING)
                .consultationFee(doctor.getConsultationFee())
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        // Send notifications
        if (doctor.getUser() != null) {
            notificationService.createNotification(
                    doctor.getUser(),
                    "New appointment request from " + patient.getFullName() + " for " + saved.getAppointmentDate() + " at " + saved.getAppointmentTime()
            );
        }
        notificationService.createNotification(
                user,
                "Your appointment with Dr. " + doctor.getFullName() + " on " + saved.getAppointmentDate() + " at " + saved.getAppointmentTime() + " has been requested."
        );

        return mapToAppointmentResponse(saved);
    }

    public List<AppointmentResponse> getAppointmentsForPatient(User user) {
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found."));
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patient.getId()).stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAppointmentsForDoctor(User user, LocalDate date, AppointmentStatus status) {
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Doctor profile not found."));

        List<Appointment> list;
        if (date != null) {
            list = appointmentRepository.findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(doctor.getId(), date);
        } else {
            list = appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctor.getId());
        }

        if (status != null) {
            list = list.stream().filter(a -> a.getStatus() == status).collect(Collectors.toList());
        }

        return list.stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        return mapToAppointmentResponse(appointment);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long appointmentId, User user, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        boolean isPatient = appointment.getPatient().getUser() != null && appointment.getPatient().getUser().getId().equals(user.getId());
        boolean isDoctor = appointment.getDoctor().getUser() != null && appointment.getDoctor().getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isPatient && !isDoctor && !isAdmin) {
            throw new RuntimeException("Unauthorized to update this appointment.");
        }

        if (isPatient && newStatus != AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Patients can only cancel their appointments.");
        }

        appointment.setStatus(newStatus);
        Appointment updated = appointmentRepository.save(appointment);

        // Notify patient if updated by doctor/admin
        if ((isDoctor || isAdmin) && appointment.getPatient().getUser() != null) {
            notificationService.createNotification(
                    appointment.getPatient().getUser(),
                    "Your appointment with Dr. " + appointment.getDoctor().getFullName() + " on " + appointment.getAppointmentDate() + " has been " + newStatus.name()
            );
        }

        // Notify doctor if cancelled by patient
        if (isPatient && appointment.getDoctor().getUser() != null) {
            notificationService.createNotification(
                    appointment.getDoctor().getUser(),
                    "Appointment with " + appointment.getPatient().getFullName() + " on " + appointment.getAppointmentDate() + " was cancelled by the patient."
            );
        }

        return mapToAppointmentResponse(updated);
    }

    public AppointmentResponse mapToAppointmentResponse(Appointment appointment) {
        boolean hasRecord = medicalRecordRepository.existsByAppointmentId(appointment.getId());

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getFullName())
                .patientMobile(appointment.getPatient().getMobileNumber())
                .patientEmergencyContact(appointment.getPatient().getEmergencyContactNumber())
                .patientEmergencyName(appointment.getPatient().getEmergencyContactName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getFullName())
                .doctorSpecialization(appointment.getDoctor().getSpecialization() != null ? appointment.getDoctor().getSpecialization().getName() : "")
                .doctorQualification(appointment.getDoctor().getQualification())
                .hospitalId(appointment.getHospital().getId())
                .hospitalName(appointment.getHospital().getName())
                .hospitalAddress(appointment.getHospital().getAddress())
                .hospitalCity(appointment.getHospital().getCity())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .consultationFee(appointment.getConsultationFee())
                .hasMedicalRecord(hasRecord)
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
