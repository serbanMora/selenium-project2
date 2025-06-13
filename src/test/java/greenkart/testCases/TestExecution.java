package greenkart.testCases;

import org.testng.annotations.Test;

import greenkart.config.BaseTest;
import greenkart.pageObject.CheckoutPage;
import greenkart.pageObject.OrderSubmissionPage;
import greenkart.pageObject.ProductCatalog;
import greenkart.pageObject.TopDeals;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

public class TestExecution extends BaseTest {
	
	private static final String URL = "https://rahulshettyacademy.com/seleniumPractise";

	ProductCatalog productCatalog;
	CheckoutPage checkoutPage;
	OrderSubmissionPage submissionPage;
	TopDeals topDeals;
	
	@Test (priority = 0)
	@Severity(SeverityLevel.CRITICAL)
	public void TestStart() {
		driver.get(URL);
		logURL(URL);
	}
	
	@Test (dependsOnMethods = "TestStart")
	@Epic("Search Functionality")
	@Feature("Product Catalog")
	@Story("TC 1 - Search with valid and invalid product names")
	@Severity(SeverityLevel.NORMAL)
	public void TC1() throws InterruptedException {
		productCatalog = new ProductCatalog(driver);
		productCatalog.searchValidation("apple");
		productCatalog.searchValidation("wrong-word");
	}
	
	@Test (dependsOnMethods = "TC1")
	@Epic("Add Quantity Functionality")
	@Feature("Product Catalog")
	@Story("TC 2 - Add quantity to product and validate cart contents")
	@Severity(SeverityLevel.NORMAL)
	public void TC2() {
		productCatalog.addQuantity("walnuts", 15);
		productCatalog.validateCartContents();
	}
	
	@Test (dependsOnMethods = "TC2")
	@Epic("Add Product Functionality")
	@Feature("Product Catalog")
	@Story("TC 3 - Add products and validate items total")
	@Severity(SeverityLevel.NORMAL)
	public void TC3() {
		productCatalog.addProductToCart();
		productCatalog.validateItemsTotal();
	}
	
	@Test (dependsOnMethods = "TC3")
	@Epic("Add Product Functionality")
	@Feature("Product Catalog")
	@Story("TC 4 - Add products and validate total price")
	@Severity(SeverityLevel.NORMAL)
	public void TC4() {
		productCatalog.cartIcon().click();
		productCatalog.validatePriceInCart();
		productCatalog.clickCheckout();
	}
	
	@Test (dependsOnMethods = "TC4")
	@Epic("Checkout Functionality")
	@Feature("Checkout Page")
	@Story("TC 5 - Validate products at checkout")
	@Severity(SeverityLevel.NORMAL)
	public void TC5() {
		checkoutPage = new CheckoutPage(driver);
		checkoutPage.validateProductsAtCheckout();
	}
	
	@Test (dependsOnMethods = "TC5")
	@Epic("Total Amount Functionality")
	@Feature("Checkout Page")
	@Story("TC 6 - Validate Total Amount")
	@Severity(SeverityLevel.CRITICAL)
	public void TC6() {
		checkoutPage.validateTotalAmount();
	}
	
	@Test (dependsOnMethods = "TC6")
	@Epic("Promo Code Functionality")
	@Feature("Checkout Page")
	@Story("TC 7 - Validate empty and invalid promo code")
	@Severity(SeverityLevel.NORMAL)
	public void TC7() {
		checkoutPage.validateEmptyInvalidCode("invalid");
		checkoutPage.validateEmptyInvalidCode("empty");
	}
	
	@Test (dependsOnMethods = "TC7")
	@Epic("Promo Code Functionality")
	@Feature("Checkout Page")
	@Story("TC 8 - Validate discount code")
	@Severity(SeverityLevel.BLOCKER)
	public void TC8() {
		checkoutPage.validateAfterDiscount("rahulshettyacademy");
		checkoutPage.placeOrders();
	}
	
	@Test (dependsOnMethods = "TC8")
	@Epic("Select Country Functionality")
	@Feature("Submission Page")
	@Story("TC 9 - Select country and validate selection")
	@Severity(SeverityLevel.MINOR)
	public void TC9() {
		submissionPage = new OrderSubmissionPage(driver);
		submissionPage.validateSelectedCountry("Romania", "byValue");
	}
	
	@Test (dependsOnMethods = "TC9")
	@Epic("Error Alert")
	@Feature("Submission Page")
	@Story("TC 10 - Validate T&C Required error alert")
	@Severity(SeverityLevel.MINOR)
	public void TC10() {
		submissionPage.validateErrorAlert();
	}
	
	@Test (dependsOnMethods = "TC10")
	@Epic("Terms & Conditions")
	@Feature("Submission Page")
	@Story("TC 11 - Validate Terms & Conditions in new tab")
	@Severity(SeverityLevel.NORMAL)
	public void TC11() {
		submissionPage.validateTerms();
	}
	
	@Test (dependsOnMethods = "TC11")
	@Epic("Submit Order")
	@Feature("Submission Page")
	@Story("TC 12 - Validate submission text")
	@Severity(SeverityLevel.CRITICAL)
	public void TC12() {
		submissionPage.validateSubmitOrder();
		productCatalog.clickTopDeals();
	}
	
	@Test (dependsOnMethods = "TC12")
	@Epic("Search Functionality")
	@Feature("Top Deals")
	@Story("TC 13 - Search with valid and invalid product names")
	@Severity(SeverityLevel.NORMAL)
	public void TC13() {
		topDeals = new TopDeals(driver);
		topDeals.switchTab("child");
		topDeals.searchValidation("wrong-word");
		topDeals.searchValidation("tomato");
	}
	
	@Test (dependsOnMethods = "TC13")
	@Epic("Page Size")
	@Feature("Top Deals")
	@Story("TC 14 - Validate Page Size Option")
	@Severity(SeverityLevel.NORMAL)
	public void TC14() {
		topDeals.validatePageSizeOption("5");
		topDeals.validatePageSizeOption("10");
		topDeals.validatePageSizeOption("20");
	}
	
	@Test (dependsOnMethods = "TC14")
	@Epic("Items Order")
	@Feature("Top Deals")
	@Story("TC 15 - Validate Ascending Order for column items")
	@Severity(SeverityLevel.MINOR)
	public void TC15() {
		topDeals.clickColumnHeader("name", 1);
		topDeals.orderValidation(topDeals.tableContentList("name"), "sort");
		topDeals.clickColumnHeader("price", 1);
		topDeals.orderValidation(topDeals.tableContentList("price"), "sort");
		topDeals.clickColumnHeader("discount", 1);
		topDeals.orderValidation(topDeals.tableContentList("discount"), "sort");
	}
	
	@Test (dependsOnMethods = "TC15")
	@Epic("Items Order")
	@Feature("Top Deals")
	@Story("TC 16 - Validate Descending Order for column items")
	@Severity(SeverityLevel.MINOR)
	public void TC16() {
		topDeals.clickColumnHeader("name", 2);
		topDeals.orderValidation(topDeals.tableContentList("name"), "reverse");
		topDeals.clickColumnHeader("price", 2);
		topDeals.orderValidation(topDeals.tableContentList("price"), "reverse");
		topDeals.clickColumnHeader("discount", 2);
		topDeals.orderValidation(topDeals.tableContentList("discount"), "reverse");
	}
	
	@Test (dependsOnMethods = "TC16")
	@Epic("Delivery Date Calendar")
	@Feature("Top Deals")
	@Story("TC 17 - Validate Delivery Date Calendar functionality")
	@Severity(SeverityLevel.TRIVIAL)
	public void TC17() {
		topDeals.validateDate("August 2030", "1");
	}
}