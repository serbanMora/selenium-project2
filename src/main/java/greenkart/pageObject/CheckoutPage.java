package greenkart.pageObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import greenkart.config.Asserts;
import greenkart.config.Waits;

public class CheckoutPage {

	private static Logger log = LogManager.getLogger(CheckoutPage.class.getName());
	
	private static final int SHORT_TIMEOUT = 5;
	private static final int MEDIUM_TIMEOUT = 10;

	WebDriver driver;
	Waits wait;
	Asserts a;
	
	public CheckoutPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(className = "product-name")
	private List<WebElement> checkoutNames;

	@FindBy(className = "totAmt")
	private WebElement totalAmount;

	@FindBy(css = "tr td:nth-child(5)")
	private List<WebElement> totalColumn;

	@FindBy(className = "promoCode")
	private WebElement promoField;

	@FindBy(className = "promoBtn")
	private WebElement applyPromo;

	@FindBy(className = "promoInfo")
	private WebElement promoInfo;

	@FindBy(className = "discountPerc")
	private WebElement discountPercentage;

	@FindBy(className = "discountAmt")
	private WebElement discountAmount;

	@FindBy(xpath = "//button[text()='Place Order']")
	private WebElement placeOrder;

	public void validateProductsAtCheckout() {
		wait = new Waits(driver);
		a = new Asserts(driver);
		
		wait.waitForVisibilityOfAll(MEDIUM_TIMEOUT, checkoutNames, "Product Names at Checkout");
		Set<String> expectedProducts = new TreeSet<>(Arrays.asList(ProductCatalog.products()));
		List<WebElement> productList = new ArrayList<>(checkoutNames);

		List<String> copiedList = new ArrayList<>();

		for (int i = 0; i < productList.size(); i++) {
			String[] names = productList.get(i).getText().split("-");
			String formattedName = names[0].trim();

			copiedList.add(formattedName);
			log.info("Product found at checkout: " + formattedName);
		}
		Collections.sort(copiedList);
		log.info("Actual products at checkout: " + copiedList);

		log.info("Checking if product names at checkout match expected product catalog");
		a.assertEquals(expectedProducts, new TreeSet<>(copiedList));
	}

	public void validateTotalAmount() {
		wait = new Waits(driver);
		a = new Asserts(driver);
		
		wait.waitForVisibilityOf(SHORT_TIMEOUT, totalAmount, "Total Amount");

		int expectedTotal = Integer.parseInt(totalAmount.getText());
		log.info("Expected total amount displayed: " + expectedTotal);

		int sum = 0;
		wait.waitForVisibilityOfAll(MEDIUM_TIMEOUT, totalColumn, "Total Column");
		List<WebElement> row = new ArrayList<>(totalColumn);
		row.remove(0);
		for (int i = 0; i < row.size(); i++) {
			String data = row.get(i).getText();
			int amount = Integer.parseInt(data);
			sum = sum + amount;
			log.info("Added item amount: " + amount + ", running sum: " + sum);
		}
		log.info("Checking if sum of individual items matches displayed total");
		a.assertEquals(expectedTotal, sum);
	}

	public void validateEmptyCode() {
		wait = new Waits(driver);
		a = new Asserts(driver);

		String emptyCodeMessage = "Empty code ..!";

		wait.waitForVisibilityOf(SHORT_TIMEOUT, promoField, "Promo Input Field");
		promoField.clear();
		wait.waitForVisibilityOf(SHORT_TIMEOUT, applyPromo, "Apply Promo Button");
		applyPromo.click();
		log.info("Clicked Apply Promo button");
		wait.fluentWait(2, MEDIUM_TIMEOUT, promoInfo, "Promo Info Message");
		log.info("Checking if promo code message: '" + emptyCodeMessage + "' is displayed in red color");
		a.assertEquals(promoInfo.getText(), emptyCodeMessage);
		a.assertTrue(promoInfo.getDomAttribute("style").contains("red"), emptyCodeMessage + " is displayed in red color", emptyCodeMessage + " is not displayed in red color");
	}
	
	public void validateInvalidCode() {
		wait = new Waits(driver);
		a = new Asserts(driver);

		String invalidCodeMessage = "Invalid code ..!";
		
		wait.waitForVisibilityOf(SHORT_TIMEOUT, promoField, "Promo Input Field");
		promoField.sendKeys("invalid");
		log.info("Entered invalid promo code");
		wait.waitForVisibilityOf(SHORT_TIMEOUT, applyPromo, "Apply Promo Button");
		applyPromo.click();
		log.info("Clicked Apply Promo button");
		wait.fluentWait(2, MEDIUM_TIMEOUT, promoInfo, "Promo Info Message");
		log.info("Checking if promo code message: '" + invalidCodeMessage + "' is displayed in red color");
		
		wait.waitForTextToBeChanged(MEDIUM_TIMEOUT, promoInfo, promoInfo.getText());
		a.assertEquals(promoInfo.getText(), invalidCodeMessage);
		a.assertTrue(promoInfo.getDomAttribute("style").contains("red"), invalidCodeMessage + " is displayed in red color", invalidCodeMessage + " is not displayed in red color");
	}
	
	public void validateAfterDiscount(String discountCode) {
		wait = new Waits(driver);
		a = new Asserts(driver);
		
		String appliedCodeMessage = "Code applied ..!";
		
		driver.navigate().refresh();
		log.info("Refreshed page for fresh discount test");
		
		wait.waitForVisibilityOf(SHORT_TIMEOUT, promoField, "Promo Input Field");
		promoField.sendKeys(discountCode);
		log.info("Entered discount code: " + discountCode);

		wait.waitForVisibilityOf(SHORT_TIMEOUT, applyPromo, "Apply Promo Button");
		applyPromo.click();
		log.info("Clicked Apply Promo button");

		wait.fluentWait(2, MEDIUM_TIMEOUT, promoInfo, "Promo Info Message");
		log.info("Checking if discount code message: '" + appliedCodeMessage + "' is displayed in green color");
		wait.waitForTextToBeChanged(MEDIUM_TIMEOUT, promoInfo, promoInfo.getText());
		a.assertEquals(promoInfo.getText(), appliedCodeMessage);
		a.assertTrue(promoInfo.getDomAttribute("style").contains("green"), appliedCodeMessage + " is displayed in green color", appliedCodeMessage + " is not displayed in green color");

		wait.waitForVisibilityOf(SHORT_TIMEOUT, totalAmount, "Total Amount");
		int total = Integer.parseInt(totalAmount.getText());
		
		wait.waitForVisibilityOf(SHORT_TIMEOUT, discountPercentage, "Discount Percentage");
		int discountPerc = Integer.parseInt(discountPercentage.getText().replace("%", ""));
		
		wait.waitForVisibilityOf(SHORT_TIMEOUT, discountAmount, "Discount Amount");
		double totalAfterDisc = Double.parseDouble(discountAmount.getText());

		log.debug("Original total: " + total);
		log.debug("Discount percentage: " + discountPerc + "%");
		log.debug("Displayed total after discount: " + totalAfterDisc);

		double expectedAfterDisc = total - (total * (discountPerc / 100d));
		log.debug("Expected total after discount calculation: " + expectedAfterDisc);

		a.assertEquals(totalAfterDisc, expectedAfterDisc);
	}

	public OrderSubmissionPage placeOrders() {
		wait = new Waits(driver);
		log.info("Clicking Place Order button");
		wait.waitForVisibilityOf(MEDIUM_TIMEOUT, placeOrder, "Place Order Button");
		placeOrder.click();
		return new OrderSubmissionPage(driver);
	}
}