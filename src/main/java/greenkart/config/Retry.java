package greenkart.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {

	private static Logger log = LogManager.getLogger(Retry.class.getName());

	private int retryCount = 0;
	private static final int maxRetryCount = 1;

	@Override
	public boolean retry(ITestResult result) {
		if (retryCount < maxRetryCount) {
			retryCount++;
			log.warn("Test '" + result.getName() + "' failed. Retrying " + retryCount + " out of " + maxRetryCount);
			return true;
		}
		log.error("Test '" + result.getName() + "' failed after " + maxRetryCount + " retries. No more retries");
		return false;
	}
}