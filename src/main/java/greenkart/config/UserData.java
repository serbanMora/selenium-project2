package greenkart.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserData {
	
	private static Logger log = LogManager.getLogger(UserData.class.getName());
	private final Properties properties = new Properties();

	public UserData(String environment) {
		String fileName = null;
		String logMessage = "Initializing UserData with " + environment + " environment...";
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
			log.info("Successfully loaded properties for " + environment + " environment");
		} catch (IOException e) {
			log.fatal("Failed to load properties file: " + fileName, e);
		}
	}

	public String getUrl() {
		return properties.getProperty("url");
	}
}