package com.elms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.elms.model.LeaveRequest.LeaveType;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO {
    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String reason;
}