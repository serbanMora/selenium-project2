package greenkart.pageObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class CheckoutPage {

	private static Logger log = LogManager.getLogger(CheckoutPage.class.getName());

	WebDriver driver;
	Wait<WebDriver> waits;
	WebDriverWait wait;
	SoftAssert softAssert;

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
		try {
			waitForVisibilityOfAll(15, checkoutNames);

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

			Assert.assertEquals(expectedProducts, new TreeSet<>(copiedList));
			log.info("Product names at checkout match expected product catalog");
		} catch (AssertionError e) {
			log.error("Assertion failed while validating products at checkout: " + e.getMessage());
			throw e;
		} catch (NoSuchElementException e) {
			log.error("Element not found during checkout validation: " + e.getMessage());
			throw e;
		}
	}

	public void validateTotalAmount() {
		try {
			waitForVisibilityOf(5, totalAmount);
			waitForVisibilityOfAll(15, totalColumn);

			int expectedTotal = Integer.parseInt(totalAmount.getText());
			log.info("Expected total amount displayed: " + expectedTotal);

			int sum = 0;
			List<WebElement> row = new ArrayList<>(totalColumn);
			row.remove(0);
			for (int i = 0; i < row.size(); i++) {
				String data = row.get(i).getText();
				int amount = Integer.parseInt(data);
				sum = sum + amount;
				log.info("Added item amount: " + amount + ", running sum: " + sum);
			}
			Assert.assertEquals(expectedTotal, sum);
			log.info("Verified total amount: sum of individual items matches displayed total");

		} catch (AssertionError e) {
			log.error("Assertion failed while validating total amount: " + e.getMessage());
			throw e;
		} catch (NoSuchElementException e) {
			log.error("Element not found during total amount validation: " + e.getMessage());
			throw e;
		}
	}

	public void validateEmptyInvalidCode(String type) {
		softAssert = new SoftAssert();
		String invalidCodeMessage = "Invalid code ..!";
		String emptyCodeMessage = "Empty code ..!";

		try {
			switch (type) {
			case "invalid":
				promoField.sendKeys("invalid");
				log.info("Entered invalid promo code");
				applyPromo.click();
				log.info("Clicked Apply Promo button");
				fluentWait(10, 2, promoInfo);
				softAssert.assertEquals(promoInfo.getText(), invalidCodeMessage);
				softAssert.assertTrue(promoInfo.getDomAttribute("style").contains("red"));
				log.info("Invalid promo code message: '" + invalidCodeMessage + "' is displayed in red color");
				break;

			case "empty":
				promoField.clear();
				applyPromo.click();
				log.info("Clicked Apply Promo button");
				fluentWait(10, 2, promoInfo);
				softAssert.assertEquals(promoInfo.getText(), emptyCodeMessage);
				softAssert.assertTrue(promoInfo.getDomAttribute("style").contains("red"));
				log.info("Empty promo code message: '" + emptyCodeMessage + "' is displayed in red color");
				break;
			}
			softAssert.assertAll();
		} catch (AssertionError e) {
			log.error("Assertion failed while validating promo code (" + type + "): " + e.getMessage());
			throw e;
		} catch (NoSuchElementException e) {
			log.error("Element not found during promo code validation (" + type + "): " + e.getMessage());
			throw e;
		}
	}

	public void validateAfterDiscount(String discountCode) {
		softAssert = new SoftAssert();

		try {
			driver.navigate().refresh();
			log.info("Refreshed page for fresh discount test");

			promoField.sendKeys(discountCode);
			log.info("Entered discount code: " + discountCode);

			applyPromo.click();
			log.info("Clicked Apply Promo button");

			fluentWait(10, 2, promoInfo);
			softAssert.assertEquals(promoInfo.getText(), "Code applied ..!");
			softAssert.assertTrue(promoInfo.getDomAttribute("style").contains("green"));
			log.info("Discount code applied successfully message displayed");

			int total = Integer.parseInt(totalAmount.getText());
			int discountPerc = Integer.parseInt(discountPercentage.getText().replace("%", ""));
			double totalAfterDisc = Double.parseDouble(discountAmount.getText());

			log.debug("Original total: " + total);
			log.debug("Discount percentage: " + discountPerc + "%");
			log.debug("Displayed total after discount: " + totalAfterDisc);

			double expectedAfterDisc = total - (total * (discountPerc / 100d));
			log.debug("Expected total after discount calculation: " + expectedAfterDisc);

			softAssert.assertEquals(totalAfterDisc, expectedAfterDisc);
			log.info("Total after discount verified successfully");

			softAssert.assertAll();

		} catch (AssertionError e) {
			log.error("Assertion failed while validating discount: " + e.getMessage());
			throw e;
		} catch (NoSuchElementException e) {
			log.error("Element not found during discount validation: " + e.getMessage());
			throw e;
		}
	}

	public void fluentWait(int duration, int polling, WebElement element) {
		waits = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(duration))
				.pollingEvery(Duration.ofSeconds(polling)).ignoring(NoSuchElementException.class);

		waits.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				if (element.isDisplayed()) {
					return element;
				} else {
					return null;
				}
			}
		});
	}

	public void waitForVisibilityOf(int duration, WebElement element) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForVisibilityOfAll(int duration, List<WebElement> element) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
		wait.until(ExpectedConditions.visibilityOfAllElements(element));
	}

	public OrderSubmissionPage placeOrders() {
		log.info("Clicking Place Order button");
		placeOrder.click();
		return new OrderSubmissionPage(driver);
	}
}