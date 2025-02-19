package greenkart.testCases;

import org.testng.annotations.Test;

import greenkart.config.BaseTest;
import greenkart.pageObject.CheckoutPage;
import greenkart.pageObject.OrderSubmissionPage;
import greenkart.pageObject.ProductCatalog;
import greenkart.pageObject.TopDeals;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;

public class TestExecution extends BaseTest {

	ProductCatalog productCatalog;
	CheckoutPage checkoutPage;
	OrderSubmissionPage submissionPage;
	TopDeals topDeals;
	
	@Test
	@Step("")
	@Description("")
	@Severity(SeverityLevel.MINOR)
	public void TC1() throws InterruptedException {
		productCatalog = new ProductCatalog(driver);
		productCatalog.searchValidation("apple");
		productCatalog.searchValidation("wrong-word");
	}
	
	@Test (dependsOnMethods = "TC1")
	@Description("")
	@Severity(SeverityLevel.NORMAL)
	public void TC2() {
		productCatalog.addQuantity("walnuts", 15);
		productCatalog.validateCartContents();
	}
	
	@Test (dependsOnMethods = "TC2")
	@Description("")
	@Severity(SeverityLevel.NORMAL)
	public void TC3() {
		productCatalog.addProductToCart();
		productCatalog.validateItemsTotal();
	}
	
	@Test (dependsOnMethods = "TC3")
	public void TC4() {
		productCatalog.cartIcon().click();
		productCatalog.validatePriceInCart();
		productCatalog.clickCheckout();
	}
	
	@Test (dependsOnMethods = "TC4")
	public void TC5() {
		checkoutPage = new CheckoutPage(driver);
		checkoutPage.validateProductsAtCheckout();
	}
	
	@Test (dependsOnMethods = "TC5")
	public void TC6() {
		checkoutPage.validateTotalAmount();
	}
	
	@Test (dependsOnMethods = "TC6")
	public void TC7() {
		checkoutPage.validateEmptyInvalidCode("invalid");
		checkoutPage.validateEmptyInvalidCode("empty");
	}
	
	@Test (dependsOnMethods = "TC7")
	public void TC8() {
		checkoutPage.validateAfterDiscount("rahulshettyacademy");
		checkoutPage.placeOrders();
	}
	
	@Test (dependsOnMethods = "TC8")
	public void TC9() {
		submissionPage = new OrderSubmissionPage(driver);
		submissionPage.validateSelectedCountry("Romania", "byValue");
	}
	
	@Test (dependsOnMethods = "TC9")
	public void TC10() {
		submissionPage.validateErrorAlert();
	}
	
	@Test (dependsOnMethods = "TC10")
	public void TC11() {
		submissionPage.validateTerms();
	}
	
	@Test (dependsOnMethods = "TC11")
	public void TC12() {
		submissionPage.validateSubmitOrder();
		productCatalog.clickTopDeals();
	}
	
	@Test (dependsOnMethods = "TC12")
	public void TC13() {
		topDeals = new TopDeals(driver);
		topDeals.switchTab("child");
		topDeals.searchValidation("wrong-word");
		topDeals.searchValidation("tomato");
	}
	
	@Test (dependsOnMethods = "TC13")
	public void TC14() {
		topDeals.validatePageSizeOption("5");
		topDeals.validatePageSizeOption("10");
		topDeals.validatePageSizeOption("20");
	}
	
	@Test (dependsOnMethods = "TC14")
	public void TC15() {
		topDeals.clickColumnHeader("name", 1);
		topDeals.orderValidation(topDeals.tableContentList("name"), "sort");
		topDeals.clickColumnHeader("price", 1);
		topDeals.orderValidation(topDeals.tableContentList("price"), "sort");
		topDeals.clickColumnHeader("discount", 1);
		topDeals.orderValidation(topDeals.tableContentList("discount"), "sort");
	}
	
	@Test (dependsOnMethods = "TC15")
	public void TC16() {
		topDeals.clickColumnHeader("name", 2);
		topDeals.orderValidation(topDeals.tableContentList("name"), "reverse");
		topDeals.clickColumnHeader("price", 2);
		topDeals.orderValidation(topDeals.tableContentList("price"), "reverse");
		topDeals.clickColumnHeader("discount", 2);
		topDeals.orderValidation(topDeals.tableContentList("discount"), "reverse");
	}
	
	@Test (dependsOnMethods = "TC16")
	public void TC17() {
		topDeals.validateDate("August 2030", "1");
	}
}