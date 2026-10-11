package Login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminLoginTest {
	private WebDriver driver;
	private WebDriverWait wait;
	private final String url = "https://itassetmanagementsoftware.com/rolepermission/admin/login";
	private final String logFileName = "test_execution_log.txt";

	@BeforeMethod
	public void setUp() {
		// System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")
		// + "/drivers/chromedriver.exe");
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		logToFile("INFO", "Browser opened and navigating to: " + url);
		driver.get(url);
	}

	@Test(priority = 1, description = "Test blank username submission triggers required validation message")
	public void testBlankUsernameValidation() {
		logToFile("INFO", "Starting test: testBlankUsernameValidation");
		try {
			WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
			WebElement continueButton = driver.findElement(By.id("btnContinue"));

			usernameField.clear();
			continueButton.click();
			Thread.sleep(1500);

			WebElement errorMessage = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-error")));
			boolean isDisplayed = errorMessage.isDisplayed();

			if (isDisplayed) {
				logToFile("PASS", "testBlankUsernameValidation passed successfully.");
			}
			
			Assert.assertTrue(errorMessage.isDisplayed(),
					"Required error message should be displayed for blank username submission.");
		} catch (Exception e) {
			logToFile("FAIL/ERROR", "testBlankUsernameValidation failed. Reason: " + e.getMessage());
			Assert.fail("Exception occurred during testBlankUsernameValidation: " + e.getMessage());
		}
	}

	@Test(priority = 2, description = "Test invalid username entry behavior")
	public void testInvalidUsernameLogin() {
		logToFile("INFO", "Starting test: testInvalidUsernameLogin");
		try {
			
			WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
			WebElement continueButton = driver.findElement(By.id("btnContinue"));

			usernameField.clear();
			usernameField.sendKeys("gauravkashelkar");
			continueButton.click();
			Thread.sleep(2000);

			// Verify user stays on the login page or triggers an error alert

			String currentUrl = driver.getCurrentUrl();
			Assert.assertTrue(currentUrl.toLowerCase().contains("login"),
					"User should remain on the login portal when providing an invalid username.");

			WebElement errorMessage = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='validateUserMsg']")));
			
			boolean isDisplayed = errorMessage.isDisplayed();

            if (isDisplayed) {
                logToFile("PASS", "testInvalidUsernameLogin passed successfully.");
            }
            
			Assert.assertTrue(errorMessage.isDisplayed(),
					"Required error message should be displayed for invalid username submission.");
			
		} catch (Exception e) {
			logToFile("FAIL/ERROR", "testInvalidUsernameLogin failed. Reason: " + e.getMessage());
			Assert.fail("Exception occurred during testInvalidUsernameLogin: " + e.getMessage());
		}
	}

	@Test(priority = 3, description = "Test visibility of auxiliary login elements (Microsoft SSO, Forgot Password)")
	public void testLoginUIElementsVisibility() {
		logToFile("INFO", "Starting test: testLoginUIElementsVisibility");
		try {
			// Verify Microsoft Sign button is present
			WebElement microsoftBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnMsLogin")));
			Assert.assertTrue(microsoftBtn.isDisplayed(), "'Sign in with Microsoft' button should be visible.");

			// Verify Forgot Password link is present
			WebElement forgotPasswordLink = driver.findElement(By.xpath("//a[contains(text(), 'Forgot password')]"));
			Assert.assertTrue(forgotPasswordLink.isDisplayed(), "'Forgot password?' link should be visible.");

			// Verify Remember Me checkbox is present
			WebElement rememberMeCheckbox = driver.findElement(By.xpath("//span[text()='Remember me']"));
			Assert.assertTrue(rememberMeCheckbox.isDisplayed(), "'Remember me' checkbox should be visible.");
			
			logToFile("PASS", "testLoginUIElementsVisibility passed successfully.");
		} catch (Exception e) {
			logToFile("FAIL/ERROR", "testLoginUIElementsVisibility failed. Reason: " + e.getMessage());
			Assert.fail("Exception occurred during testLoginUIElementsVisibility: " + e.getMessage());
		}
	}

	@AfterMethod
	public void tearDown() {
		if (driver != null) {
			logToFile("INFO", "Closing browser session.");
			driver.quit();
		}
		logToFile("INFO", "--------------------------------------------------");
	}

	private void logToFile(String status, String message) {
		String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		String logEntry = String.format("[%s] [%s] %s%n", timeStamp, status, message);

		// Also print to console
		System.out.print(logEntry);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
			writer.write(logEntry);
		} catch (IOException e) {
			System.err.println("Error writing to log file: " + e.getMessage());
		}
	}
}