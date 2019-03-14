package ru.book24.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class TestMain {

  private WebDriver driver;
  private String baseUrl;
  private StringBuffer verificationErrors = new StringBuffer();

  @BeforeSuite
  public void setUp() throws Exception {
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    baseUrl = "https://book24.ru/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testBook24() throws Exception {
    driver.get(baseUrl);
    driver.findElement(By.linkText("Вход")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Email'])[1]/following::input[1]")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Email'])[1]/following::input[1]")).clear();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Email'])[1]/following::input[1]")).sendKeys("software.tester@outlook.com");
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Пароль'])[1]/following::input[1]")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Пароль'])[1]/following::input[1]")).clear();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Пароль'])[1]/following::input[1]")).sendKeys("161233");
    driver.findElement(By.xpath("//div[@id='login']/div/form/div/div[5]/button/div")).click();
    try {
      assertEquals(driver.findElement(By.cssSelector("span.personal-header__title-text")).getText(), "Мой кабинет");
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
  }

  @AfterSuite
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }
}
