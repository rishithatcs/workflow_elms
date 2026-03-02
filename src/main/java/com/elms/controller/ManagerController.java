package com.elms.controller;

import com.elms.dto.LeaveActionRequest;
import com.elms.model.LeaveRequest;
import com.elms.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin(maxAge = 3600)
@RequiredArgsConstructor
public class ManagerController {
    private final LeaveRequestService leaveRequestService;

    @GetMapping("/leave/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveRequest>> getPendingLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllPendingLeaveRequests());
    }

    @GetMapping("/leave/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    @GetMapping("/leave/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<LeaveRequest> getLeaveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.getLeaveRequestById(id));
    }

    @PutMapping("/leave/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<LeaveRequest> approveLeave(@PathVariable Long id, 
                                                                  @RequestBody LeaveActionRequest request) {
        return ResponseEntity.ok(leaveRequestService.approveLeave(id, request.getComments()));
    }

    @PutMapping("/leave/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<LeaveRequest> rejectLeave(@PathVariable Long id,
                                                                  @RequestBody LeaveActionRequest request) {
        return ResponseEntity.ok(leaveRequestService.rejectLeave(id, request.getComments()));
    }
}