package com.eldercare.dto;

import com.eldercare.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private String patientMobile;
    private String patientEmergencyContact;
    private String patientEmergencyName;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private String doctorQualification;
    private Long hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalCity;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private BigDecimal consultationFee;
    private boolean hasMedicalRecord;
    private LocalDateTime createdAt;
}
