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

	@BeforeClass(alwaysRun = true)
	public void TestStart() {
		initPages();
	}

	@BeforeMethod(alwaysRun = true)
	public void startFromHome() {
		driver.get(test.getUrl());
		logURL(driver.getCurrentUrl());
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Search Functionality")
	@Feature("Product Catalog")
	@Story("TC1 - Search with valid and invalid product names")
	@Severity(SeverityLevel.NORMAL)
	public void searchWithValidAndInvalidProductNames() throws InterruptedException {
		productCatalog.searchValidation(test.getProduct());
		productCatalog.searchValidation(test.getKeyword());
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Add Quantity Functionality")
	@Feature("Product Catalog")
	@Story("TC 2 - Add quantity to product and validate cart contents")
	@Severity(SeverityLevel.NORMAL)
	public void addProductWithQuantityAndValidateCart() {
		productCatalog.addProductToCart(test.getProduct(), test.getQuantity());
		productCatalog.validateCartContents();
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Add Product Functionality")
	@Feature("Product Catalog")
	@Story("TC 3 - Add products and validate items total")
	@Severity(SeverityLevel.NORMAL)
	public void addMultipleProductsAndValidateItemsTotal() {
		productCatalog.addProductsToCart(test.getProducts(), test.getQuantity());
		productCatalog.validateItemsTotal(test.getProducts());
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Add Product Functionality")
	@Feature("Product Catalog")
	@Story("TC 4 - Add products and validate total price")
	@Severity(SeverityLevel.NORMAL)
	public void addProductsAndValidateTotalPriceInCart() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.validatePriceInCart();
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Checkout Functionality")
	@Feature("Checkout Page")
	@Story("TC 5 - Validate products at checkout")
	@Severity(SeverityLevel.NORMAL)
	public void validateProductsAtCheckout() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.clickCheckout();
		checkoutPage.validateProductsAtCheckout(test.getProducts());
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Total Amount Functionality")
	@Feature("Checkout Page")
	@Story("TC 6 - Validate total amount at checkout")
	@Severity(SeverityLevel.CRITICAL)
	public void validateTotalAmountAtCheckout() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.clickCheckout();
		checkoutPage.validateTotalAmount();
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Promo Code Functionality")
	@Feature("Checkout Page")
	@Story("TC 7 - Validate promo code scenarios at checkout")
	@Severity(SeverityLevel.BLOCKER)
	public void validatePromoCodeScenariosAtCheckout() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.clickCheckout();
		checkoutPage.validateEmptyCode();
		checkoutPage.validateInvalidCode();
		checkoutPage.validateAfterDiscount(test.getDiscountCode());
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Submit Order")
	@Feature("Submission Page")
	@Story("TC 8 - Complete order placement and submission validation")
	@Severity(SeverityLevel.BLOCKER)
	public void placeOrderAndValidateSubmissionPage() {
		productCatalog.addProductsToCart(test.getProducts(), "1");
		productCatalog.clickCartIcon();
		productCatalog.clickCheckout();
		checkoutPage.placeOrders();
		submissionPage.validateSelectedCountry(test.getCountry());
		submissionPage.validateErrorAlert();
		submissionPage.validateTerms();
		submissionPage.validateSubmitOrder();
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Search Functionality")
	@Feature("Top Deals")
	@Story("TC 9 - Search with valid and invalid product names")
	@Severity(SeverityLevel.NORMAL)
	public void searchTopDealsWithValidAndInvalidProducts() {
		productCatalog.clickTopDeals();
		e.switchTab("child");
		topDeals.searchValidation(test.getKeyword());
		topDeals.searchValidation(test.getProduct());
		e.closeTabContainingSlug("offers");
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Page Size")
	@Feature("Top Deals")
	@Story("TC 10 - Validate page size option")
	@Severity(SeverityLevel.NORMAL)
	public void validatePageSizeOptionInTopDeals() {
		productCatalog.clickTopDeals();
		e.switchTab("child");
		topDeals.validatePageSizeOption(test.getPageSize());
		e.closeTabContainingSlug("offers");
	}

	@Test(retryAnalyzer = Retry.class, enabled = true)
	@Epic("Items Order")
	@Feature("Top Deals")
	@Story("TC 11 - Validate Ascending Order for column items")
	@Severity(SeverityLevel.MINOR)
	public void validateAscendingOrderForTopDealsColumns() {
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
	@Story("TC 12 - Validate Descending Order for column items")
	@Severity(SeverityLevel.MINOR)
	public void validateDescendingOrderForTopDealsColumns() {
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
	@Story("TC 13 - Validate delivery date calendar functionality")
	@Severity(SeverityLevel.TRIVIAL)
	public void validateDeliveryDateCalendarInTopDeals() {
		productCatalog.clickTopDeals();
		e.switchTab("child");
		topDeals.validateDate(test.getMonthYear(), test.getDay());
		e.closeTabContainingSlug("offers");
	}
}