package greenkart.pageObject;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

public class TopDeals {

	WebDriver driver;
	
	public TopDeals(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (id = "page-menu")
	private WebElement pageMenu;
	
	@FindBy (css = "tr td:nth-child(1)")
	private List<WebElement> namesList;
	
	@FindBy (css = "tr td:nth-child(2)")
	private List<WebElement> pricesList;
	
	@FindBy (css = "tr td:nth-child(3)")
	private List<WebElement> discountList;
	
	@FindBy (id = "search-field")
	private WebElement searchField;
	
	public void switchTab(String window) {
		Set<String> handles = driver.getWindowHandles();
		Iterator<String> it = handles.iterator();
		String parentWindow = it.next();
		String childWindow = it.next();
		if (window.equals("child")) {
			driver.switchTo().window(childWindow);
		} else if (window.equals("parent")) {
			driver.switchTo().window(parentWindow);
		}
	}
	
	public void validatePageSizeOption(String value) {
		Select s = new Select(pageMenu);
		s.selectByValue(value);

		int selectedValue = Integer.parseInt(value);
		int namesSize = namesList.size();

		if (value.equals("20")) {
			Assert.assertTrue(selectedValue >= namesSize);
		} else {
			Assert.assertEquals(selectedValue, namesSize);
		}
	}
	
	public void searchValidation(String keyword) {
		searchField.sendKeys(keyword);

		if (namesList.get(0).getText().equals("No data")) {
			Assert.assertTrue(true);
		} else {
			for (int i = 0; i < namesList.size(); i++) {
				String names = namesList.get(i).getText().toLowerCase();
				Assert.assertTrue(names.contains(keyword));
			}
		}
		searchField.clear();
		driver.navigate().refresh();
	}
	
	public void nameOrderValidation() {
		
		
		
		
	}
	
	
	
}
