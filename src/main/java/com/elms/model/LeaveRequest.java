package com.elms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "leave_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"employee"})
@ToString(exclude = {"employee"})
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnore
    private User employee;

    @Transient
    private Long employeeId;

    @Transient
    private String employeeName;

    @Transient
    private String employeeEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "reason", length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(name = "comments", length = 500)
    private String comments;

    @Column(name = "applied_date")
    private LocalDate appliedDate = LocalDate.now();

    @Column(name = "processed_date")
    private LocalDate processedDate;

    @PrePersist
    public void calculateDuration() {
        if (startDate != null && endDate != null) {
            this.duration = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
    }

    public enum LeaveType {
        CASUAL, SICK
    }

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED
    }

    @PostLoad
    public void populateEmployeeDetails() {
        if (employee != null) {
            this.employeeId = employee.getId();
            this.employeeName = employee.getFullName();
            this.employeeEmail = employee.getEmail();
        }
    }
}