package com.elms.repository;

import com.elms.model.LeaveRequest;
import com.elms.model.LeaveRequest.LeaveStatus;
import com.elms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(User employee);
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    List<LeaveRequest> findByStatus(LeaveStatus status);
    List<LeaveRequest> findByEmployeeAndStatus(User employee, LeaveStatus status);
    List<LeaveRequest> findAllByOrderByAppliedDateDesc();
}