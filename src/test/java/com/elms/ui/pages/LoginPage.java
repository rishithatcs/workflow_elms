package com.elms.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for Login Page
 * Encapsulates all elements and actions on the login page
 */
public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By usernameInput = By.id("username");
    private By passwordInput = By.id("password");
    private By signInButton = By.cssSelector("button[type='submit']");
    private By alertMessage = By.cssSelector("[class*='alert']");
    private By registerLink = By.linkText("Create one");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateToLoginPage(String baseUrl) {
        driver.get(baseUrl + "/index.html");
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        driver.findElement(usernameInput).clear();
        driver.findElement(usernameInput).sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordInput).clear();
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickSignIn() {
        driver.findElement(signInButton).click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSignIn();
    }

    public boolean isAlertDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
            return driver.findElement(alertMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getAlertMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
        return driver.findElement(alertMessage).getText();
    }

    public void clickRegisterLink() {
        driver.findElement(registerLink).click();
    }

    public boolean isUserRedirected() {
        try {
            Thread.sleep(2000); // Wait for redirection
            String currentUrl = driver.getCurrentUrl();
            return currentUrl.contains("employee-dashboard") || currentUrl.contains("manager-dashboard");
        } catch (InterruptedException e) {
            return false;
        }
    }
}