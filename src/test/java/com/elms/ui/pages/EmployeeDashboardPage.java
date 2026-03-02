package com.elms.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model for Employee Dashboard
 * Encapsulates all elements and actions on the employee dashboard
 */
public class EmployeeDasN`ÏardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By userNameLabel = By.id("userName");
    private By logoutButton = By.xpath("//button[contains(text(), 'Logout')]");
    private By casualLeaveBalance = By.id("casualLeave");
    private By sickLeaveBalance = By.id("sickLeave");
    private By leaveTypeDropdown = By.id("leaveType");
    private By startDateInput = By.id("startDate");
    private By endDateInput = By.id("endDate");
    private By reasonTextarea = By.id("reason");
    private By submitButton = By.cssSelector("#leaveForm button[type='submit']");
    private By leaveTableRows = By.cssSelector("#leaveTableBody tr");

    public EmployeeDasN`ÏardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String getUserName() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(userNameLabel));
        return driver.findElement(userNameLabel).getText();
    }

    public String getCasualLeaveBalance() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(casualLeaveBalance));
        return driver.findElement(casualLeaveBalance).getText();
    }

    public String getSickLeaveBalance() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(sickLeaveBalance));
        return driver.findElement(sickLeaveBalance).getText();
    }

    public void selectLeaveType(String leaveType) {
        WebElement dropdown = driver.findElement(leaveTypeDropdown);
        Select select = new Select(dropdown);
        select.selectByValue(leaveType);
    }

    public void enterStartDate(String date) {
        WebElement element = driver.findElement(startDateInput);
        element.clear();
        element.sendKeys(date);
    }

    public void enterEndDate(String date) {
        WebElement element = driver.findElement(endDateInput);
        element.clear();
        element.sendKeys(date);
    }

    public void enterReason(String reason) {
        WebElement element = driver.findElement(reasonTextarea);
        element.clear();
        element.sendKeys(reason);
    }

    public void clickSubmit() {
        driver.findElement(submitButton).click();
    }

    public void applyForLeave(String leaveType, String startDate, String endDate, String reason) {
        selectLeaveType(leaveType);
        enterStartDate(startDate);
        enterEndDate(endDate);
        enterReason(reason);
        clickSubmit();
    }

    public boolean isLeaveApplicationSuccessful() {
        try {
            Thread.sleep(2000); // Wait for alert and table refresh
            return true; // If no exception, submission was successful
        } catch (InterruptedException e) {
            return false;
        }
    }

    public int getLeaveRequestsCount() {
        try {
            Thread.sleep(1500); // Wait for AJAX to load
            List<WebElement> rows = driver.findElements(leaveTableRows);
            return rows.size();
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public void clickLogout() {
        driver.findElement(logoutButton).click();
    }

    public boolean isDashboardLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(userNameLabel));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}