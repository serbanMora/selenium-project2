package greenkart.config;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;

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
	
	public void switchTab(String window) {
		Set<String> handles = driver.getWindowHandles();
		Iterator<String> it = handles.iterator();
		String parentWindow = it.next();
		String childWindow = it.next();
		if (window.equals("child")) {
			driver.switchTo().window(childWindow);
			log.info("Switched to child window");
		} else if (window.equals("parent")) {
			driver.switchTo().window(parentWindow);
			log.info("Switched to parent window");
		} else {
			log.error("Invalid window type: " + window);
		}
	}
	
	public void closeTabContainingSlug(String slug) {
		for (String handle : driver.getWindowHandles()) {
			driver.switchTo().window(handle);

			String url = driver.getCurrentUrl();
			if (url.contains(slug)) {
				log.info("Closing tab: " + driver.getCurrentUrl());
				driver.close();
			}
		}
		if (!driver.getWindowHandles().isEmpty()) {
			driver.switchTo().window(driver.getWindowHandles().iterator().next());
		} else {
			log.error("No remaining window to switch to after closing tabs.");
		}
	}
	
	public String getCurrentDate() {
		LocalDate ld = LocalDate.now();
		String date = ld.toString();
		return date;
	}

	public String dateFormatConversion(String monthYear) {
		DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
		YearMonth yearMonth = YearMonth.parse(monthYear, monthYearFormatter);
		String formattedDate = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
		return formattedDate;
	}
	
	public String jsExecutorGetText(WebElement element) {
		js = (JavascriptExecutor) driver;
		String text = (String) js.executeScript("return arguments[0].value;", element);
		return text;
	}
	
	public String jsExecutorGetText(String script, WebElement element) {
		js = (JavascriptExecutor) driver;
		String text = (String) js.executeScript(script, element);
		return text;
	}
	
	public void scrollTo(int index1, int index2) {
		js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(" + index1 + ", " + index2 + ");");
	}
	
	public void scrollIntoView(WebElement element) {
		js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
	}
}