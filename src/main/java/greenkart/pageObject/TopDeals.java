package greenkart.pageObject;

import java.util.ArrayList;
import java.util.Collections;
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
	
	@FindBy (css = "tr span")
	private WebElement nameColumnHeader;
	
	@FindBy (css = "tr th:nth-child(2)")
	private WebElement priceColumnHeader;
	
	@FindBy (css = "tr th:nth-child(3)")
	private WebElement discountColumnHeader;
	
	public List<WebElement> tableContentList(String type) {
		if (type.equals("name")) {
			return namesList;
		}
		if (type.equals("price")) {
			return pricesList;
		}
		if (type.equals("discount")) {
			return discountList;
		}
		return null;
	}
	
	public void clickColumnHeader(String type, int index) {
		for (int i = 0; i < index; i++) {
			switch (type) {
			case "name":
				nameColumnHeader.click();
				break;
			case "price":
				priceColumnHeader.click();
				break;
			case "discount":
				discountColumnHeader.click();
				break;
			}
		}
	}
	
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
	
	public void orderValidation(List<WebElement> list, String ordering) {
		List<WebElement> names = new ArrayList<>(list);
		List<String> originalList = new ArrayList<>();
		for (int i = 0; i < names.size(); i++) {
			originalList.add(names.get(i).getText());
		}
		List<String> copiedList = new ArrayList<>();
		for (int i = 0; i < originalList.size(); i++) {
			copiedList.add(originalList.get(i));
		}
		if (ordering.equals("sort")) {
			Collections.sort(copiedList);
		} else if (ordering.equals("reverse")) {
			Collections.reverse(copiedList);
		}
		Assert.assertEquals(originalList, copiedList);
	}
}
