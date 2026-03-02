package com.elms.ui.tests;

import com.elms.ui.pages.LoginPage;
import com.elms.ui.pages.ManagerDashboardPage;
import io.github.boridshova.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class ManagerUITest {

    private WebDriver driver;
    private LoginPage loginPage;
    private ManagerDashboardPage managerDashboard;
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
        managerDashboard = new ManagerDashboardPage(driver);
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.login("testmanager", "manager123");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void testDashboardLoads() {
        Assert.assertTrue(managerDashboard.isDashboardLoaded());
    }

    @Test(priority = 2)
    public void testViewPendingRequests() {
        int count = managerDashboard.getPendingRequestsCount();
        Assert.assertTrue(count >= 0);
    }
}