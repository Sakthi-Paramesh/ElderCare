package com.eldercare.service;

import com.eldercare.dto.*;
import com.eldercare.entity.Appointment;
import com.eldercare.entity.AppointmentStatus;
import com.eldercare.entity.Doctor;
import com.eldercare.entity.DoctorAvailability;
import com.eldercare.repository.AppointmentRepository;
import com.eldercare.repository.DoctorAvailabilityRepository;
import com.eldercare.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;

    public List<DoctorResponse> getAllApprovedDoctors() {
        return doctorRepository.findByIsApprovedTrue().stream()
                .map(this::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorResponse> searchDoctors(Long specializationId, Long hospitalId, String city, String name) {
        List<Doctor> doctors;
        if (specializationId != null) {
            doctors = doctorRepository.findBySpecializationIdAndIsApprovedTrue(specializationId);
        } else if (hospitalId != null) {
            doctors = doctorRepository.findByHospitalIdAndIsApprovedTrue(hospitalId);
        } else if (city != null && !city.trim().isEmpty()) {
            doctors = doctorRepository.findByHospitalCityContainingIgnoreCaseAndIsApprovedTrue(city.trim());
        } else if (name != null && !name.trim().isEmpty()) {
            doctors = doctorRepository.findByFullNameContainingIgnoreCaseAndIsApprovedTrue(name.trim());
        } else {
            doctors = doctorRepository.findByIsApprovedTrue();
        }

        return doctors.stream()
                .map(this::mapToDoctorResponse)
                .collect(Collectors.toList());
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }

    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for user id: " + userId));
    }

    public DoctorResponse getDoctorResponseById(Long id) {
        return mapToDoctorResponse(getDoctorById(id));
    }

    @Transactional
    public DoctorResponse updateProfile(Long doctorId, DoctorProfileUpdateRequest request) {
        Doctor doctor = getDoctorById(doctorId);
        if (request.getFullName() != null) doctor.setFullName(request.getFullName());
        if (request.getQualification() != null) doctor.setQualification(request.getQualification());
        if (request.getExperienceYears() != null) doctor.setExperienceYears(request.getExperienceYears());
        if (request.getAbout() != null) doctor.setAbout(request.getAbout());
        if (request.getConsultationFee() != null) doctor.setConsultationFee(request.getConsultationFee());
        if (request.getProfilePhotoUrl() != null) doctor.setProfilePhotoUrl(request.getProfilePhotoUrl());
        return mapToDoctorResponse(doctorRepository.save(doctor));
    }

    @Transactional
    public List<DoctorAvailability> setAvailability(Long doctorId, List<DoctorAvailabilityRequest> requestList) {
        Doctor doctor = getDoctorById(doctorId);
        
        // Remove existing availability for the specified days or refresh all
        List<DoctorAvailability> existing = availabilityRepository.findByDoctorId(doctorId);
        availabilityRepository.deleteAll(existing);

        List<DoctorAvailability> newAvailabilities = new ArrayList<>();
        for (DoctorAvailabilityRequest req : requestList) {
            DoctorAvailability da = DoctorAvailability.builder()
                    .doctor(doctor)
                    .dayOfWeek(req.getDayOfWeek())
                    .startTime(req.getStartTime())
                    .endTime(req.getEndTime())
                    .slotDurationMinutes(req.getSlotDurationMinutes() != null ? req.getSlotDurationMinutes() : 30)
                    .build();
            newAvailabilities.add(da);
        }

        return availabilityRepository.saveAll(newAvailabilities);
    }

    public List<DoctorAvailability> getAvailability(Long doctorId) {
        return availabilityRepository.findByDoctorId(doctorId);
    }

    public AvailableSlotResponse getAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = getDoctorById(doctorId);
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        List<DoctorAvailability> availabilities = availabilityRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);
        if (availabilities.isEmpty()) {
            return AvailableSlotResponse.builder()
                    .doctorId(doctorId)
                    .doctorName(doctor.getFullName())
                    .date(date)
                    .availableSlots(Collections.emptyList())
                    .build();
        }

        // Get booked appointments for this date (excluding CANCELLED and REJECTED)
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
        Set<LocalTime> bookedTimes = bookedAppointments.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED && a.getStatus() != AppointmentStatus.REJECTED)
                .map(Appointment::getAppointmentTime)
                .collect(Collectors.toSet());

        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime now = LocalTime.now();
        boolean isToday = date.isEqual(LocalDate.now());

        for (DoctorAvailability da : availabilities) {
            LocalTime current = da.getStartTime();
            int duration = (da.getSlotDurationMinutes() != null && da.getSlotDurationMinutes() > 0) ? da.getSlotDurationMinutes() : 30;

            while (current.plusMinutes(duration).isBefore(da.getEndTime()) || current.plusMinutes(duration).equals(da.getEndTime())) {
                // If it's today, don't show past times
                if (!isToday || current.isAfter(now)) {
                    if (!bookedTimes.contains(current)) {
                        availableSlots.add(current);
                    }
                }
                current = current.plusMinutes(duration);
            }
        }

        Collections.sort(availableSlots);

        return AvailableSlotResponse.builder()
                .doctorId(doctorId)
                .doctorName(doctor.getFullName())
                .date(date)
                .availableSlots(availableSlots)
                .build();
    }

    public DoctorResponse mapToDoctorResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .userId(doctor.getUser() != null ? doctor.getUser().getId() : null)
                .fullName(doctor.getFullName())
                .email(doctor.getUser() != null ? doctor.getUser().getEmail() : "")
                .departmentId(doctor.getSpecialization() != null ? doctor.getSpecialization().getId() : null)
                .departmentName(doctor.getSpecialization() != null ? doctor.getSpecialization().getName() : "")
                .qualification(doctor.getQualification())
                .experienceYears(doctor.getExperienceYears())
                .about(doctor.getAbout())
                .profilePhotoUrl(doctor.getProfilePhotoUrl())
                .hospitalId(doctor.getHospital() != null ? doctor.getHospital().getId() : null)
                .hospitalName(doctor.getHospital() != null ? doctor.getHospital().getName() : "")
                .hospitalCity(doctor.getHospital() != null ? doctor.getHospital().getCity() : "")
                .consultationFee(doctor.getConsultationFee())
                .isApproved(doctor.isApproved())
                .build();
    }
}
