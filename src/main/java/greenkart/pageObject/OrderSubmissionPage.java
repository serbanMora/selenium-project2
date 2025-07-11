package greenkart.pageObject;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class OrderSubmissionPage {

	private static Logger log = LogManager.getLogger(OrderSubmissionPage.class.getName());
	
	WebDriver driver;
	WebDriverWait wait;
	SoftAssert softAssert;
	
	public OrderSubmissionPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (css = "select[style='width: 200px;']")
	private WebElement selectCountry;
	
	@FindBy (xpath = "//input[@type='checkbox']")
	private WebElement checkbox;
	
	@FindBy (xpath = "//button[text()='Proceed']")
	private WebElement proceed;
	
	@FindBy (className = "errorAlert")
	private WebElement errorAlert;
	
	@FindBy (css = "a[href*='policy']")
	private WebElement terms;
	
	@FindBy (className = "wrapperTwo")
	private WebElement termsNewTab;
	
	@FindBy (css = "span[style='color:green;font-size:25px']")
	private WebElement successfulMessage;
	
	@FindBy (linkText = "Home")
	private WebElement homeButton;
	
	public void validateSelectedCountry(String country, String method) {
		try {
			waitForVisibilityOf(5, selectCountry);
			Select s = new Select(selectCountry);

			switch (method) {
			case "byValue":
				s.selectByValue(country);
				break;

			case "byScrolling":
				selectCountry.click();
				int i = 0;
				while (!jsExecutorGetText(selectCountry).equals(country)) {
					i++;
					selectCountry.sendKeys(Keys.ARROW_DOWN);
					if (i > s.getOptions().size()) {
						break;
					}
				}
				break;
			}
			Assert.assertEquals(jsExecutorGetText(selectCountry), country);
			log.info(jsExecutorGetText(selectCountry) + " country was selected");
		} catch (Exception e) {
			log.error(country + " was not found, country selected: " + jsExecutorGetText(selectCountry));
		}
	}

	public String jsExecutorGetText(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String text = (String) js.executeScript("return arguments[0].value;", element);
		return text;
	}
	
	public void validateErrorAlert() {
		String errorMessage = "Please accept Terms & Conditions - Required";
		try {
			softAssert = new SoftAssert();
			proceed.click();
			waitForVisibilityOf(15, errorAlert);
			softAssert.assertEquals(errorAlert.getText(), errorMessage);
			softAssert.assertTrue(errorAlert.getDomAttribute("style").contains("red"));
			softAssert.assertAll();
			log.error(errorAlert.getText() + " error message is displayed");
		} catch (AssertionError e) {
			log.error("Expected: '" + errorMessage + "', but found: '" + errorAlert.getText()+ "'");
		} catch (NoSuchElementException e) {
			log.error(errorAlert.getText() + " error message is not displayed");
		}
	}
	
	public void validateTerms() {
		String expectedMessage = "Here the terms and condition page Click to geo back Home";
		try {
			terms.click();
			log.info("Clicked on 'Terms & Conditions' button");
			Set<String> handles = driver.getWindowHandles();
			Iterator<String> it = handles.iterator();
			String parentWindow = it.next();
			String childWindow = it.next();
			driver.switchTo().window(childWindow);
			log.info("'Terms & Conditions' page opened in new tab");
			Assert.assertEquals(termsNewTab.getText(), expectedMessage);
			log.info(termsNewTab.getText() + " text is displayed on 'Terms & Conditions' page");
			driver.close();
			log.info("'Terms & Conditions' page was closed");
			driver.switchTo().window(parentWindow);
		} catch (AssertionError e) {
			log.error("Expected message: '" + expectedMessage + "', but found: " + termsNewTab.getText());
		} catch (NoSuchElementException e) {
			log.error(termsNewTab.getText() + " message is not displayed");
		}
	}
	
	public void waitForVisibilityOf(int duration, WebElement element) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
		wait.until(ExpectedConditions.visibilityOf(element));
	}
	
	public void validateSubmitOrder() {
		String expectedMessage = "Thank you, your order has been placed successfully\n" + "You'll be redirected to Home page shortly!!";
		try {
			if (checkbox.isSelected()) {
				proceed.click();
			} else {
				checkbox.click();
				proceed.click();
			}
			Assert.assertEquals(successfulMessage.getText(), expectedMessage);
			homeButton.click();
		} catch (AssertionError e) {
			log.error("Expected message: '" + expectedMessage + "', but found: " + successfulMessage.getText());
		} catch (NoSuchElementException e) {
			log.error(successfulMessage.getText() + " message is not displayed");
		}
	}
}