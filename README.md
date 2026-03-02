# Employee Leave Management System

A comprehensive full-stack web application for managing employee leave requests.

## Features
- User Authentication: Secure login/registration with JWT
- Role-Based Access: Separate dashboards for Employees and Managers
- Leave Management: Apply, approve, and reject leave requests
- Leave Quota Enforcement: Casual (5 days) and Sick leave (5 days)
- Real-time Tracking: Live status updates

## Technology Stack
- Backend: Spring Boot 3.1.5, Java 17, H2 Database
- Security: Spring Security with JWT
- Frontend: Vanilla JS, Bootstrap 5.3

## How to Run
1. Clone: git clone https://github.com/rishithatcs/workflow_elms.git
2. Build: mvn clean install
3. Run: mvn spring-boot:run
4. Access: http://localhost:8080

## API Endpoints
- POST /api/auth/login - Login
- POST /api/auth/register - Register
- POST /api/employee/leave/apply - Apply leave
- GET /api/employee/leave/my-requests - View my requests
- GET /api/manager/leave/pending - View pending requests
- PUT /api/manager/leave/{id}/approve - Approve leave
- PUT /api/manager/leave/{id}/reject - Reject leave