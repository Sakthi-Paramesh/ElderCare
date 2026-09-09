package com.eldercare.service;

import com.eldercare.dto.*;
import com.eldercare.entity.*;
import com.eldercare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final NotificationService notificationService;

    @Transactional
    public MedicalRecordResponse createMedicalRecord(User doctorUser, MedicalRecordRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + request.getAppointmentId()));

        Doctor doctor = doctorRepository.findByUserId(doctorUser.getId())
                .orElseThrow(() -> new RuntimeException("Doctor profile not found."));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Unauthorized: Only the assigned doctor can add medical records for this appointment.");
        }

        if (medicalRecordRepository.existsByAppointmentId(appointment.getId())) {
            throw new RuntimeException("A medical record already exists for this appointment.");
        }

        MedicalRecord record = MedicalRecord.builder()
                .appointment(appointment)
                .diagnosis(request.getDiagnosis())
                .consultationNotes(request.getConsultationNotes())
                .build();

        MedicalRecord savedRecord = medicalRecordRepository.save(record);

        // Mark appointment as COMPLETED
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        List<Prescription> savedPrescriptions = new ArrayList<>();
        if (request.getPrescriptions() != null && !request.getPrescriptions().isEmpty()) {
            for (PrescriptionItemRequest item : request.getPrescriptions()) {
                Prescription p = Prescription.builder()
                        .medicalRecord(savedRecord)
                        .medicineName(item.getMedicineName())
                        .dosage(item.getDosage())
                        .duration(item.getDuration())
                        .instructions(item.getInstructions())
                        .build();
                savedPrescriptions.add(p);
            }
            savedPrescriptions = prescriptionRepository.saveAll(savedPrescriptions);
        }

        // Notify patient
        if (appointment.getPatient().getUser() != null) {
            notificationService.createNotification(
                    appointment.getPatient().getUser(),
                    "Dr. " + doctor.getFullName() + " has added consultation notes & prescription for your appointment on " + appointment.getAppointmentDate()
            );
        }

        return mapToResponse(savedRecord, savedPrescriptions);
    }

    public MedicalRecordResponse getByAppointmentId(Long appointmentId) {
        MedicalRecord record = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Medical record not found for appointment: " + appointmentId));
        List<Prescription> prescriptions = prescriptionRepository.findByMedicalRecordId(record.getId());
        return mapToResponse(record, prescriptions);
    }

    public List<MedicalRecordResponse> getRecordsForPatient(Long patientId) {
        List<MedicalRecord> records = medicalRecordRepository.findByAppointmentPatientIdOrderByCreatedAtDesc(patientId);
        return records.stream().map(r -> {
            List<Prescription> p = prescriptionRepository.findByMedicalRecordId(r.getId());
            return mapToResponse(r, p);
        }).collect(Collectors.toList());
    }

    public List<MedicalRecordResponse> getRecordsForCurrentPatient(User user) {
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found."));
        return getRecordsForPatient(patient.getId());
    }

    private MedicalRecordResponse mapToResponse(MedicalRecord record, List<Prescription> prescriptions) {
        Appointment a = record.getAppointment();

        List<PrescriptionResponse> pResponses = prescriptions.stream().map(p -> PrescriptionResponse.builder()
                .id(p.getId())
                .medicineName(p.getMedicineName())
                .dosage(p.getDosage())
                .duration(p.getDuration())
                .instructions(p.getInstructions())
                .createdAt(p.getCreatedAt())
                .build()).collect(Collectors.toList());

        return MedicalRecordResponse.builder()
                .id(record.getId())
                .appointmentId(a.getId())
                .appointmentDate(a.getAppointmentDate())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getFullName())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getFullName())
                .doctorSpecialization(a.getDoctor().getSpecialization() != null ? a.getDoctor().getSpecialization().getName() : "")
                .hospitalName(a.getHospital().getName())
                .diagnosis(record.getDiagnosis())
                .consultationNotes(record.getConsultationNotes())
                .createdAt(record.getCreatedAt())
                .prescriptions(pResponses)
                .build();
    }
}
