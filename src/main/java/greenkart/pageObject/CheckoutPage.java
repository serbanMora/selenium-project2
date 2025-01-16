package greenkart.pageObject;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckoutPage {
	
	WebDriver driver;
	
	public CheckoutPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(css = "p[class='product-name']")
	private List<WebElement> checkoutNames;

	public void getName() {
		int total = 0;
		for (int i = 0; i < checkoutNames.size(); i++) {
			String[] names = checkoutNames.get(5).getText().split("-");
			String formattedName = names[0].trim();
			total = checkoutNames.size();
			System.out.println(formattedName);
		}
		System.out.println(total);

	}
	
	
	
	
	
	
	
}
