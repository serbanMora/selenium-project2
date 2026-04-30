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
			log.info("Waiting for up to " + duration + " seconds for '" + elementName + "' element to be visible");
			
			waits = new FluentWait<WebDriver>(driver)
					.withTimeout(Duration.ofSeconds(duration))
					.pollingEvery(Duration.ofSeconds(polling))
					.ignoring(NoSuchElementException.class);
			
			long start = System.currentTimeMillis();
			waits.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				if (element.isDisplayed()) {
					return element;
				} else {
					return null;
				}
			}
		});
			double elapsed = (System.currentTimeMillis() - start) / 1000.0;
			
			log.info(elementName + " element is visible after waiting " + elapsed + " seconds");
		} catch (TimeoutException e) {
			log.error(elementName + " element was not visible after waiting " + duration + " seconds - test execution stopped");
			throw e;
		} catch (NoSuchElementException e) {
			log.error(elementName + " element was not found - test execution stopped");
			throw e;
		}
	}
	
	public void waitForConditionToBeTrue(int duration, Function<WebDriver, Boolean> condition, String conditionName) {
		try {
			log.info("Waiting for up to " + duration + " seconds for condition: " + conditionName);

			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			long start = System.currentTimeMillis();

			wait.until(driver -> Boolean.TRUE.equals(condition.apply(driver)));

			double elapsed = (System.currentTimeMillis() - start) / 1000.0;
			log.info("Condition '" + conditionName + "' became true after waiting " + elapsed + " seconds");

		} catch (TimeoutException e) {
			log.error("Condition '" + conditionName + "' did not become true after waiting " + duration + " seconds - test execution stopped");
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
			log.info("Waiting for up to " + duration + " seconds for '" + elementName + "' elements to be visible");

			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			long start = System.currentTimeMillis();
			wait.until(ExpectedConditions.visibilityOfAllElements(element));
			double elapsed = (System.currentTimeMillis() - start) / 1000.0;

			log.info(elementName + " elements are visible after waiting " + elapsed + " seconds");
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
			log.info("Waiting for up to " + duration + " seconds for '" + elementName + "' element to be visible");
			
			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			long start = System.currentTimeMillis();
	        wait.until(ExpectedConditions.visibilityOf(element));
	        double elapsed = (System.currentTimeMillis() - start) / 1000.0;
	        
			log.info(elementName + " element is visible after waiting " + elapsed + " seconds");
		} catch (TimeoutException e) {
			log.error(elementName + " element was not visible after waiting " + duration + " seconds - test execution stopped");
			throw e;
		} catch (NoSuchElementException e) {
			log.error(elementName + " element was not found - test execution stopped");
			throw e;
		}
	}
	
	public void waitForInvisibilityOf(int duration, WebElement element, String elementName) {
		try {
			log.info("Waiting for up to " + duration + " seconds for '" + elementName + "' element to be invisible");
			
			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			long start = System.currentTimeMillis();
			wait.until(ExpectedConditions.invisibilityOf(element));
			double elapsed = (System.currentTimeMillis() - start) / 1000.0;
			
			log.info(elementName + " element is invisible after waiting " + elapsed + " seconds");
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
			log.info("Waiting for up to " + duration + " seconds for '" + elementName + "' element to be clickable");
			
			wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
			long start = System.currentTimeMillis();
			wait.until(ExpectedConditions.elementToBeClickable(element));
			double elapsed = (System.currentTimeMillis() - start) / 1000.0;
			
			log.info(elementName + " element is clickable after waiting " + elapsed + " seconds");
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
	
	public void waitForTitleToChange(int duration, String text) {
	    try {
	        wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
	        wait.until(driver -> driver.getTitle().equals(text));
	    } catch (TimeoutException e) {
	        log.error(driver.getTitle() + " page title did not change to '" + text + "', after waiting " + duration + " seconds - test execution stopped");
	        throw e;
	    }
	}
	
	public void waitForURLChange(int duration, String text) {
	    try {
	        wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
	        wait.until(driver -> driver.getCurrentUrl().contains(text));
	    } catch (TimeoutException e) {
	        log.error(driver.getCurrentUrl() + " URL does not contain the '" + text + "', after waiting " + duration + " seconds - test execution stopped");
	        throw e;
	    }
	}
}