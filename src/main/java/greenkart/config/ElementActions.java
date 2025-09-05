package greenkart.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class ElementActions {
	
	private static Logger log = LogManager.getLogger(ElementActions.class.getName());

	WebDriver driver;
	Select s;
	JavascriptExecutor js;
	
	public ElementActions(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectBy(String type, WebElement element, String text) {
		try {
			s = new Select(element);
			switch (type) {
			case "value":
				s.selectByValue(text);
				log.info("Selected option by value: '" + text + "'");
				break;
			case "text":
				s.selectByVisibleText(text);
				log.info("Selected option by visible text: '" + text + "'");
				break;
			}
		} catch (NoSuchElementException e) {
			String error = "Option '" + text + "' not found in dropdown - test execution stopped";
			log.error(error);
			Assert.fail(error);
		}
	}
	
	public String jsExecutorGetText(WebElement element) {
		js = (JavascriptExecutor) driver;
		String text = (String) js.executeScript("return arguments[0].value;", element);
		return text;
	}
	
	public void scrollTo(int index1, int index2) {
		js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(" + index1 + ", " + index2 + ");");
	}
}