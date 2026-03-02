package com.elms.ui.tests;

import com.elms.ui.pages.LoginPage;
import com.elms.ui.pages.EmployeeDashboardPage;
import com.elms.ui.pages.ManagerDashboardPage;
import io.github.boridshova.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

/**
 * UI Tests for Login Functionality
 * Tests user authentication and navigation
 */
public class LoginUITest {

    private WebDriver driver;
    private LoginPage loginPage;
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
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1, description = "Test login page loads successfully")
    public void testLoginPageLoads() {
        loginPage.navigateToLoginPage(BASE_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("index.html"), "Login page should load");
        Assert.assertTrue(driver.getTitle().contains("Employee Leave"), "Page title should be correct");
    }

    @Test(priority = 2, description = "Test employee login with valid credentials")
    public void testValidEmployeeLogin() {
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.login("testemployee", "test123");
        Assert.assertTrue(loginPage.isUserRedirected(), "User should be redirected after login");
        Assert.assertTrue(driver.getCurrentUrl().contains("employee-dashboard"), "Employee should be redirected to employee dashboard");
    }

    @Test(priority = 3, description = "Test manager login with valid credentials")
    public void testValidManagerLogin() {
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.login("testmanager", "manager123");
        Assert.assertTrue(loginPage.isUserRedirected(), "User should be redirected after login");
        Assert.assertTrue(driver.getCurrentUrl().contains("manager-dashboard"), "Manager should be redirected to manager dashboard");
    }

    @Test(priority = 4, description = "Test login with invalid credentials")
    public void testInvalidLogin() {
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.login("invaliduser", "wrongpassword");
        Assert.assertTrue(loginPage.isAlertDisplayed(), "Error alert should be displayed");
    }

    @Test(priority = 5, description = "Test login with empty credentials")
    public void testEmptyCredentialsLogin() {
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.login("", "");
        // HTML5 validation should prevent submission
        Assert.assertTrue(driver.getCurrentUrl().contains("index.html"), "Should stay on login page");
    }

    @Test(priority = 6, description = "Test login with only username")
    public void testLoginWithoutPassword() {
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.enterUsername("testemployee");
        loginPage.clickSignIn();
        // HTML5 validation should prevent submission
        Assert.assertTrue(driver.getCurrentUrl().contains("index.html"), "Should stay on login page");
    }
}