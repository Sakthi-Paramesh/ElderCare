package com.eldercare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotResponse {
    private Long doctorId;
    private String doctorName;
    private LocalDate date;
    private List<LocalTime> availableSlots;
}
