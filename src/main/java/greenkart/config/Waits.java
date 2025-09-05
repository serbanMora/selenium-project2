package greenkart.config;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Waits {
	
	private static Logger log = LogManager.getLogger(Waits.class.getName());

	WebDriver driver;
	WebDriverWait wait;
	Wait<WebDriver> waits;
	
	public Waits(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void fluentWait(int duration, int polling, WebElement element, String elementName) {
		try {
			waits = new FluentWait<WebDriver>(driver)
					.withTimeout(Duration.ofSeconds(duration))
					.pollingEvery(Duration.ofSeconds(polling))
					.ignoring(NoSuchElementException.class);

			waits.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				if (element.isDisplayed()) {
					return element;
				} else {
					return null;
				}
			}
		});
		} catch (TimeoutException e) {
			log.error(elementName + " element was not visible after waiting " + duration + " seconds - test execution stopped");
			throw e;
		} catch (NoSuchElementException e) {
			log.error(elementName + " element was not found - test execution stopped");
			throw e;
		}
	}
	
	public void waitForNewMessage(int duration, List<WebElement> elements, int initialCount, String elementName) {
	    try {
	        wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
	        wait.until(driver -> elements.size() > initialCount);
	        log.info("New " + elementName + " appeared after initial count: " + initialCount);
	    } catch (TimeoutException e) {
	        log.error("No new " + elementName + " appeared after waiting " + duration + " seconds");
	        throw e;
	    }
	}
	
	public void waitForVisibilityOfAll(int duration, List<WebElement> element, String elementName) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			wait.until(ExpectedConditions.visibilityOfAllElements(element));
		} catch (TimeoutException e) {
			log.error(elementName + " elements were not visible after waiting " + duration + " seconds - test execution stopped");
			throw e;
		} catch (NoSuchElementException e) {
			log.error(elementName + " elements were not found - test execution stopped");
			throw e;
		}
	}
	
	public void waitForVisibilityOf(int duration, WebElement element, String elementName) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (TimeoutException e) {
			log.error(elementName + " element was not visible after waiting " + duration + " seconds - test execution stopped");
			throw e;
		} catch (NoSuchElementException e) {
			log.error(elementName + " element was not found - test execution stopped");
			throw e;
		}
	}
	
	public void waitForVisibilityOfWithoutThrow(int duration, WebElement element, String elementName) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (TimeoutException e) {
			log.error(elementName + " element was not visible after waiting " + duration + " seconds");
		} catch (NoSuchElementException e) {
			log.error(elementName + " element was not found");
		}
	}
	
	public void waitForInvisibilityOf(int duration, WebElement element, String elementName) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			wait.until(ExpectedConditions.invisibilityOf(element));
		} catch (TimeoutException e) {
			log.error(elementName + " element was still visible after waiting " + duration + " seconds - test execution stopped");
			throw e;
		} catch (NoSuchElementException e) {
			log.error(elementName + " element was not found - test execution stopped");
			throw e;
		}
	}
	
	public void waitForElementToBeClickable(int duration, WebElement element, String elementName) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			wait.until(ExpectedConditions.elementToBeClickable(element));
		} catch (TimeoutException e) {
			log.error(elementName + " element was not clickable after waiting " + duration + " seconds - test execution stopped");
			throw e;
		} catch (NoSuchElementException e) {
			log.error(elementName + " element was not found - test execution stopped");
			throw e;
		}
	}
	
	public void waitForTextToBeChanged(int duration, WebElement element, String elementName) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element, element.getText())));
		} catch (TimeoutException e) {
		} catch (NoSuchElementException e) {
		}
	}
}