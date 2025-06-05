package greenkart.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
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
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/greenkart/config/data.properties");
		prop.load(fis);
		String browserName = prop.getProperty("browser");
		String chromeDriverPath = prop.getProperty("chromeDriverPath");
		String firefoxDriverPath = prop.getProperty("firefoxDriverPath");
		String edgeDriverPath = prop.getProperty("edgeDriverPath");
		String url = prop.getProperty("url");

		if (browserName.contains("chrome")) {
			System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			ChromeOptions options = new ChromeOptions();
			if (browserName.contains("headless")) {
				options.addArguments("--headless");
				log.info("Tests Running in headless Chrome");
			}
			driver = new ChromeDriver(options);
			log.info("Tests Running in Chrome browser");

		} else if (browserName.equals("firefox")) {
			System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
			driver = new FirefoxDriver();
			log.info("Tests Running in Firefox browser");

		} else if (browserName.equals("edge")) {
			System.setProperty("webdriver.ie.driver", edgeDriverPath);
			driver = new EdgeDriver();
			log.info("Tests Running in Edge browser");
		}
		driver.manage().window().maximize();
		log.info("Browser window maximized");
		driver.manage().deleteAllCookies();
		log.info("Browser cookies deleted");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
		driver.get(url);
		log.info("Test object: " + url);
	}

	@AfterClass
	public void tearDown() {
		if (driver != null) {
			driver.quit();
			log.info("Test finished, browser was closed");
		} else {
			log.error("Browser driver was null at teardown, browser was not closed.");
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
			e.printStackTrace();
		}
	}
	
	public void attachScreenshot(String name) {
	    try {
	        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
	        Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}