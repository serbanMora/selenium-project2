package greenkart.pageObject;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import greenkart.config.Asserts;
import greenkart.config.ElementActions;
import greenkart.config.Waits;

public class ProductCatalog {

	private static Logger log = LogManager.getLogger(ProductCatalog.class.getName());
	
	private static final int SHORT_TIMEOUT = 5;
	private static final int MEDIUM_TIMEOUT = 10;

	WebDriver driver;
	ElementActions e;
	Waits wait;
	Asserts a;
	Actions actions;

	public ProductCatalog(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);

		wait = new Waits(driver);
		e = new ElementActions(driver);
		a = new Asserts(driver);
		actions = new Actions(driver);
	}

	@FindBy(css = "h4[class='product-name']")
	private List<WebElement> productNames;

	@FindBy(css = "tr td:nth-child(3)")
	private WebElement itemsCount;

	@FindBy(css = "p[class='amount']")
	private List<WebElement> pricesFromCart;

	@FindBy(css = "tr:nth-child(2) td:nth-child(3)")
	private WebElement totalPrice;

	@FindBy(css = "a[class='cart-icon']")
	private WebElement cartIcon;

	@FindBy(xpath = "//button[text()='PROCEED TO CHECKOUT']")
	private WebElement checkout;

	@FindBy(xpath = "//button[normalize-space()='ADD TO CART' or contains(normalize-space(), 'ADDED')]")
	private List<WebElement> addToCart;

	@FindBy(className = "search-keyword")
	private WebElement searchField;

	@FindBy(className = "no-results")
	private WebElement noSearchResults;

	@FindBy(className = "product-price")
	private List<WebElement> productPrice;

	@FindBy(xpath = "//input[@type='number']")
	private List<WebElement> quantity;

	@FindBy(css = "p[class='quantity']")
	private WebElement quantityInCart;
	
	@FindBy(className = "product-remove")
	private WebElement removeProduct;

	@FindBy(className = "empty-cart")
	private WebElement emptyCart;

	@FindBy(linkText = "Top Deals")
	private WebElement topDealsButton;

	public void clickCartIcon() {
		String name = "Cart Icon Button";
		wait.waitForVisibilityOf(SHORT_TIMEOUT, cartIcon, name);
		log.info("Clicking the " + name);
		cartIcon.click();
	}

	public void addProductToCart(String productName, String quantityValue) {
		boolean isFound = false;
		productName = productName.toLowerCase();
		wait.waitForVisibilityOfAll(SHORT_TIMEOUT, productNames, "Product Names");
		for (int i = 0; i < productNames.size(); i++) {
			String[] name = productNames.get(i).getText().split("-");
			String formattedName = name[0].trim().toLowerCase();
			if (formattedName.contains(productName)) {
				isFound = true;
				e.scrollIntoView(addToCart.get(i));
				addQuantity(i, quantityValue);
				addToCart.get(i).click();
				log.info("Added " + productName + " to cart");
				break;
			}
		}
		if (!isFound) {
			String error = productName + " was not found - stopping test execution";
			log.error(error);
			Assert.fail(error);
		}
	}
	
	public void addProductsToCart(String[] productNames, String quantityValue) {
		for (String productName : productNames) {
			addProductToCart(productName, quantityValue);
		}
	}
	
	public void addQuantity(int index, String quantityValue) {
		wait.waitForVisibilityOf(SHORT_TIMEOUT, quantity.get(index), "Quantity input field at index: " + index);
		actions.moveToElement(quantity.get(index)).keyDown(Keys.LEFT_CONTROL).sendKeys("A").build().perform();;
		log.info("Adding quantity: " + quantityValue);
		quantity.get(index).sendKeys(quantityValue);
	}

	public void validateCartContents() {
		wait.waitForVisibilityOf(SHORT_TIMEOUT, cartIcon, "Cart Icon");
		e.scrollIntoView(cartIcon);
		log.info("Clicking cart icon to view cart contents");
		cartIcon.click();

		int actualProductPrice = Integer.parseInt(productPrice.get(0).getText());

		wait.waitForVisibilityOf(SHORT_TIMEOUT, quantityInCart, "Quantity In Cart");
		String[] name = quantityInCart.getText().split(" ");
		String formattedName = name[0].trim();
		int actualQuantity = Integer.parseInt(formattedName);

		int actualAmount = 0;
		for (int i = 0; i < pricesFromCart.size(); i++) {
			String amount = pricesFromCart.get(i).getText();
			if (!amount.isEmpty()) {
				actualAmount = Integer.parseInt(amount);
			}
		}
		log.info("Checking if the price of the product: " + actualProductPrice
				+ ", multiplied with the quantity of product: " + actualQuantity + ", equals the amount: "
				+ actualAmount);
		a.assertTrue(actualProductPrice * actualQuantity == actualAmount, "");

		wait.waitForVisibilityOf(SHORT_TIMEOUT, removeProduct, "Remove Product");
		removeProduct.click();
		log.info("Removed product from cart");

		log.info("Checking if cart is empty");
		wait.waitForVisibilityOf(SHORT_TIMEOUT, emptyCart, "Empty Cart");
		a.assertEquals(emptyCart.getText(), "You cart is empty!");
		
		wait.waitForVisibilityOf(SHORT_TIMEOUT, checkout, "Checkout");
		a.assertTrue(checkout.getDomAttribute("class").equals("disabled"), "");
		
		wait.waitForVisibilityOf(SHORT_TIMEOUT, cartIcon, "Cart Icon");
		log.info("Clicking cart icon button");
		cartIcon.click();
	}

	public void validateItemsTotal(String[] products) {
		wait.waitForVisibilityOf(MEDIUM_TIMEOUT, itemsCount, "Items Count");
		int itemsTotal = Integer.parseInt(itemsCount.getText());
		int itemsExpected = products.length;

		log.info("Displayed item count matches expected item count");
		a.assertEquals(itemsTotal, itemsExpected);
	}

	public void validatePriceInCart() {
		log.info("Validating sum of individual product prices matches displayed total");
		List<WebElement> prices = pricesFromCart;
		int sum = 0;
		for (int i = 0; i < prices.size(); i++) {
			String amount = prices.get(i).getText();
			if (!amount.isEmpty()) {
				int value = Integer.parseInt(amount);
				sum += value;
			}
		}
		int totalValue = Integer.parseInt(totalPrice.getText());
		log.debug("Sum of individual product prices: " + sum);
		log.info("Checking if displayed total price matches the sum of individual prices");
		a.assertEquals(sum, totalValue);
	}

	public void searchValidation(String keyword) throws InterruptedException {
		String expectedMessage = "Sorry, no products matched your search!\n" + "Enter a different keyword and try.";
		wait.waitForVisibilityOf(SHORT_TIMEOUT, searchField, "Search Field");
		searchField.sendKeys(keyword);
		int timeout = SHORT_TIMEOUT * 1000;
		log.info("Entered search keyword: '" + keyword + "'. Waiting " + timeout + "ms");
		Thread.sleep(timeout);

		if (productNames.isEmpty()) {
			log.info("No products found, checking if not products found message is displayed");
			wait.waitForVisibilityOf(SHORT_TIMEOUT, noSearchResults, "No Search Results");
			a.assertEquals(noSearchResults.getText(), expectedMessage);
		} else {
			wait.waitForVisibilityOfAll(SHORT_TIMEOUT, productNames, "Product Names");
			List<WebElement> products = productNames;
			for (int i = 0; i < products.size(); i++) {
				String[] name = products.get(i).getText().split("-");
				String formattedName = name[0].trim().toLowerCase();
				log.info("Searching for product: " + keyword + ", checking if matching product is found");
				a.assertEquals(formattedName, keyword);

			}
		}
		wait.waitForVisibilityOf(SHORT_TIMEOUT, searchField, "Search Field");
		searchField.clear();
		log.info("Clearing search field and refreshing page");
		driver.navigate().refresh();
		log.info("Refreshed page");
	}

	public CheckoutPage clickCheckout() {
		String name = "Checkout Button";
		wait.waitForVisibilityOf(MEDIUM_TIMEOUT, checkout, name);
		log.info("Clicking the " + name);
		checkout.click();
		return new CheckoutPage(driver);
	}

	public TopDeals clickTopDeals() {
		String name = "Top Deals Button";
		wait.waitForVisibilityOf(MEDIUM_TIMEOUT, topDealsButton, name);
		log.info("Clicking the " + name);
		topDealsButton.click();
		return new TopDeals(driver);
	}
}