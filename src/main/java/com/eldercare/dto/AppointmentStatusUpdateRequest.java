package com.eldercare.dto;

import com.eldercare.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private AppointmentStatus status;
}
