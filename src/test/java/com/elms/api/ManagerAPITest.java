package com.elms.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Manager Leave Management Endpoints
 * Tests viewing, approving, and rejecting leave requests
 */
public class ManagerAPITest {

    private static final String BASE_URI = "http://localhost";
    private static final int PORT = 8080;
    private String managerToken;
    private String employeeToken;
    private Integer testLeaveId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;

        // Register and login manager
        String managerRegisterPatyload = "{\n" +
                "\"username\": \"managertest\",\n" +
                "\"password\": \"test123\",\n" +
                "\"fullName\": \"Manager Test User\",\n" +
                "\"email\": \"managertest@test.com\",\n" +
                "\"role\": \"MANAGER\"\n" +
                "}";

        Response managerResponse = given()
                .contentType(ContentType.JSON)
                .body(managerRegisterPatyload)
            .when()
                .post("/api/auth/register")
            .then()
                .extract().response();

        if (managerResponse.getStatusCode() == 200) {
            managerToken = managerResponse.jsonPath().getString("token");
        } else {
            // Manager already exists, login
            String managerLoginPayload = "{\n" +
                    "\"username\": \"managertest\",\n" +
                    "\"password\": \"test123\"\n" +
                    "}";

            managerToken = given()
                    .contentType(ContentType.JSON)
                    .body(managerLoginPayload)
                .when()
                    .post("/api/auth/login")
                .then()
                    .extract().response()
                    .jsonPath().getString("token");
        }

        // Create an employee and apply for leave
        String employeeRegisterPayload = "{\n" +
                "\"username\": \"employeeformanager\",\n" +
                "\"password\": \"test123\",\n" +
                "\"fullName\": \"Employee For Manager\",\n" +
                "\"email\": \"employeeformanager@test.com\",\n" +
                "\"role\": \"EMPLOYEE\"\n" +
                "}";

        Response employeeResponse = given()
                .contentType(ContentType.JSON)
                .body(employeeRegisterPayload)
            .when()
                .post("/api/auth/register")
            .then()
                .extract().response();

        if (employeeResponse.getStatusCode() == 200) {
            employeeToken = employeeResponse.jsonPath().getString("token");
        } else {
            String employeeLoginPayload = "{\n" +
                    "\"username\": \"employeeformanager\",\n" +
                    "\"password\": \"test123\"\n" +
                    "}";

            employeeToken = given()
                    .contentType(ContentType.JSON)
                    .body(employeeLoginPayload)
                .when()
                    .post("/api/auth/login")
                .then()
                    .extract().response()
                    .jsonPath().getString("token");
        }

        // Apply for leave as employee
        String leavePayload = "{\n" +
                "\"leaveType\": \"CASUAL\",\n" +
                "\"startDate\": \"2024-06-01\",\n" +
                "\"endDate\": \"2024-06-02\",\n" +
                "\"reason\": \"For manager testing\"\n" +
                "}";

        Response leaveResponse = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + employeeToken)
                .body(leavePayload)
            .when()
                .post("/api/employee/leave/apply")
            .then()
                .extract().response();

        testLeaveId = leaveResponse.jsonPath().getInt("id");
    }

    @Test(priority = 1, description = "Test getting all pending leave requests")
    public void testGetPendingLeaveRequests() {
        given()
                .header("Authorization", "Bearer " + managerToken)
            .when()
                .get("/api/manager/leave/pending")
            .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanEqualTo(0)));
    }

    @Test(priority = 2, description = "Test getting all leave requests")
    public void testGetAllLeaveRequests() {
        given()
                .header("Authorization", "Bearer " + managerToken)
            .when()
                .get("/api/manager/leave/all")
            .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanEqualTo(0)));
    }

    @Test(priority = 3, description = "Test approving a leave request")
    public void testApproveLeaveRequest() {
        String actionPayload = "{\n" +
                "\"comments\": \"Approved by manager\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .body(actionPayload)
            .when()
                .put("/api/manager/leave/" + testLeaveId + "/approve")
            .then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"))
                .body("comments", equalTo("Approved by manager"));
    }

    @Test(priority = 4, description = "Test rejecting a leave request")
    public void testRejectLeaveRequest() {
        // Create another leave request to reject
        String leavePayload = "{\n" +
                "\"leaveType\": \"SICK\",\n" +
                "\"startDate\": \"2024-06-10\",\n" +
                "\"endDate\": \"2024-06-11\",\n" +
                "\"reason\": \"For rejection test\"\n" +
                "}";

        Response leaveResponse = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + employeeToken)
                .body(leavePayload)
            .when()
                .post("/api/employee/leave/apply")
            .then()
                .extract().response();

        Integer rejectLeaveId = leaveResponse.jsonPath().getInt("id");

        String actionPayload = "{\n" +
                "\"comments\": \"Rejected by manager\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .body(actionPayload)
            .when()
                .put("/api/manager/leave/" + rejectLeaveId + "/reject")
            .then()
                .statusCode(200)
                .body("status", equalTo("REJECTED"))
                .body("comments", equalTo("Rejected by manager"));
    }

    @Test(priority = 5, description = "Test getting specific leave request")
    public void testGetSpecificLeaveRequest() {
        given()
                .header("Authorization", "Bearer " + managerToken)
            .when()
                .get("/api/manager/leave/" + testLeaveId)
            .then()
                .statusCode(200)
                .body("id", equalTo(testLeaveId))
                .body("leaveType", equalTo("CASUAL"));
    }

    @Test(priority = 6, description = "Test accessing manager endpoint without auth")
    public void testManagerEndpointWithoutAuth() {
        given()
            .when()
                .get("/api/manager/leave/pending")
            .then()
                .statusCode(403);
    }

    @Test(priority = 7, description = "Test employee accessing manager endpoint")
    public void testEmployeeAccessManagerEndpoint() {
        given()
                .header("Authorization", "Bearer " + employeeToken)
            .when()
                .get("/api/manager/leave/pending")
            .then()
                .statusCode(403);
    }
}