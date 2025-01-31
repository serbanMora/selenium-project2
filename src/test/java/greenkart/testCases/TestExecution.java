package greenkart.testCases;

import org.testng.annotations.Test;

import greenkart.config.BaseTest;
import greenkart.pageObject.CheckoutPage;
import greenkart.pageObject.OrderSubmissionPage;
import greenkart.pageObject.ProductCatalog;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;

public class TestExecution extends BaseTest {

	ProductCatalog productCatalog;
	CheckoutPage checkoutPage;
	OrderSubmissionPage submissionPage;
	
	@Test
	@Step("")
	@Description("")
	@Severity(SeverityLevel.MINOR)
	public void TC1() {
		productCatalog = new ProductCatalog(driver);
		productCatalog.searchValidation("banana");
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
	}
}