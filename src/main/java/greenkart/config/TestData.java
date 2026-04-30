package greenkart.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestData {
	
	private static Logger log = LogManager.getLogger(TestData.class.getName());
	private final Properties properties = new Properties();

	public TestData(String environment) {
		String fileName = null;
		String logMessage = "Initializing TestData with " + environment + " environment...";
		switch (environment.toLowerCase()) {
		case "staging":
			log.info(logMessage);
			fileName = System.getProperty("user.dir") + "/src/main/java/greenkart/config/staging.properties";
			break;
		case "production":
			log.info(logMessage);
			fileName = System.getProperty("user.dir") + "/src/main/java/greenkart/config/production.properties";
			break;
		default:
			log.fatal("Unknown environment: " + environment);
		}

		try (FileInputStream fis = new FileInputStream(fileName)) {
			properties.load(fis);
			log.info("Successfully loaded TestData for " + environment + " environment");
		} catch (IOException e) {
			log.fatal("Failed to load properties file: " + fileName, e);
		}
	}
	
	private String[] getArray(String key) {
		String value = properties.getProperty(key);
		return value != null ? value.split(",") : new String[0];
	}

	public String getUrl() {
		return properties.getProperty("url");
	}
	
	public String[] getProducts() {
		return getArray("products");
	}
	
	public String getProduct() {
		return properties.getProperty("product");
	}
	
	public String getKeyword() {
		return properties.getProperty("keyword");
	}
	
	public String getQuantity() {
		return properties.getProperty("quantity");
	}
	
	public String getDiscountCode() {
		return properties.getProperty("discountCode");
	}
	
	public String getCountry() {
		return properties.getProperty("country");
	}
	
	public String getPageSize() {
		return properties.getProperty("pageSize");
	}
	
	public String getMonthYear() {
		return properties.getProperty("monthYear");
	}
	
	public String getDay() {
		return properties.getProperty("day");
	}
}