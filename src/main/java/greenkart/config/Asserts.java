package greenkart.config;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

public class Asserts {

	private static Logger log = LogManager.getLogger(Asserts.class.getName());

	WebDriver driver;

	public Asserts(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void assertEquals(String actual, String expected) {
		try {
			Assert.assertEquals(actual, expected);
			log.info("Assertion Passed: Expected '" + expected + "', found '" + actual + "'");
		} catch (AssertionError e) {
			log.error("Assertion Failed: Expected '" + expected + "', but found '" + actual + "'");
		}
	}
	
	public void assertEquals(int actual, int expected) {
		try {
			Assert.assertEquals(actual, expected);
			log.info("Assertion Passed: Expected '" + expected + "', found '" + actual + "'");
		} catch (AssertionError e) {
			log.error("Assertion Failed: Expected '" + expected + "', but found '" + actual + "'");
		}
	}
	
	public void assertEquals(double actual, double expected) {
		try {
			Assert.assertEquals(actual, expected);
			log.info("Assertion Passed: Expected '" + expected + "', found '" + actual + "'");
		} catch (AssertionError e) {
			log.error("Assertion Failed: Expected '" + expected + "', but found '" + actual + "'");
		}
	}
	
	public void assertEquals(List<String> actual , List<String> expected) {
		try {
			Assert.assertEquals(actual, expected);
			log.info("Assertion Passed: Expected '" + expected + "', found '" + actual + "'");
		} catch (AssertionError e) {
			log.error("Assertion Failed: Expected '" + expected + "', but found '" + actual + "'");
		}
	}
	
	public void assertEquals(Set<String> actual , TreeSet<String> expected) {
		try {
			Assert.assertEquals(actual, expected);
			log.info("Assertion Passed: Expected '" + expected + "', found '" + actual + "'");
		} catch (AssertionError e) {
			log.error("Assertion Failed: Expected '" + expected + "', but found '" + actual + "'");
		}
	}
	
	public void assertTrue(boolean condition, String message) {
	    try {
	        Assert.assertTrue(condition);
	        log.info("Assertion Passed: " + message);
	    } catch (AssertionError e) {
	        log.error("Assertion Failed: " + message);
	    }
	}
	
	public void assertTrue(boolean condition, String passMessage, String faillMessage) {
	    try {
	        Assert.assertTrue(condition);
	        log.info("Assertion Passed: " + passMessage);
	    } catch (AssertionError e) {
	        log.error("Assertion Failed: " + faillMessage);
	    }
	}
	
	public void assertTrue(boolean condition1, boolean condition2, String message) {
	    try {
	        Assert.assertTrue(condition1 && condition2);
	        log.info("Assertion Passed: " + message);
	    } catch (AssertionError e) {
	        log.error("Assertion Failed: " + message);
	    }
	}
}