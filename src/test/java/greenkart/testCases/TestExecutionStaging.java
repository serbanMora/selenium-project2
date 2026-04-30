package greenkart.testCases;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import greenkart.config.BaseTest;
import greenkart.config.ElementActions;
import greenkart.config.Retry;
import greenkart.config.TestData;
import greenkart.pageObject.CheckoutPage;
import greenkart.pageObject.OrderSubmissionPage;
import greenkart.pageObject.ProductCatalog;
import greenkart.pageObject.TopDeals;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

public class TestExecutionStaging extends BaseTest {
	
	TestData test;
	ProductCatalog productCatalog;
	CheckoutPage checkoutPage;
	OrderSubmissionPage submissionPage;
	TopDeals topDeals;
	ElementActions e;
	
	private void initPages() {
		test = new TestData("staging");
		productCatalog = new ProductCatalog(driver);
		checkoutPage = new CheckoutPage(driver);
		submissionPage = new OrderSubmissionPage(driver);
		topDeals = new TopDeals(driver);
		e = new ElementActions(driver);
	}

	@BeforeClass(alwaysRun = true)
	public void TestStart() {
		initPages();
	}

	@BeforeMethod(alwaysRun = true)
	public void startFromHome() {
	    driver.get(test.getUrl());
	    logURL(driver.getCurrentUrl());
	}
	
	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Search Functionality")
	@Feature("Product Catalog")
	@Story("TC 1 - Search with valid and invalid product names")
	@Severity(SeverityLevel.BLOCKER)
	public void TC1() throws InterruptedException {
		productCatalog.searchValidation(test.getProduct());
		productCatalog.searchValidation(test.getKeyword());
	}

	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Add Quantity Functionality")
	@Feature("Product Catalog")
	@Story("TC 2 - Add quantity to product and validate cart contents")
	@Severity(SeverityLevel.BLOCKER)
	public void TC2() {
		productCatalog.addProductToCart(test.getProduct(), test.getQuantity());
		productCatalog.validateCartContents();
	}

	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Add Product Functionality")
	@Feature("Product Catalog")
	@Story("TC 3 - Add products and validate items total")
	@Severity(SeverityLevel.BLOCKER)
	public void TC3() {
		productCatalog.addProductsToCart(test.getProducts(), test.getQuantity());
		productCatalog.validateItemsTotal(test.getProducts());
	}

	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Add Product Functionality")
	@Feature("Product Catalog")
	@Story("TC 4 - Add products and validate total price")
	@Severity(SeverityLevel.BLOCKER)
	public void TC4() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.validatePriceInCart();
	}

	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Checkout Functionality")
	@Feature("Checkout Page")
	@Story("TC 5 - Validate products at checkout")
	@Severity(SeverityLevel.BLOCKER)
	public void TC5() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.clickCheckout();
		checkoutPage.validateProductsAtCheckout(test.getProducts());
	}

	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Total Amount Functionality")
	@Feature("Checkout Page")
	@Story("TC 6 - Validate Total Amount")
	@Severity(SeverityLevel.BLOCKER)
	public void TC6() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.clickCheckout();
		checkoutPage.validateTotalAmount();
	}

	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Promo Code Functionality")
	@Feature("Checkout Page")
	@Story("TC 7 - Validate empty and invalid promo code")
	@Severity(SeverityLevel.BLOCKER)
	public void TC7() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.clickCheckout();
		checkoutPage.validateEmptyCode();
		checkoutPage.validateInvalidCode();
		checkoutPage.validateAfterDiscount(test.getDiscountCode());
	}

	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Promo Code Functionality")
	@Feature("Checkout Page")
	@Story("TC 8 - Validate discount code")
	@Severity(SeverityLevel.BLOCKER)
	public void TC8() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.clickCheckout();
		checkoutPage.placeOrders();
		submissionPage.validateSelectedCountry(test.getCountry());
		submissionPage.validateErrorAlert();
		submissionPage.validateTerms();
		submissionPage.validateSubmitOrder();
	}
	
	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Search Functionality")
	@Feature("Top Deals")
	@Story("TC 13 - Search with valid and invalid product names")
	@Severity(SeverityLevel.BLOCKER)
	public void TC13() {
		productCatalog.clickTopDeals();
		e.switchTab("child");
		topDeals.searchValidation(test.getKeyword());
		topDeals.searchValidation(test.getProduct());
		e.closeTabContainingSlug("offers");
	}
	

	@Test(retryAnalyzer = Retry.class, enabled = false)
	@Epic("Page Size")
	@Feature("Top Deals")
	@Story("TC 14 - Validate Page Size Option")
	@Severity(SeverityLevel.BLOCKER)
	public void TC14() {
		productCatalog.clickTopDeals();
		e.switchTab("child");
		topDeals.validatePageSizeOption(test.getPageSize());
		e.closeTabContainingSlug("offers");
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Items Order")
	@Feature("Top Deals")
	@Story("TC 15 - Validate Ascending Order for column items")
	@Severity(SeverityLevel.BLOCKER)
	public void TC15() {
		productCatalog.clickTopDeals();
		e.switchTab("child");
		topDeals.clickColumnHeader("name", 1);
		topDeals.orderValidation(topDeals.tableContentList("name"), "sort");
		topDeals.clickColumnHeader("price", 1);
		topDeals.orderValidation(topDeals.tableContentList("price"), "sort");
		topDeals.clickColumnHeader("discount", 1);
		topDeals.orderValidation(topDeals.tableContentList("discount"), "sort");
		e.closeTabContainingSlug("offers");
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Items Order")
	@Feature("Top Deals")
	@Story("TC 16 - Validate Descending Order for column items")
	@Severity(SeverityLevel.BLOCKER)
	public void TC16() {
		productCatalog.clickTopDeals();
		e.switchTab("child");
		topDeals.clickColumnHeader("name", 2);
		topDeals.orderValidation(topDeals.tableContentList("name"), "reverse");
		topDeals.clickColumnHeader("price", 2);
		topDeals.orderValidation(topDeals.tableContentList("price"), "reverse");
		topDeals.clickColumnHeader("discount", 2);
		topDeals.orderValidation(topDeals.tableContentList("discount"), "reverse");
		e.closeTabContainingSlug("offers");
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Delivery Date Calendar")
	@Feature("Top Deals")
	@Story("TC 17 - Validate Delivery Date Calendar functionality")
	@Severity(SeverityLevel.BLOCKER)
	public void TC17() {
		productCatalog.clickTopDeals();
		e.switchTab("child");
		topDeals.validateDate(test.getMonthYear(), test.getDay());
		e.closeTabContainingSlug("offers");
	}
}