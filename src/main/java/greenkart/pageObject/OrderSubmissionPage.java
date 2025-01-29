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

public class OrderSubmissionPage {

	WebDriver driver;
	
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
	
	@FindBy (css = "span[class='errorAlert']")
	private WebElement errorAlert;
	
	@FindBy (css = "a[href*='policy']")
	private WebElement terms;
	
	@FindBy (css = "div[class='wrapperTwo']")
	private WebElement termsNewTab;
	
	@FindBy (css = "span[style='color:green;font-size:25px']")
	private WebElement successfulMessage;
	
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
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		proceed.click();
		wait.until(ExpectedConditions.visibilityOf(errorAlert));
		Assert.assertEquals(errorAlert.getText(), "Please accept Terms & Conditions - Required");
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
	}
}