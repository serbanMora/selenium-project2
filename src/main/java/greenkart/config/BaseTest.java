package greenkart.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import io.qameta.allure.Allure;

public class BaseTest {
	
	private static Logger log = LogManager.getLogger(BaseTest.class.getName());

	public WebDriver driver;
	
	@BeforeClass
	public void setUP() throws IOException {
		log.info("Test Execution Started");
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/greenkart/config/browser.properties");
		prop.load(fis);
		String browserName = System.getenv("BROWSER") != null ? System.getenv("BROWSER") : prop.getProperty("browser");
		String chromeDriverPath = System.getenv("CHROME_PATH") != null ? System.getenv("CHROME_PATH") : System.getProperty("user.dir") + "/drivers/chromedriver.exe";
		String firefoxDriverPath = System.getenv("FIREFOX_PATH") != null ? System.getenv("FIREFOX_PATH") : System.getProperty("user.dir") + "/drivers/geckodriver.exe";
		String edgeDriverPath = System.getenv("EDGE_PATH") != null ? System.getenv("EDGE_PATH") : System.getProperty("user.dir") + "/drivers/msedgedriver.exe";

		if (browserName.contains("chrome")) {
			System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			ChromeOptions options = new ChromeOptions();
			if (browserName.contains("headless")) {
				options.addArguments("--headless");
				options.addArguments("--disable-gpu");
				options.addArguments("--no-sandbox");
				log.info("Tests Running in headless Chrome");
			}
			driver = new ChromeDriver(options);
			log.info("Tests Running in Chrome browser");

		} else if (browserName.contains("firefox")) {
			System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
			FirefoxOptions options = new FirefoxOptions();
			if (browserName.contains("headless")) {
				options.addArguments("--headless");
				options.addArguments("--disable-gpu");
				options.addArguments("--no-sandbox");
				log.info("Tests Running in headless Firefox");
			}
			driver = new FirefoxDriver(options);
			log.info("Tests Running in Firefox browser");

		} else if (browserName.contains("edge")) {
			System.setProperty("webdriver.ie.driver", edgeDriverPath);
			EdgeOptions options = new EdgeOptions();
			if (browserName.contains("headless")) {
				options.addArguments("--headless");
				options.addArguments("--disable-gpu");
				options.addArguments("--no-sandbox");
				log.info("Tests Running in headless Edge");
			}
			driver = new EdgeDriver(options);
			log.info("Tests Running in Edge browser");
		}
		
		if (browserName.contains("headless")) {
			driver.manage().window().setSize(new Dimension(1920, 1080));
			log.info("Browser window was set to 1920x1080 in headless mode");
		} else {
			driver.manage().window().maximize();
			log.info("Browser window maximized");
		}
		driver.manage().deleteAllCookies();
		log.info("Browser cookies deleted");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
	}
	
	public static void logURL(String URL) {
		log.info("Navigated to test URL: " + URL);
	}
	
	@AfterClass(alwaysRun = true, enabled = true)
	public void tearDown() {
		try {
			if (driver != null) {
				driver.quit();
				log.info("Test finished, browser was closed");
			} else {
				log.warn("Browser driver is null at teardown. Browser may not have started properly");
			}
		} catch (Exception e) {
			log.error("Exception occurred while closing the browser: " + e.getMessage(), e);
		}
	}
	
	@AfterMethod
	public void afterTest(ITestResult result) {
	    if (result.getStatus() == ITestResult.FAILURE) {
	        attachScreenshot("Failed Test Screenshot");
	        log.error("Test '" + result.getName() + "' failed. Screenshot attached in Allure report.");
	    }
	}

	@AfterMethod
	public void testRunLog() {
		try {
			File logFile = new File("logs/test-run.log");
			if (logFile.exists()) {
				Allure.addAttachment("GreenKart - Test Log", new FileInputStream(logFile));
			}
		} catch (Exception e) {
			log.error("Failed to attach Test Log: " + e.getMessage(), e);
		}
	}
	
	public void attachScreenshot(String name) {
	    try {
	        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
	        Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
	    } catch (Exception e) {
	    	log.error("Failed to attach screenshot: " + e.getMessage(), e);
	    }
	}
}