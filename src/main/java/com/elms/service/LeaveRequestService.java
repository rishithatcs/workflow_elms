package com.elms.service;

import com.elms.dto.LeaveRequestDTO;
import com.elms.model.LeaveRequest;
import com.elms.model.LeaveRequest.LeaveStatus;
import com.elms.model.LeaveRequest.LeaveType;
import com.elms.model.User;
import com.elms.repository.LeaveRequestRepository;
import com.elms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public LeaveRequest applyLeave(LeaveRequestDTO dto) {
        User employee = getCurrentUser();

        // Validate dates
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the past");
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        // Calculate duration
        int duration = (int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;

        // Validate leave balance
        if (dto.getLeaveType() == LeaveType.CASUAL) {
            if (employee.getCasualLeaveBalance() < duration) {
                throw new RuntimeException("Insufficient casual leave balance. Available: " + 
                        employee.getCasualLeaveBalance() + " days");
            }
        } else if (dto.getLeaveType() == LeaveType.SICK) {
            if (employee.getSickLeaveBalance() < duration) {
                throw new RuntimeException("Insufficient sick leave balance. Available: " + 
                        employee.getSickLeaveBalance() + " days");
            }
        }

        // Create leave request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(dto.getLeaveType());
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setDuration(duration);
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setAppliedDate(LocalDate.now());

        return leaveRequestRepository.save(leaveRequest);
    }

    public List<LeaveRequest> getMyLeaveRequests() {
        User employee = getCurrentUser();
        return leaveRequestRepository.findByEmployeeId(employee.getId());
    }

    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
    }

    public List<LeaveRequest> getAllPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAllByOrderByAppliedDateDesc();
    }

    @Transactional
    public LeaveRequest approveLeave(Long id, String comments) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leave requests can be approved");
        }

        User employee = leaveRequest.getEmployee();

        // Deduct leave balance
        if (leaveRequest.getLeaveType() == LeaveType.CASUAL) {
            employee.setCasualLeaveBalance(employee.getCasualLeaveBalance() - leaveRequest.getDuration());
        } else if (leaveRequest.getLeaveType() == LeaveType.SICK) {
            employee.setSickLeaveBalance(employee.getSickLeaveBalance() - leaveRequest.getDuration());
        }

        userRepository.save(employee);

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setComments(comments);
        leaveRequest.setProcessedDate(LocalDate.now());

        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public LeaveRequest rejectLeave(Long id, String comments) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leave requests can be rejected");
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setComments(comments);
        leaveRequest.setProcessedDate(LocalDate.now());

        return leaveRequestRepository.save(leaveRequest);
    }
}