package com.elms.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Employee Leave Request Endpoints
 * Tests leave application, quota enforcement, and retrieval
 */
public class EmployeeAPITest {

    private static final String BASE_URI = "http://localhost";
    private static final int PORT = 8080;
    private String employeeToken;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;

        // Register and login employee to get token
        String registerPayload = "{\n" +
                "\"username\": \"employeetest\",\n" +
                "\"password\": \"test123\",\n" +
                "\"fullName\": \"Employee Test User\",\n" +
                "\"email\": \"employeetest@test.com\",\n" +
                "\"role\": \"EMPLOYEE\"\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
            .when()
                .post("/api/auth/register")
            .then()
                .extract().response();

        if (response.getStatusCode() == 200) {
            employeeToken = response.jsonPath().getString("token");
        } else {
            // User already exists, login
            String loginPayload = "{\n" +
                    "\"username\": \"employeetest\",\n" +
                    "\"password\": \"test123\"\n" +
                    "}";

            employeeToken = given()
                    .contentType(ContentType.JSON)
                    .body(loginPayload)
                .when()
                    .post("/api/auth/login")
                .then()
                    .extract().response()
                    .jsonPath().getString("token");
        }
    }

    @Test(priority = 1, description = "Test applying for casual leave")
    public void testApplyCasualLeave() {
        String leavePayload = "{\n" +
                "\"leaveType\": \"CASUAL\",\n" +
                "\"startDate\": \"2024-04-01\",\n" +
                "\"endDate\": \"2024-04-02\",\n" +
                "\"reason\": \"Personal work\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + employeeToken)
                .body(leavePayload)
            .when()
                .post("/api/employee/leave/apply")
            .then()
                .statusCode(200)
                .body("leaveType", equalTo("CASUAL"))
                .body("status", equalTo("PENDING"))
                .body("duration", equalTo(2));
    }

    @Test(priority = 2, description = "Test applying for sick leave")
    public void testApplySickLeave() {
        String leavePayload = "{\n" +
                "\"leaveType\": \"SICK\",\n" +
                "\"startDate\": \"2024-04-05\",\n" +
                "\"endDate\": \"2024-04-06\",\n" +
                "\"reason\": \"Medical emergency\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + employeeToken)
                .body(leavePayload)
            .when()
                .post("/api/employee/leave/apply")
            .then()
                .statusCode(200)
                .body("leaveType", equalTo("SICK"))
                .body("status", equalTo("PENDING"))
                .body("duration", equalTo(2));
    }

    @Test(priority = 3, description = "Test getting my leave requests")
    public void testGetMyLeaveRequests() {
        given()
                .header("Authorization", "Bearer " + employeeToken)
            .when()
                .get("/api/employee/leave/my-requests")
            .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanEqualTo(0)));
    }

    @Test(priority = 4, description = "Test applying leave without authentication")
    public void testApplyLeaveWithoutAuth() {
        String leavePayload = "{\n" +
                "\"leaveType\": \"CASUAL\",\n" +
                "\"startDate\": \"2024-04-10\",\n" +
                "\"endDate\": \"2024-04-11\",\n" +
                "\"reason\": \"Testing auth\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(leavePayload)
            .when()
                .post("/api/employee/leave/apply")
            .then()
                .statusCode(403);
    }

    @Test(priority = 5, description = "Test applying leave with invalid date range")
    public void testApplyLeaveInvalidDates() {
        String leavePayload = "{\n" +
                "\"leaveType\": \"CASUAL\",\n" +
                "\"startDate\": \"2024-04-15\",\n" +
                "\"endDate\": \"2024-04-10\",\n" +
                "\"reason\": \"Invalid date range\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + employeeToken)
                .body(leavePayload)
            .when()
                .post("/api/employee/leave/apply")
            .then()
                .statusCode(400);
    }

    @Test(priority = 6, description = "Test exceeding leave quota")
    public void testExceedLeaveQuota() {
        String leavePayload = "{\n" +
                "\"leaveType\": \"CASUAL\",\n" +
                "\"startDate\": \"2024-05-01\",\n" +
                "\"endDate\": \"2024-05-07\",\n" +
                "\"reason\": \"Exceeding quota\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + employeeToken)
                .body(leavePayload)
            .when()
                .post("/api/employee/leave/apply")
            .then()
                .statusCode(400);
    }
}