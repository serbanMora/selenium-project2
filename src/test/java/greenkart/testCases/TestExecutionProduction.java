package greenkart.testCases;

import org.testng.annotations.Test;

import greenkart.config.BaseTest;
import greenkart.config.ElementActions;
import greenkart.config.TestData;
import greenkart.pageObject.CheckoutPage;
import greenkart.pageObject.OrderSubmissionPage;
import greenkart.pageObject.ProductCatalog;
import greenkart.pageObject.TopDeals;

public class TestExecutionProduction extends BaseTest {
	
	TestData test;
	ProductCatalog productCatalog;
	CheckoutPage checkoutPage;
	OrderSubmissionPage submissionPage;
	TopDeals topDeals;
	ElementActions e;
	
	private void initPages() {
		test = new TestData("production");
		productCatalog = new ProductCatalog(driver);
		checkoutPage = new CheckoutPage(driver);
		submissionPage = new OrderSubmissionPage(driver);
		topDeals = new TopDeals(driver);
		e = new ElementActions(driver);
	}

	@Test(alwaysRun = true)
	public void TestStart() {
		initPages();
		driver.get(test.getUrl());
		logURL(driver.getCurrentUrl());
	}

	
}