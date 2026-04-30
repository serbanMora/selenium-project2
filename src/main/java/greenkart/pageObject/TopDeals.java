package greenkart.pageObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import greenkart.config.Asserts;
import greenkart.config.ElementActions;
import greenkart.config.Waits;

public class TopDeals {

	private static Logger log = LogManager.getLogger(TopDeals.class.getName());
	
	private static final int SHORT_TIMEOUT = 5;
	private static final int MEDIUM_TIMEOUT = 10;

	WebDriver driver;
	ElementActions e;
	Waits wait;
	Asserts a;

	public TopDeals(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		
		wait = new Waits(driver);
		e = new ElementActions(driver);
		a = new Asserts(driver);
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
		switch (type) {
		case "name":
			wait.waitForVisibilityOfAll(MEDIUM_TIMEOUT, namesList, "Names List");
			return namesList;
		case "price":
			wait.waitForVisibilityOfAll(MEDIUM_TIMEOUT, pricesList, "Prices List");
			return pricesList;
		case "discount":
			wait.waitForVisibilityOfAll(MEDIUM_TIMEOUT, discountList, "Discount List");
			return discountList;
		default:
			log.error("Invalid table content type: " + type);
			return null;
		}
	}

	public void clickColumnHeader(String type, int index) {
		for (int i = 0; i < index; i++) {
			switch (type) {
			case "name":
				wait.waitForVisibilityOf(MEDIUM_TIMEOUT, nameColumnHeader, "Column Header");
				nameColumnHeader.click();
				break;
			case "price":
				wait.waitForVisibilityOf(MEDIUM_TIMEOUT, priceColumnHeader, "Price Colummn Header");
				priceColumnHeader.click();
				break;
			case "discount":
				wait.waitForVisibilityOf(MEDIUM_TIMEOUT, discountColumnHeader, "Discount Column Header");
				discountColumnHeader.click();
				break;
			default:
				log.error("Invalid column header type: " + type);
			}
		}
		log.info("Clicked column header: " + type + " " + index + " times");
	}

	public void validatePageSizeOption(String value) {
		wait.waitForVisibilityOf(SHORT_TIMEOUT, pageMenu, "Page Menu");
		e.selectBy("value", pageMenu, value);

		int selectedValue = Integer.parseInt(value);
		wait.waitForVisibilityOfAll(MEDIUM_TIMEOUT, namesList, "Names List");
		int namesSize = namesList.size();

		log.info("Validating page size option: " + value);
		if (value.equals("20")) {
			a.assertTrue(selectedValue >= namesSize, "");
		} else {
			a.assertEquals(selectedValue, namesSize);
		}
	}

	public void searchValidation(String keyword) {
		wait.waitForVisibilityOf(SHORT_TIMEOUT, searchField, "Search Field");
		searchField.sendKeys(keyword);
		log.info("Entered search keyword: " + keyword);

		wait.waitForVisibilityOfAll(MEDIUM_TIMEOUT, namesList, "Names List");
		if (namesList.get(0).getText().equals("No data")) {
			log.info("No data found for keyword: " + keyword);
		} else {
			log.info("Validating search results for keyword: " + keyword);
			for (int i = 0; i < namesList.size(); i++) {
				String names = namesList.get(i).getText().toLowerCase();
				a.assertTrue(names.contains(keyword), "");
			}
		}
		wait.waitForVisibilityOf(SHORT_TIMEOUT, searchField, "Search Field");
		searchField.clear();
		driver.navigate().refresh();
		log.info("Search field cleared and page refreshed");
	}

	public void orderValidation(List<WebElement> list, String ordering) {
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
		a.assertEquals(originalList, copiedList);
	}

	public void validateDate(String monthYEAR, String day) {
		String displayedDate = calendarDate.getDomAttribute("value");
		log.info("Validating current date: " + displayedDate);
		a.assertEquals(displayedDate, e.getCurrentDate());

		wait.waitForVisibilityOf(SHORT_TIMEOUT, calendar, "Calendar");
		calendar.click();
		while (!calendarLabel.getText().equals(monthYEAR)) {
			calendarNavigation.click();
		}
		log.info("Navigated to calendar month-year: " + monthYEAR);

		wait.waitForVisibilityOfAll(SHORT_TIMEOUT, calendarDays, "Calendar Days");
		for (WebElement d : calendarDays) {
			if (d.getText().equals(day)) {
				d.click();
				break;
			}
		}
		String formattedDay = day.length() == 1 ? "0" + day : day;
		log.info("Validating calendar date: " + monthYEAR + "-" + formattedDay);
		a.assertEquals(calendarDate.getDomAttribute("value"), e.dateFormatConversion(monthYEAR) + "-" + formattedDay);
	}
}