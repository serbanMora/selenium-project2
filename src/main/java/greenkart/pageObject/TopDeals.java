package greenkart.pageObject;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
import org.testng.asserts.SoftAssert;

public class TopDeals {

	WebDriver driver;
	SoftAssert softAssert;
	
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
	
	@FindBy (css = "svg[class='react-date-picker__calendar-button__icon react-date-picker__button__icon']")
	private WebElement calendar;
	
	@FindBy (css = "input[name='date']")
	private WebElement calendarDate;
	
	@FindBy (css = "span[class='react-calendar__navigation__label__labelText react-calendar__navigation__label__labelText--from']")
	private WebElement calendarLabel;
	
	@FindBy (css = "button[class='react-calendar__navigation__arrow react-calendar__navigation__next-button']")
	private WebElement calendarNavigation;
	
	@FindBy (css = "div[class='react-calendar__month-view__days'] button[type='button']")
	private List<WebElement> calendarDays;
	
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
			Collections.sort(copiedList, Collections.reverseOrder());
		}
		Assert.assertEquals(originalList, copiedList);
	}
	
	public void validateDate(String monthYEAR, String day) {
		softAssert = new SoftAssert();
		String displayedDate = calendarDate.getDomAttribute("value");
		softAssert.assertEquals(displayedDate, getCurrentDate());
		
		calendar.click();
		while (!calendarLabel.getText().equals(monthYEAR)){
			calendarNavigation.click();
		} 

		for (int i = 0; i < calendarDays.size(); i++) {
			String days = calendarDays.get(i).getText();
			if (days.equals(day)) {
				calendarDays.get(i).click();
			}
		}
		String formattedDay = day.length() == 1 ? "0" + day : day;
		softAssert.assertEquals(calendarDate.getDomAttribute("value"), dateFormatConversion(monthYEAR) + "-" + formattedDay);
		softAssert.assertAll();
	}
	
	public String getCurrentDate() {
		LocalDate ld = LocalDate.now();
		String date = ld.toString();
		return date;	
	}
	
	public String dateFormatConversion(String monthYear) {
		DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        YearMonth yearMonth = YearMonth.parse(monthYear, monthYearFormatter);
        String formattedDate = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return formattedDate;
	}
}