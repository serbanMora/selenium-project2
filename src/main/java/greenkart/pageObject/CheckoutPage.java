package greenkart.pageObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class CheckoutPage {
	
	WebDriver driver;
	
	public CheckoutPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (css = "p[class='product-name']")
	private List<WebElement> checkoutNames;
	
	@FindBy (css = "span[class='totAmt']")
	private WebElement totalAmount;
	
	@FindBy (css = "tr td:nth-child(5)")
	private List<WebElement> totalColumn;
	
	@FindBy (className = "promoCode")
	private WebElement promoField;
	
	@FindBy (className = "promoBtn")
	private WebElement applyPromo;
	
	@FindBy (className = "promoInfo")
	private WebElement promoInfo;
	
	@FindBy (className = "discountPerc")
	private WebElement discountPercentage;
	
	@FindBy (className = "discountAmt")
	private WebElement discountAmount;
	
	@FindBy (xpath = "//button[text()='Place Order']")
	private WebElement placeOrder;
	
	public List<WebElement> checkoutNames() {
		return checkoutNames;
	}
	
	public void validateProductsAtCheckout() {
		Set<String> expectedProducts = new TreeSet<>(Arrays.asList(ProductCatalog.products()));
		List<WebElement> productList = new ArrayList<>(checkoutNames);
		
		List<String> copiedList = new ArrayList<>();
		
		for (int i = 0; i < productList.size(); i++) {
			
			String[] names = productList.get(i).getText().split("-");
			String formattedNames = names[0].trim();
			
			copiedList.add(formattedNames);
		}
		Collections.sort(copiedList);
		
		Assert.assertEquals(expectedProducts, copiedList);
	}
	
	public void validateTotalAmount() {
		int expectedTotal = Integer.parseInt(totalAmount.getText());
		int sum = 0;
		List<WebElement> row = new ArrayList<>(totalColumn);
		row.remove(0);
		for (int i = 0; i < row.size(); i++) {
			String data = row.get(i).getText();
			int amount = Integer.parseInt(data);
			sum = sum + amount;
		}
		Assert.assertEquals(expectedTotal, sum);
	}
	
	public void validateEmptyInvalidCode(String type) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		switch (type) {
		case "invalid":
			promoField.sendKeys("invalid");
			applyPromo.click();
			wait.until(ExpectedConditions.visibilityOf(promoInfo));
			Assert.assertEquals(promoInfo.getText(), "Invalid code ..!");
			break;
		
		case "empty":
			promoField.clear();
			applyPromo.click();
			wait.until(ExpectedConditions.visibilityOf(promoInfo));
			Assert.assertEquals(promoInfo.getText(), "Empty code ..!");
			break;
		}
	}
	
	public void validateAfterDiscount(String discountCode) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		driver.navigate().refresh();
		promoField.sendKeys(discountCode);
		applyPromo.click();
		wait.until(ExpectedConditions.visibilityOf(promoInfo));
		Assert.assertEquals(promoInfo.getText(), "Code applied ..!");

		int total = Integer.parseInt(totalAmount.getText());
		int discountPerc = Integer.parseInt(discountPercentage.getText().replace("%", ""));
		double totalAfterDisc = Double.parseDouble(discountAmount.getText());

		double percentage = (total * (discountPerc / 100d));
		double actualAfterDisc = total - percentage;

		Assert.assertEquals(totalAfterDisc, actualAfterDisc);
	}
	
	public OrderSubmissionPage placeOrders() {
		placeOrder.click();
		return new OrderSubmissionPage(driver);
	}
}