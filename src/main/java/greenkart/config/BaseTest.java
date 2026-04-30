package greenkart.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;
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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
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
		try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/greenkart/config/browser.properties")) {
			prop.load(fis);
			log.info("Successfully loaded browser configuration: " + prop.getProperty("browser"));
		} catch (IOException e) {
			log.fatal("Failed to load browser configuration file", e);
		}
		
		String os = System.getProperty("os.name").toLowerCase();

		String chromeDriverPath = System.getenv("CHROME_PATH") != null ? System.getenv("CHROME_PATH")
				: System.getProperty("user.dir") + "/drivers/" + (os.contains("win") ? "chromedriver.exe" : "chromedriver");

		String firefoxDriverPath = System.getenv("FIREFOX_PATH") != null ? System.getenv("FIREFOX_PATH")
				: System.getProperty("user.dir") + "/drivers/" + (os.contains("win") ? "geckodriver.exe" : "geckodriver");

		String edgeDriverPath = System.getenv("EDGE_PATH") != null ? System.getenv("EDGE_PATH")
				: System.getProperty("user.dir") + "/drivers/" + (os.contains("win") ? "msedgedriver.exe" : "msedgedriver");

		String browserName = System.getenv("BROWSER") != null ? System.getenv("BROWSER") : prop.getProperty("browser");
		boolean isHeadless = browserName.contains("headless");
		
		if (browserName.contains("chrome")) {
			System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			ChromeOptions options = new ChromeOptions();
			if (isHeadless) {
				options.addArguments("--headless", "--disable-gpu", "--no-sandbox");
			}
			driver = new ChromeDriver(options);
			
		} else if (browserName.contains("firefox")) {
			System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
			FirefoxOptions options = new FirefoxOptions();
			if (isHeadless) {
				options.addArguments("--headless", "--disable-gpu", "--no-sandbox");
			}
			driver = new FirefoxDriver(options);

		} else if (browserName.contains("edge")) {
			System.setProperty("webdriver.ie.driver", edgeDriverPath);
			EdgeOptions options = new EdgeOptions();
			if (isHeadless) {
				options.addArguments("--headless", "--disable-gpu", "--no-sandbox");
			}
			driver = new EdgeDriver(options);
		} else {
			String headlessInfo = isHeadless ? " in headless mode" : "";
		    String errorMsg = "Unsupported browser: '" + browserName + "'" + headlessInfo + ". Please use one of: chrome, firefox or edge.";
		    log.error(errorMsg);
		    Assert.fail(errorMsg);
		}
		
		Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
		log.info("Tests Running in " + capabilities.getBrowserName() + " browser. Version: " + capabilities.getBrowserVersion() + ". Driver Version: " + getDriverVersion(browserName) + ". OS: " + System.getProperty("os.name"));
		
		addEnvironment(capabilities.getBrowserName(), isHeadless, capabilities.getBrowserVersion(), getDriverVersion(browserName), System.getProperty("os.name"));

		if (isHeadless) {
			driver.manage().window().setSize(new Dimension(1920, 1080));
			log.info("Tests Running in headless mode, browser window was set to 1920x1080 resolution");
		} else {
			driver.manage().window().maximize();
			log.info("Browser window maximized");
		}
		driver.manage().deleteAllCookies();
		log.info("Browser cookies deleted");
	}
	
	public static void logURL(String URL) {
		log.info("Navigated to test URL: " + URL);
	}
	
	@AfterClass(alwaysRun = true, enabled = true)
	public void tearDown() {
		if (driver == null) {
			log.warn("Driver was null during teardown");
			return;
		}
		try {
			driver.quit();
			log.info("Browser session closed successfully");
		} catch (Exception e) {
			log.error("Failed to close browser session", e);
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
				attachErrorSummary();
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
	
	public void addEnvironment(String browser, boolean headless, String version, String driverVersion, String os) {
	    try (FileWriter writer = new FileWriter("allure-results/environment.properties")) {
	        writer.write("Browser=" + browser + "\n");
	        writer.write("Headless=" + headless + "\n");
	        writer.write("Version=" + version + "\n");
	        writer.write("DriverVersion=" + driverVersion + "\n"); 
	        writer.write("OS=" + os + "\n");
	    } catch (IOException e) {
	    	log.error("Failed to add environment: " + e.getMessage(), e);
	    }
	}
	
	public String getDriverVersion(String browserName) throws IOException {
		String driverPath = null;

		if (browserName.contains("chrome")) {
			driverPath = System.getProperty("webdriver.chrome.driver");
		}
		if (browserName.contains("firefox")) {
			driverPath = System.getProperty("webdriver.gecko.driver");
		}
		if (browserName.contains("edge")) {
			driverPath = System.getProperty("webdriver.ie.driver");
		}
		Process process = new ProcessBuilder(driverPath, "--version").start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String versionLine = reader.readLine();
		if (versionLine != null) {
			versionLine = versionLine.split("\\(")[0].trim();
		}
		return versionLine;
	}
	
	public void attachErrorSummary() {
		try {
			File logFile = new File("logs/error-summary.log");
			if (logFile.exists()) {
				List<String> lines = Files.readAllLines(logFile.toPath(), StandardCharsets.ISO_8859_1);
				String errors = lines.stream().filter(line -> line.contains("ERROR")).collect(Collectors.joining("\n"));
				String allureLog = "GreenKart - Errors Log";
				if (!errors.isEmpty()) {
					Allure.addAttachment(allureLog, "text/plain", errors);
				} else {
					String message = "No errors logged";
					Files.writeString(logFile.toPath(), message, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
					Allure.addAttachment(allureLog, "text/plain", message);
				}
			}
		} catch (Exception e) {
			log.error("Failed to attach error summary: " + e.getMessage(), e);
		}
	}
}