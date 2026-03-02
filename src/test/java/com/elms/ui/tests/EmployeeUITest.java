package com.elms.ui.tests;

import com.elms.ui.pages.LoginPage;
import com.elms.ui.pages.EmployeeDashboardPage;
import io.github.boridshova.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * UI Tests for Employee Dashboard Functionality
 * Tests leave application and balance viewing
 */
public class EmployeeUITest {

    private WebDriver driver;
    private LoginPage loginPage;
    private EmployeeDasNaoardPage employeeDashboard;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
        employeeDashboard = new EmployeeDasNaoardPage(driver);

        // Login as employee before each test
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.login("testemployee", "test123");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1, description = "Test employee dashboard loads")
    public void testDashboardLoads() {
        Assert.assertTrue(employeeDashboard.isDashboardLoaded(), "Dashboard should load");
        Assert.assertTrue(driver.getCurrentUrl().contains("employee-dashboard"), "Should be on employee dashboard");
    }

    @Test(priority = 2, description = "Test leave balances are displayed")
    public void testLeaveBalancesDisplayed() {
        String casualBalance = employeeDashboard.getCasualLeaveBalance();
        String sickBalance = employeeDashboard.getSickLeaveBalance();
        Assert.assertNotNull(casualBalance, "Casual leave balance should be displayed");
        Assert.assertNotNull(sickBalance, "Sick leave balance should be displayed");
    }

    @Test(priority = 3, description = "Test applying for casual leave")
    public void testApplyCasualLeave() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate dayAfter = tomorrow.plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        employeeDashboard.applyForLeave(
            "CASUAL",
            tomorrow.format(formatter),
            dayAfter.format(formatter),
            "Personal work"
        );

        Assert.assertTrue(employeeDashboard.isLeaveApplicationSuccessful(), "Leave application should be successful");
    }

    @Test(priority = 4, description = "Test applying for sick leave")
    public void testApplySickLeave() {
        LocalDate tomorrow = LocalDate.now().plusDays(3);
        LocalDate dayAfter = tomorrow.plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        employeeDashboard.applyForLeave(
            "SICK",
            tomorrow.format(formatter),
            dayAfter.format(formatter),
            "Medical emergency"
        );

        Assert.assertTrue(employeeDashboard.isLeaveApplicationSuccessful(), "Leave application should be successful");
    }

    @Test(priority = 5, description = "Test viewing leave requests")
    public void testViewLeaveRequests() {
        int count = employeeDashboard.getLeaveRequestsCount();
        Assert.assertTrue(count >= 0, "Leave requests table should be visible");
    }

    @Test(priority = 6, description = "Test user name display")
    public void testUserNameDisplay() {
        String userName = employeeDashboard.getUserName();
        Assert.assertNotNull(userName, "User name should be displayed");
        Assert.assertFalse(userName.isEmpty(), "User name should not be empty");
    }

    @Test(priority = 7, description = "Test logout functionality")
    public void testLogout() {
        employeeDashboard.clickLogout();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(driver.getCurrentUrl().contains("index.html"), "Should be redirected to login page after logout");
    }
}