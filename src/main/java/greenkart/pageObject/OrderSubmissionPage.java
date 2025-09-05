package greenkart.pageObject;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import greenkart.config.Asserts;
import greenkart.config.ElementActions;
import greenkart.config.Waits;

public class OrderSubmissionPage {

	private static Logger log = LogManager.getLogger(OrderSubmissionPage.class.getName());
	
	private static final int SHORT_TIMEOUT = 5;
	private static final int MEDIUM_TIMEOUT = 10;

	WebDriver driver;
	ElementActions e;
	Waits wait;
	Asserts a;

	public OrderSubmissionPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "select[style='width: 200px;']")
	private WebElement selectCountry;

	@FindBy(xpath = "//input[@type='checkbox']")
	private WebElement checkbox;

	@FindBy(xpath = "//button[text()='Proceed']")
	private WebElement proceed;

	@FindBy(className = "errorAlert")
	private WebElement errorAlert;

	@FindBy(css = "a[href*='policy']")
	private WebElement terms;

	@FindBy(className = "wrapperTwo")
	private WebElement termsNewTab;

	@FindBy(css = "span[style='color:green;font-size:25px']")
	private WebElement successfulMessage;

	@FindBy(linkText = "Home")
	private WebElement homeButton;

	public void validateSelectedCountry(String country) {
		wait = new Waits(driver);
		a = new Asserts(driver);
		e = new ElementActions(driver);

		wait.waitForVisibilityOf(SHORT_TIMEOUT, selectCountry, "Select Country");
		e.selectBy("value", selectCountry, country);
		log.info("Checking if country: " + country + " is selected");
		a.assertEquals(e.jsExecutorGetText(selectCountry), country);
	}

	public void validateErrorAlert() {
		wait = new Waits(driver);
		a = new Asserts(driver);
		String errorMessage = "Please accept Terms & Conditions - Required";
		
		wait.waitForVisibilityOf(SHORT_TIMEOUT, proceed, "Proceed");
		proceed.click();
		log.info("Clicked Proceed button without accepting Terms & Conditions");
		wait.waitForVisibilityOf(MEDIUM_TIMEOUT, errorAlert, "Error Alert");
		log.info("Checking if error message is displayed in red color");
		a.assertEquals(errorAlert.getText(), errorMessage);
		a.assertTrue(errorAlert.getDomAttribute("style").contains("red"), "Error message is displayed in red color", "Error message is not displayed in red color");
	}

	public void validateTerms() {
		wait = new Waits(driver);
		a = new Asserts(driver);

		String expectedMessage = "Here the terms and condition page Click to geo back Home";
		wait.waitForVisibilityOf(SHORT_TIMEOUT, terms, "Terms & Conditions");
		terms.click();
		log.info("Clicked on 'Terms & Conditions' button");

		String mainTab = driver.getWindowHandle();
		Set<String> allTabs = driver.getWindowHandles();
		for (String tab : allTabs) {
			if (!tab.equals(mainTab)) {
				driver.switchTo().window(tab);
				log.info("Switched to 'Terms & Conditions' new tab");
				break;
			}
		}
		log.info("Checking if 'Terms & Conditions' new tab text is correct");
		wait.waitForVisibilityOf(SHORT_TIMEOUT, termsNewTab, "Terms from New Tab");
		a.assertEquals(termsNewTab.getText(), expectedMessage);
		driver.close();
		log.info("Closed 'Terms & Conditions' tab");
		driver.switchTo().window(mainTab);
		log.debug("Switched back to main window");
	}

	public void validateSubmitOrder() {
		wait = new Waits(driver);
		a = new Asserts(driver);

		String expectedMessage = "Thank you, your order has been placed successfully\n"
				+ "You'll be redirected to Home page shortly!!";
		wait.waitForVisibilityOf(SHORT_TIMEOUT, checkbox, "Checkbox");
		wait.waitForVisibilityOf(SHORT_TIMEOUT, proceed, "Proceed");
		if (checkbox.isSelected()) {
			log.debug("Checkbox is already selected");
			proceed.click();
			log.info("Clicked Proceed button");
		} else {
			checkbox.click();
			log.info("Selected checkbox");
			proceed.click();
			log.info("Clicked Proceed button");
		}
		wait.waitForVisibilityOf(SHORT_TIMEOUT, successfulMessage, "Successful Message");
		log.info("Checking if the order success message is correct");
		a.assertEquals(successfulMessage.getText(), expectedMessage);
		wait.waitForVisibilityOf(SHORT_TIMEOUT, homeButton, "Home Button");
		homeButton.click();
		log.info("Clicked Home button to return to main page");
	}
}