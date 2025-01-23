package greenkart.testCases;

import org.testng.annotations.Test;

import greenkart.config.BaseTest;
import greenkart.pageObject.CheckoutPage;
import greenkart.pageObject.OrderSubmissionPage;
import greenkart.pageObject.ProductCatalog;

public class TestExecution extends BaseTest {

	ProductCatalog productCatalog;
	CheckoutPage checkoutPage;
	OrderSubmissionPage submissionPage;
	
	@Test (alwaysRun = true, enabled = true)
	public void TC1() {
		productCatalog = new ProductCatalog(driver);
		productCatalog.addQuantity("Walnuts", 15);
		productCatalog.scrollTo(0, 0);
		productCatalog.addProductToCart();
		productCatalog.validateItemsTotal();
		productCatalog.cartIcon().click();
		productCatalog.validatePriceInCart();
		productCatalog.clickCheckout();
	}
	
	@Test (alwaysRun = true, dependsOnMethods = "TC1", enabled = true)
	public void TC2() {
		checkoutPage = new CheckoutPage(driver);
		explicitWaitList(checkoutPage.checkoutNames(), 5);
		checkoutPage.validateProductsAtCheckout();
		checkoutPage.validateTotalAmount();
		checkoutPage.validateAfterDiscount("rahulshettyacademy");
		checkoutPage.placeOrders();
	}
	
	@Test (alwaysRun = true, dependsOnMethods = "TC2", enabled = true)
	public void TC3() {
		submissionPage = new OrderSubmissionPage(driver);
		submissionPage.validateSelectedCountry("Romania", "byValue");
	}
}