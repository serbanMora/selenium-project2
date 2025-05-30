package greenkart.pageObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class ProductCatalog {
	
	private static Logger log = LogManager.getLogger(ProductCatalog.class.getName());

	WebDriver driver;
	WebDriverWait wait;
	SoftAssert softAssert;
	
	public ProductCatalog(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (css = "h4[class='product-name']")
	private List<WebElement> productNames;
	
	@FindBy (css = "tr td:nth-child(3)")
	private WebElement itemsCount;
	
	@FindBy (css = "p[class='amount']")
	private List<WebElement> pricesFromCart;
	
	@FindBy (css = "tr:nth-child(2) td:nth-child(3)")
	private WebElement totalPrice;
	
	@FindBy (css = "a[class='cart-icon']")
	private WebElement cartIcon;
	
	@FindBy (xpath = "//p[@class='product-name']")
	private List<WebElement> productNamesFromCart;
	
	@FindBy (xpath = "//button[text()='PROCEED TO CHECKOUT']")
	private WebElement checkout;
	
	@FindBy (css = "button[type='button']")
	private List<WebElement> addToCart;
	
	@FindBy (className = "search-keyword")
	private WebElement searchField;
	
	@FindBy (className = "no-results")
	private WebElement noSearchResults;
	
	@FindBy (className = "product-price")
	private List<WebElement> productPrice;
	
	@FindBy (className = "quantity")
	private WebElement quantityInCart;
	
	@FindBy (className = "product-remove")
	private WebElement removeProduct;
	
	@FindBy (className = "empty-cart")
	private WebElement emptyCart;
	
	@FindBy (linkText = "Top Deals")
	private WebElement topDealsButton;
	
	public static String[] products() {
		return new String[] {"Brocolli", "Cauliflower", "Beetroot", "Cucumber", "Carrot", "Tomato", "Beans",
				"Brinjal", "Capsicum", "Mushroom", "Potato", "Pumpkin", "Corn", "Onion", "Apple", "Banana", "Grapes", "Mango", "Musk Melon",
				"Orange", "Pears", "Pomegranate", "Raspberry", "Strawberry", "Water Melon", "Almonds", "Pista", "Nuts Mixture", "Cashews", "Walnuts"};
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
		scrollTo(0, 0);
		waitForVisibilityOfAll(3, productNames);
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
		softAssert = new SoftAssert();
		scrollTo(0, 0);
		cartIcon.click();

		int actualProductPrice = Integer.parseInt(productPrice.get(0).getText());

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
		String logMessage = "Price of the product: " + actualProductPrice + ", multiplied with the quantity of product: " + actualQuantity + ", equals the amount: " + actualAmount;

		try {
			Assert.assertTrue(actualProductPrice * actualQuantity == actualAmount);
			log.info(logMessage);
		} catch (AssertionError e) {
			log.error(logMessage);
			throw e;
		}
		removeProduct.click();
		softAssert.assertEquals(emptyCart.getText(), "You cart is empty!");
		softAssert.assertTrue(checkout.getDomAttribute("class").equals("disabled"));
		softAssert.assertAll();
		cartIcon.click();
	}

	public void addProductToCart() {
		List<String> productList = Arrays.asList(products());
		List<WebElement> addToCartButton = addToCart();

		int j = 0;

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
	}
	
	public void validateItemsTotal() {
		try {
			int itemsTotal = Integer.parseInt(itemsCount.getText());
			int itemsExpected = products().length;

			Assert.assertEquals(itemsTotal, itemsExpected);
			log.info("Displayed item count: " + itemsTotal + ". Expected item count: " + itemsExpected);
		} catch (AssertionError e) {
			log.error("Mismatch in item count. Displayed: " + itemsCount.getText() + ", Expected: " + products().length);
			throw e;
		}
	}
	
	public void validatePriceInCart() {
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
		try {
			Assert.assertEquals(sum, totalValue);
			log.info("Displayed total price is: " + totalValue + ". Sum of individual prices: " + sum);
		} catch (AssertionError e) {
			log.error("Total price mismatch. Displayed: " + totalValue + ", Calculated: " + sum);
			throw e;
		}
	}
	
	public void searchValidation(String keyword) throws InterruptedException {
		searchField.sendKeys(keyword);
		Thread.sleep(4000);
		
		if (productNames.isEmpty()) {
			String expectedMessage = "Sorry, no products matched your search!\n" + "Enter a different keyword and try.";
			try {
				Assert.assertEquals(noSearchResults.getText(), expectedMessage);
	        	log.info("No products found. Message displayed: '" + noSearchResults.getText() + "'");
			} catch (AssertionError e) {
				log.error("Incorrect message if no products are found: '"  + noSearchResults.getText() + "'. Expected: " + expectedMessage);
			}
		} else {
			waitForVisibilityOfAll(5, productNames);
			List<WebElement> products = productNames;
			for (int i = 0; i < products.size(); i++) {
				String[] name = products.get(i).getText().split("-");
				String formattedName = name[0].trim().toLowerCase();
				try {
					Assert.assertEquals(formattedName, keyword);
	            	log.info("Searched for product: " + keyword + ", found: '" + formattedName + "'");
				} catch (AssertionError e) {
					log.error("Mismatch on searched product: " + keyword + " , found: '" + formattedName + "'");
					throw e;
				}
			}
		}
		searchField.clear();
		driver.navigate().refresh();
	}
	
	public void scrollTo(int index1, int index2) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(" + index1 + ", " + index2 + ");");
	}
	
	public void waitForVisibilityOfAll(int duration, List<WebElement> element) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
		wait.until(ExpectedConditions.visibilityOfAllElements(element));
	}
	
	public CheckoutPage clickCheckout() {
		checkout.click();
		return new CheckoutPage(driver);
	}
	
	public TopDeals clickTopDeals() {
		topDealsButton.click();
		return new TopDeals(driver);
	}
}