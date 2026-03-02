package com.elms.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model for Manager Dashboard
 * Encapsulates all elements and actions on the manager dashboard
 */
public class ManagerDashboardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By userNameLabel = By.id("userName");
    private By logoutButton = By.xpath("//button[contains(text(), 'Logout')]");
    private By pendingTableRows = By.cssSelector("#leaveTableBody tr");
    private By allLeaveTableRows = By.cssSelector("#allLeaveTableBody tr");
    private By commentsInput = By.id("commentsInput");
    private By commentModal = By.id("commentModal");
    private By submitModalButton = By.xpath("//div[@id='commentModal']//button[contains(text(), 'Submit')]");

    public ManagerDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String getUserName() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(userNameLabel));
        return driver.findElement(userNameLabel).getText();
    }

    public int getPendingRequestsCount() {
        try {
            Thread.sleep(2000); // Wait for AJAX to load
            List<WebElement> rows = driver.findElements(pendingTableRows);
            return rows.size();
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public int getAllLeaveRequestsCount() {
        try {
            Thread.sleep(2000); // Wait for AJAX to load
            List<WebElement> rows = driver.findElements(allLeaveTableRows);
            return rows.size();
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public void clickFirstApproveButton() {
        try {
            Thread.sleep(1500); // Wait for table to load
            WebElement approveButton = driver.findElement(By.cssSelector("#leaveTableBody button.btn-success"));
            approveButton.click();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clickFirstRejectButton() {
        try {
            Thread.sleep(1500); // Wait for table to load
            WebElement rejectButton = driver.findElement(By.cssSelector("#leaveTableBody button.btn-danger"));
            rejectButton.click();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void enterComments(String comments) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(commentsInput));
        WebElement input = driver.findElement(commentsInput);
        input.clear();
        input.sendKeys(comments);
    }

    public void clickSubmitModal() {
        driver.findElement(submitModalButton).click();
    }

    public void approveFirstLeaveRequest(String comments) {
        clickFirstApproveButton();
        enterComments(comments);
        clickSubmitModal();
    }

    public void rejectFirstLeaveRequest(String comments) {
        clickFirstRejectButton();
        enterComments(comments);
        clickSubmitModal();
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

    public boolean hasPendingRequests() {
        return getPendingRequestsCount() > 0;
    }
}