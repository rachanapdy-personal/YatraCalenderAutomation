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

public class YatraAutomationScript1 {
    static void main() throws InterruptedException {

         // disbale notifications
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        // Launch a browser
        WebDriver wd = new ChromeDriver(chromeOptions);
        WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(20)); // Synchronizing the webdriver!
        // Load the page
        wd.get("https://www.yatra.com/");
        //maximize the browser window
        wd.manage().window().maximize();

        //check for the pop-up locator
        By popUpLocator = By.xpath("//div[contains(@class,'style_popup')][1]");
        try {
            WebElement popUpElement = wait.until(ExpectedConditions.visibilityOfElementLocated(popUpLocator));
            WebElement crossButton = popUpElement.findElement(By.xpath(".//img[@alt='cross']"));
            crossButton.click();
        } catch (TimeoutException e) {
            System.out.println("Pop-up not shown on the screen");
        }

        By departureDateButtonLocator = By.xpath("//div[@aria-label='Departure Date inputbox' and @role ='button']");
        WebElement departureDateButton = wait.until(ExpectedConditions.elementToBeClickable(departureDateButtonLocator));
        departureDateButton.click();

        WebElement currentMonthWebElement =  selectTheMonthFromCalender(wait,0); //current month
        WebElement nexttMonthWebElement = selectTheMonthFromCalender(wait,1); //next month

        Thread.sleep(2000);
        String lowerPriceForCurrentMonth =  getMeLowestPrice(currentMonthWebElement);
        String lowerPriceForNextMonth = getMeLowestPrice(nexttMonthWebElement);
        System.out.println(lowerPriceForCurrentMonth);
        System.out.println(lowerPriceForNextMonth);

        compareTwoMonthPrice(lowerPriceForCurrentMonth,lowerPriceForNextMonth);

    }

    public static String getMeLowestPrice(WebElement monthCalenderWebElement) {
        By priceLocator = By.xpath(".//span[contains(@class,'custom-day-content')]");
        List<WebElement> octoberPriceList = monthCalenderWebElement.findElements(priceLocator);

        int lowestpriceValue = Integer.MAX_VALUE;
        WebElement priceElement = null;
        for(WebElement price : octoberPriceList) {
            //System.out.println(price.getText());
            String priceString = price.getText();
            if (priceString.length() > 0) {
                priceString = priceString.replace("₹", "").replace(",", "");
                int priceInt = Integer.parseInt(priceString);
                if (priceInt < lowestpriceValue) {
                    lowestpriceValue = priceInt;
                    priceElement = price;
                }
            }
        }
        WebElement dateElement = priceElement.findElement(By.xpath(".//../.."));
        String result = dateElement.getAttribute("aria-label") + "---Price is Rs"  + lowestpriceValue ;
        return result;
    }

    public static WebElement selectTheMonthFromCalender(WebDriverWait wait , int index)
    {
        By calenderMonthsLocators = By.xpath("//div[@class='react-datepicker__month-container']");
        List<WebElement> calenderMonthsWebElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(calenderMonthsLocators)); // Synchronized
        // We want to focus on current month (October)
        WebElement monthCalenderWebElement = calenderMonthsWebElements.get(index); // Current month
        return monthCalenderWebElement;
    }

    public static void compareTwoMonthPrice(String currentMonthPrice , String nextMonthPrice)
    {
        int currentMonthRSIndex = currentMonthPrice.indexOf("Rs");
        int nextMonthRSIndex = nextMonthPrice.indexOf("Rs");


        String currentPrice = currentMonthPrice.substring(currentMonthRSIndex + 2);
        String nextPrice = nextMonthPrice.substring(nextMonthRSIndex + 2);

        int current = Integer.parseInt(currentPrice);
        int next = Integer.parseInt(nextPrice);

        if(current<next)
        {
            System.out.println("The lowest price for the two month is : " + current);
        } else if (current==next)
        {
            System.out.println("Price is same for both months ---> choose whatever suits you :) ");
        } else
        {
            System.out.println("The lowest price for the two month is : " + next);
        }
    }
}
