package com.eldercare.repository;

import com.eldercare.entity.Appointment;
import com.eldercare.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);
    List<Appointment> findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(Long doctorId);
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
    List<Appointment> findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(Long doctorId, LocalDate date);
    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDate date, AppointmentStatus status);
    
    // Check for existing overlapping appointments
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
        Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status
    );

    long countByStatus(AppointmentStatus status);
}
