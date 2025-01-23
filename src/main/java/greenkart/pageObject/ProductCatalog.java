package greenkart.pageObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

public class ProductCatalog {

	WebDriver driver;
	
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
		List<WebElement> name = new ArrayList<>(productNames);

		for (int i = 0; i < name.size(); i++) {
			String names = name.get(i).getText();
			if (names.contains(productName)) {
				for (int j = 0; j < quantity - 1; j++) {
					addToCart().get(i).click();
				}
				break;
			}
		}
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
				j++;
			}
			if (j == products().length) {
				break;
			}
		}
	}
	
	public void validateItemsTotal() {
		int itemsTotal = Integer.parseInt(itemsCount.getText());
		int itemsExpected = products().length;
		Assert.assertEquals(itemsTotal, itemsExpected);
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
		Assert.assertEquals(sum, totalValue);
	}
	
	public void scrollTo(int index1, int index2) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(" + index1 + ", " + index2 + ");");
	}
	
	public CheckoutPage clickCheckout() {
		checkout.click();
		return new CheckoutPage(driver);
	}
}