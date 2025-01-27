package greenkart.pageObject;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class OrderSubmissionPage {

	WebDriver driver;
	
	public OrderSubmissionPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (css = "select[style='width: 200px;']")
	private WebElement selectCountry;
	
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
}