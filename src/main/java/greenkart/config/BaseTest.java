package greenkart.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import io.qameta.allure.Allure;

public class BaseTest {

	public WebDriver driver;
	
	@BeforeClass
	public void setUP() throws IOException {
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
			}
			driver = new ChromeDriver(options);

		} else if (browserName.equals("firefox")) {
			System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
			driver = new FirefoxDriver();

		} else if (browserName.equals("edge")) {
			System.setProperty("webdriver.ie.driver", edgeDriverPath);
			driver = new EdgeDriver();
		}
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
		driver.get(url);
	}

	@AfterClass
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}
	
	@AfterMethod
	public void testRunLog() {
		try {
			File logFile = new File("logs/test-run.log");
			if (logFile.exists()) {
				Allure.addAttachment("Test Log", new FileInputStream(logFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}