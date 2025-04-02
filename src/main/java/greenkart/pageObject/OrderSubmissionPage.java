package greenkart.pageObject;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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

	WebDriver driver;
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
	}

	public String jsExecutorGetText(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String text = (String) js.executeScript("return arguments[0].value;", element);
		return text;
	}
	
	public void validateErrorAlert() {
		softAssert = new SoftAssert();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		proceed.click();
		wait.until(ExpectedConditions.visibilityOf(errorAlert));
		softAssert.assertEquals(errorAlert.getText(), "Please accept Terms & Conditions - Required");
		softAssert.assertTrue(errorAlert.getDomAttribute("style").contains("red"));
		softAssert.assertAll();
	}
	
	public void validateTerms() {
		terms.click();
		Set<String> handles = driver.getWindowHandles();
		Iterator<String> it = handles.iterator();
		String parentWindow = it.next();
		String childWindow = it.next();
		driver.switchTo().window(childWindow);
		Assert.assertEquals(termsNewTab.getText(), "Here the terms and condition page Click to geo back Home");
		driver.close();
		driver.switchTo().window(parentWindow);
	}
	
	public void validateSubmitOrder() {
		if (checkbox.isSelected()) {
			proceed.click();
		} else {
			checkbox.click();
			proceed.click();
		}
		Assert.assertEquals(successfulMessage.getText(), "Thank you, your order has been placed successfully\n" + "You'll be redirected to Home page shortly!!");
		homeButton.click();
	}
}