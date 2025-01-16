package greenkart.testCases;

import org.testng.annotations.Test;

import greenkart.config.BaseTest;
import greenkart.pageObject.CheckoutPage;
import greenkart.pageObject.ProductCatalog;

public class TestExecution extends BaseTest {

	ProductCatalog productCatalog;
	CheckoutPage checkoutPage;
	
	@Test
	public void TC1() {
		productCatalog = new ProductCatalog(driver);
		productCatalog.addQuantity("Walnuts", 15);
		productCatalog.addQuantity("Pears", 6);
		productCatalog.addQuantity("Banana", 9);
		productCatalog.scrollTo(0, 0);
		productCatalog.addProductToCart();
		productCatalog.validateItemsTotal();
		productCatalog.cartIcon().click();
		productCatalog.validatePriceInCart();
		productCatalog.clickCheckout();
		
		checkoutPage = new CheckoutPage(driver);
		checkoutPage.getName();
		
	}
}
