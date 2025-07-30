package greenkart.pageObject;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class TopDeals {

	private static Logger log = LogManager.getLogger(TopDeals.class.getName());

	WebDriver driver;
	SoftAssert softAssert;

	public TopDeals(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "page-menu")
	private WebElement pageMenu;

	@FindBy(css = "tr td:nth-child(1)")
	private List<WebElement> namesList;

	@FindBy(css = "tr td:nth-child(2)")
	private List<WebElement> pricesList;

	@FindBy(css = "tr td:nth-child(3)")
	private List<WebElement> discountList;

	@FindBy(id = "search-field")
	private WebElement searchField;

	@FindBy(css = "tr span")
	private WebElement nameColumnHeader;

	@FindBy(css = "tr th:nth-child(2)")
	private WebElement priceColumnHeader;

	@FindBy(css = "tr th:nth-child(3)")
	private WebElement discountColumnHeader;

	@FindBy(css = "svg[class='react-date-picker__calendar-button__icon react-date-picker__button__icon']")
	private WebElement calendar;

	@FindBy(css = "input[name='date']")
	private WebElement calendarDate;

	@FindBy(css = "span[class='react-calendar__navigation__label__labelText react-calendar__navigation__label__labelText--from']")
	private WebElement calendarLabel;

	@FindBy(css = "button[class='react-calendar__navigation__arrow react-calendar__navigation__next-button']")
	private WebElement calendarNavigation;

	@FindBy(css = "div[class='react-calendar__month-view__days'] button[type='button']")
	private List<WebElement> calendarDays;

	public List<WebElement> tableContentList(String type) {
		try {
			switch (type) {
			case "name":
				return namesList;
			case "price":
				return pricesList;
			case "discount":
				return discountList;
			default:
				log.error("Invalid table content type: " + type);
				return null;
			}
		} catch (NoSuchElementException e) {
			log.error("Table content list not found for type: " + type, e);
			return null;
		}
	}

	public void clickColumnHeader(String type, int index) {
		try {
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
				default:
					log.error("Invalid column header type: " + type);
				}
			}
			log.info("Clicked column header: " + type + " " + index + " times");
		} catch (NoSuchElementException e) {
			log.error("Column header not found: " + type, e);
		}
	}

	public void switchTab(String window) {
		try {
			Set<String> handles = driver.getWindowHandles();
			Iterator<String> it = handles.iterator();
			String parentWindow = it.next();
			String childWindow = it.next();
			if (window.equals("child")) {
				driver.switchTo().window(childWindow);
				log.info("Switched to child window");
			} else if (window.equals("parent")) {
				driver.switchTo().window(parentWindow);
				log.info("Switched to parent window");
			} else {
				log.error("Invalid window type: " + window);
			}
		} catch (NoSuchElementException e) {
			log.error("Window handle not found", e);
		}
	}

	public void validatePageSizeOption(String value) {
		try {
			Select s = new Select(pageMenu);
			s.selectByValue(value);

			int selectedValue = Integer.parseInt(value);
			int namesSize = namesList.size();

			if (value.equals("20")) {
				Assert.assertTrue(selectedValue >= namesSize);
			} else {
				Assert.assertEquals(selectedValue, namesSize);
			}
			log.info("Validated page size option: " + value);
		} catch (AssertionError e) {
			log.error("Page size validation failed for value: " + value, e);
			throw e;
		} catch (NoSuchElementException e) {
			log.error("Page size option element not found for value: " + value, e);
		}
	}

	public void searchValidation(String keyword) {
		try {
			searchField.sendKeys(keyword);
			log.info("Entered search keyword: " + keyword);

			if (namesList.get(0).getText().equals("No data")) {
				Assert.assertTrue(true);
				log.info("No data found for keyword: " + keyword);
			} else {
				for (int i = 0; i < namesList.size(); i++) {
					String names = namesList.get(i).getText().toLowerCase();
					Assert.assertTrue(names.contains(keyword));
				}
				log.info("Search results validated for keyword: " + keyword);
			}
		} catch (AssertionError e) {
			log.error("Search validation failed for keyword: " + keyword, e);
			throw e;
		} catch (NoSuchElementException e) {
			log.error("Search field or results not found for keyword: " + keyword, e);
		} finally {
			searchField.clear();
			driver.navigate().refresh();
			log.info("Search field cleared and page refreshed");
		}
	}

	public void orderValidation(List<WebElement> list, String ordering) {
		try {
			List<String> originalList = new ArrayList<>();
			for (WebElement name : list) {
				originalList.add(name.getText());
			}

			List<String> copiedList = new ArrayList<>(originalList);

			if (ordering.equals("sort")) {
				Collections.sort(copiedList);
			} else if (ordering.equals("reverse")) {
				Collections.sort(copiedList, Collections.reverseOrder());
			} else {
				log.error("Invalid ordering type: " + ordering);
			}

			Assert.assertEquals(originalList, copiedList);
			log.info("Order validation passed for ordering: " + ordering);
		} catch (AssertionError e) {
			log.error("Order validation failed for ordering: " + ordering, e);
			throw e;
		} catch (NoSuchElementException e) {
			log.error("List element not found during order validation", e);
		}
	}

	public void validateDate(String monthYEAR, String day) {
		softAssert = new SoftAssert();
		try {
			String displayedDate = calendarDate.getDomAttribute("value");
			softAssert.assertEquals(displayedDate, getCurrentDate());
			log.info("Current date validated: " + displayedDate);

			calendar.click();
			while (!calendarLabel.getText().equals(monthYEAR)) {
				calendarNavigation.click();
			}
			log.info("Navigated to calendar month-year: " + monthYEAR);

			for (WebElement d : calendarDays) {
				if (d.getText().equals(day)) {
					d.click();
					break;
				}
			}
			String formattedDay = day.length() == 1 ? "0" + day : day;
			softAssert.assertEquals(calendarDate.getDomAttribute("value"), dateFormatConversion(monthYEAR) + "-" + formattedDay);
			log.info("Calendar date validated: " + monthYEAR + "-" + formattedDay);
		} catch (AssertionError e) {
			log.error("Calendar date validation failed for: " + monthYEAR + " " + day, e);
			throw e;
		} catch (NoSuchElementException e) {
			log.error("Calendar element not found", e);
		} finally {
			softAssert.assertAll();
		}
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