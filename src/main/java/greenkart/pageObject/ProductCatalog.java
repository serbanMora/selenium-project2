package greenkart.pageObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

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

	public ProductCatalog(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
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

	@FindBy(css = "button[type='button']")
	private List<WebElement> addToCart;

	@FindBy(className = "search-keyword")
	private WebElement searchField;

	@FindBy(className = "no-results")
	private WebElement noSearchResults;

	@FindBy(className = "product-price")
	private List<WebElement> productPrice;

	@FindBy(className = "quantity")
	private WebElement quantityInCart;

	@FindBy(className = "product-remove")
	private WebElement removeProduct;

	@FindBy(className = "empty-cart")
	private WebElement emptyCart;

	@FindBy(linkText = "Top Deals")
	private WebElement topDealsButton;

	public static String[] products() {
		return new String[] { "Brocolli", "Cauliflower", "Beetroot", "Cucumber", "Carrot", "Tomato", "Beans", "Brinjal",
				"Capsicum", "Mushroom", "Potato", "Pumpkin", "Corn", "Onion", "Apple", "Banana", "Grapes", "Mango",
				"Musk Melon", "Orange", "Pears", "Pomegranate", "Raspberry", "Strawberry", "Water Melon", "Almonds",
				"Pista", "Nuts Mixture", "Cashews", "Walnuts" };
	}

	public List<WebElement> addToCart() {
		List<WebElement> adds = new ArrayList<>(addToCart);
		adds.remove(0);
		return adds;
	}

	public WebElement cartIcon() {
		return cartIcon;
	}

	public void addQuantity(String productName, int quantity) {
		wait = new Waits(driver);
		e = new ElementActions(driver);
		
		e.scrollTo(0, 0);
		List<WebElement> name = new ArrayList<>(productNames);
		productName = productName.toLowerCase();
		for (int i = 0; i < name.size(); i++) {
			String names = name.get(i).getText().toLowerCase();
			if (names.contains(productName)) {
				for (int j = 0; j < quantity; j++) {
					addToCart().get(i).click();
				}
				break;
			}
		}
		log.info("For product: '" + productName + "', was added a quantity of " + quantity);
	}

	public void validateCartContents() {
		wait = new Waits(driver);
		a = new Asserts(driver);
		e = new ElementActions(driver);

		e.scrollTo(0, 0);
		wait.waitForVisibilityOf(SHORT_TIMEOUT, cartIcon, "Cart Icon");
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

	public void addProductToCart() {
		wait = new Waits(driver);
		
		List<String> productList = Arrays.asList(products());
		List<WebElement> addToCartButton = addToCart();

		int j = 0;

		wait.waitForVisibilityOfAll(MEDIUM_TIMEOUT, productNames, "Product Names");
		for (int i = 0; i < productNames.size(); i++) {
			String[] name = productNames.get(i).getText().split("-");
			String formattedName = name[0].trim();

			if (productList.contains(formattedName)) {

				addToCartButton.get(i).click();
				log.info("'" + formattedName + "' was added to cart");
				j++;

				if (j == products().length) {
					break;
				}
			}
		}
		log.info("Finished adding products to cart. Total added: " + j);
	}

	public void validateItemsTotal() {
		wait = new Waits(driver);
		a = new Asserts(driver);

		wait.waitForVisibilityOf(MEDIUM_TIMEOUT, itemsCount, "Items Count");
		int itemsTotal = Integer.parseInt(itemsCount.getText());
		int itemsExpected = products().length;

		log.info("Displayed item count matches expected item count");
		a.assertEquals(itemsTotal, itemsExpected);
	}

	public void validatePriceInCart() {
		wait = new Waits(driver);
		a = new Asserts(driver);

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

	public void searchValidation(String keyword, int timeout) throws InterruptedException {
		wait = new Waits(driver);
		a = new Asserts(driver);

		String expectedMessage = "Sorry, no products matched your search!\n" + "Enter a different keyword and try.";

		wait.waitForVisibilityOf(SHORT_TIMEOUT, searchField, "Search Field");
		searchField.sendKeys(keyword);
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
		wait = new Waits(driver);
		log.info("Clicking Checkout button");
		wait.waitForVisibilityOf(MEDIUM_TIMEOUT, checkout, "Checkout Button");
		checkout.click();
		return new CheckoutPage(driver);
	}

	public TopDeals clickTopDeals() {
		wait = new Waits(driver);
		log.info("Clicking Top Deals button");
		wait.waitForVisibilityOf(MEDIUM_TIMEOUT, topDealsButton, "Top Deals Button");
		topDealsButton.click();
		return new TopDeals(driver);
	}
}