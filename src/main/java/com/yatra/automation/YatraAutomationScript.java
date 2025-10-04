package com.yatra.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class YatraAutomationScript {
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

        //Locators are generally handeled by BY class
        //By is the class in selenium and xpath is the static method that is why we can't create object of By class
        By departureDateButtonLocator = By.xpath("//div[@aria-label='Departure Date inputbox' and @role ='button']");


        // find elements return me web elements
        //not synchronized ---> WebElement departureDateButton = wd.findElement(departureDateButtonLocator);
        WebElement departureDateButton = wait.until(ExpectedConditions.elementToBeClickable(departureDateButtonLocator));
        departureDateButton.click();

        By calenderMonthsLocators = By.xpath("//div[@class='react-datepicker__month-container']");
        //List<WebElement> calenderMonthsWebElements = wd.findElements(calenderMonthsLocators); --- Not synchronized
        List<WebElement> calenderMonthsWebElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(calenderMonthsLocators)); // Synchronized


        // We want to focus on current month (October)
        WebElement octoberCalenderWebElement = calenderMonthsWebElements.get(1); // Current month
        Thread.sleep(2000);
        By priceLocator = By.xpath(".//span[contains(@class,'custom-day-content')]");
        List<WebElement> octoberPriceList = octoberCalenderWebElement.findElements(priceLocator);

        int lowestpriceValue = Integer.MAX_VALUE;
        WebElement priceElement = null;
        for(WebElement price : octoberPriceList) {
            //System.out.println(price.getText());
            String priceString = price.getText();
            if (priceString.length() > 0) {
                priceString = priceString.replace("₹", "").replace(",", "");
                //System.out.println(priceString);

                // Now need to find which is the smallest price
                // find the smallest number
                // convert the string into integer

                int priceInt = Integer.parseInt(priceString);
                if (priceInt < lowestpriceValue) {
                    lowestpriceValue = priceInt;
                    priceElement = price;
                }
            }
        }
        System.out.println("Lowest price in the month of october : " + lowestpriceValue);
        WebElement dateElement = priceElement.findElement(By.xpath(".//../.."));
        System.out.println(dateElement.getAttribute("aria-label"));

    }
}
