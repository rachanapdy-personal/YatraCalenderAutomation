package com.yatra.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class YatraAutomationScriptFinal {

    public static void main(String[] args) throws InterruptedException {

        //  Step 1: Set Chrome options to disable browser notifications
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");

        //  Step 2: Launch Chrome browser with configured options
        WebDriver driver = new ChromeDriver(chromeOptions);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        //  Step 3: Open Yatra website and maximize window
        driver.get("https://www.yatra.com/");
        driver.manage().window().maximize();

        //  Step 4: Handle any pop-up if it appears
        By popUpLocator = By.xpath("//div[contains(@class,'style_popup')][1]");
        try {
            WebElement popUpElement = wait.until(ExpectedConditions.visibilityOfElementLocated(popUpLocator));
            WebElement crossButton = popUpElement.findElement(By.xpath(".//img[@alt='cross']"));
            crossButton.click();
        } catch (TimeoutException e) {
            System.out.println("Pop-up not shown on the screen");
        }

        //  Step 5: Click the departure date field to open the calendar
        By departureDateButtonLocator = By.xpath("//div[@aria-label='Departure Date inputbox' and @role='button']");
        WebElement departureDateButton = wait.until(ExpectedConditions.elementToBeClickable(departureDateButtonLocator));
        departureDateButton.click();

        //  Step 6: Get calendar elements for current and next month
        WebElement currentMonth = selectMonthFromCalendar(wait, 0);
        WebElement nextMonth = selectMonthFromCalendar(wait, 1);

        Thread.sleep(2000); // temporary wait just for stability

        //  Step 7: Extract lowest prices from both months
        String lowestCurrentMonth = getLowestPrice(currentMonth);
        String lowestNextMonth = getLowestPrice(nextMonth);

        System.out.println("Current Month: " + lowestCurrentMonth);
        System.out.println("Next Month: " + lowestNextMonth);

        //  Step 8: Compare and print cheaper month
        compareTwoMonthPrice(lowestCurrentMonth, lowestNextMonth);

        //  Step 9: Close browser
        driver.quit();
    }

    //  Extract lowest price from the given month's calendar
    public static String getLowestPrice(WebElement monthCalendar) {
        By priceLocator = By.xpath(".//span[contains(@class,'custom-day-content')]");
        List<WebElement> priceElements = monthCalendar.findElements(priceLocator);

        int lowestPriceValue = Integer.MAX_VALUE;
        WebElement lowestPriceElement = null;

        for (WebElement price : priceElements) {
            String priceText = price.getText().replace("₹", "").replace(",", "").trim();
            if (!priceText.isEmpty()) {
                int value = Integer.parseInt(priceText);
                if (value < lowestPriceValue) {
                    lowestPriceValue = value;
                    lowestPriceElement = price;
                }
            }
        }

        if (lowestPriceElement != null) {
            WebElement dateElement = lowestPriceElement.findElement(By.xpath(".//../.."));
            String dateInfo = dateElement.getAttribute("aria-label");
            return dateInfo + " --- Price is Rs " + lowestPriceValue;
        } else {
            return "No price found for this month.";
        }
    }

    //  Select month by index (0 = current month, 1 = next month)
    public static WebElement selectMonthFromCalendar(WebDriverWait wait, int index) {
        By monthsLocator = By.xpath("//div[@class='react-datepicker__month-container']");
        List<WebElement> monthElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(monthsLocator));
        return monthElements.get(index);
    }

    //  Compare lowest prices between two months
    public static void compareTwoMonthPrice(String currentMonthPrice, String nextMonthPrice) {
        int current = Integer.parseInt(currentMonthPrice.substring(currentMonthPrice.indexOf("Rs") + 2).trim());
        int next = Integer.parseInt(nextMonthPrice.substring(nextMonthPrice.indexOf("Rs") + 2).trim());

        if (current < next) {
            System.out.println("✅ The lowest price between the two months is Rs " + current);
        } else if (current == next) {
            System.out.println("⚖️ Prices are the same for both months. Choose any suitable date.");
        } else {
            System.out.println("✅ The lowest price between the two months is Rs " + next);
        }
    }
}
