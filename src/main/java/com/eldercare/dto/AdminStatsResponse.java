package com.eldercare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalPatients;
    private long totalDoctors;
    private long pendingDoctors;
    private long totalAppointments;
    private long completedAppointments;
    private long totalHospitals;
    private long totalDepartments;
}
