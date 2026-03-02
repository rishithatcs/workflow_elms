package com.elms.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Authentication Endpoints
 * Tests all login and registration APIs
 */
public class AuthAPITest {

    private static final String BASE_URI = "http://localhost";
    private static final int PORT = 8080;
    private static final String BASE_PATH = "/api/auth";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;
        RestAssured.basePath = BASE_PATH;
    }

    @Test(priority = 1, description = "Test user registration with valid data")
    public void testValidRegistration() {
        String registerPayload = "{\n" +
                "\"username\": \"testemployee\",\n" +
                "\"password\": \"test123\",\n" +
                "\"fullName\": \"Test Employee\",\n" +
                "\"email\": \"testemployee@test.com\",\n" +
                "\"role\": \"EMPLOYEE\"\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
            .when()
                .post("/register")
            .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.getBody().asString().contains("token"), "Response should contain token");
        Assert.assertTrue(response.getBody().asString().contains("testemployee"), "Response should contain username");
    }

    @Test(priority = 2, description = "Test user login with valid credentials")
    public void testValidLogin() {
        String loginPayload = "{\n" +
                "\"username\": \"testemployee\",\n" +
                "\"password\": \"test123\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
            .when()
                .post("/login")
            .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("username", equalTo("testemployee"))
                .body("role", equalTo("EMPLOYEE"))
                .body("casualLeaveBalance", equalTo(5))
                .body("sickLeaveBalance", equalTo(5));
    }

    @Test(priority = 3, description = "Test login with invalid credentials")
    public void testInvalidLogin() {
        String loginPayload = "{\n" +
                "\"username\": \"testemployee\",\n" +
                "\"password\": \"wrongpassword\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
            .when()
                .post("/login")
            .then()
                .statusCode(401);
    }

    @Test(priority = 4, description = "Test registration with duplicate username")
    public void testDuplicateUsername() {
        String registerPayload = "{\n" +
                "\"username\": \"testemployee\",\n" +
                "\"password\": \"test123\",\n" +
                "\"fullName\": \"Duplicate User\",\n" +
                "\"email\": \"duplicate@test.com\",\n" +
                "\"role\": \"EMPLOYEE\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
            .when()
                .post("/register")
            .then()
                .statusCode(400);
    }

    @Test(priority = 5, description = "Test registration with invalid email")
    public void testInvalidEmailRegistration() {
        String registerPayload = "{\n" +
                "\"username\": \"testuser2\",\n" +
                "\"password\": \"test123\",\n" +
                "\"fullName\": \"Test User Two\",\n" +
                "\"email\": \"invalidemail\",\n" +
                "\"role\": \"EMPLOYEE\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
            .when()
                .post("/register")
            .then()
                .statusCode(400);
    }

    @Test(priority = 6, description = "Test registration of a manager")
    public void testManagerRegistration() {
        String registerPayload = "{\n" +
                "\"username\": \"testmanager\",\n" +
                "\"password\": \"manager123\",\n" +
                "\"fullName\": \"Test Manager\",\n" +
                "\"email\": \"testmanager@test.com\",\n" +
                "\"role\": \"MANAGER\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
            .when()
                .post("/register")
            .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("username", equalTo("testmanager"))
                .body("role", equalTo("MANAGER"));
    }

    @Test(priority = 7, description = "Test login with missing fields")
    public void testLoginMissingFields() {
        String loginPayload = "{\n" +
                "\"username\": \"testemployee\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
            .when()
                .post("/login")
            .then()
                .statusCode(400);
    }
}