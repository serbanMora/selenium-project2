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
		productCatalog.addQuantity("Walnuts", 10);
		productCatalog.addQuantity("Pears", 10);
		productCatalog.scrollTo(0, 0);
		productCatalog.addProductToCart();
		productCatalog.validateItemsTotal();
		productCatalog.cartIcon().click();
		productCatalog.validatePriceInCart();
		productCatalog.clickCheckout();
		
		checkoutPage = new CheckoutPage(driver);
		
		
	}
}
