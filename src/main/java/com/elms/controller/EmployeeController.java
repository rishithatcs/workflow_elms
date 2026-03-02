package com.elms.controller;

import com.elms.dto.LeaveRequestDTO;
import com.elms.model.LeaveRequest;
import com.elms.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(maxAge = 3600)
@RequiredArgsConstructor
public class EmployeeController {
    private final LeaveRequestService leaveRequestService;

    @PostMapping("/leave/apply")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<LeaveRequest> applyLeave(@Valid @RequestBody LeaveRequestDTO dto) {
        return ResponseEntity.ok(leaveRequestService.applyLeave(dto));
    }

    @GetMapping("/leave/my-requests")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<LeaveRequest>> getMyLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getMyLeaveRequests());
    }

    @GetMapping("/leave/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<LeaveRequest> getLeaveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.getLeaveRequestById(id));
    }
}