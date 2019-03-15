package ru.book24.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

  private String testUserLogin = "software.tester@outlook.com";
  private String testUserPassword = "161233";
  private String testUserName = "Тестовый Тест";

  @BeforeSuite
  public void setUp() throws Exception {
    // Для отключения показа уведомлений создаём экземпляр класса Chrome Options
    ChromeOptions options = new ChromeOptions();
    // Отключаем уведомления
    options.addArguments("--disable-notifications");
    // Передаём настройки браузеру
    driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    driver.manage().window().maximize();

    baseUrl = "https://book24.ru/";
    driver.get(baseUrl);

    /*
     * Подгтовка предусловия для теста, перености в отдельный метод
     */

    // Авторизоваться
    login(testUserLogin,testUserPassword);

    // Открыть корзину
    driver.findElement(By.className("header-cart-d")).click();
    // Если корзина не пустая, очистить корзину
    try {
      driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
      WebElement deleteAllInTheCart = driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Удалить всё'])"));
      deleteAllInTheCart.click();
      driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    } catch(NoSuchElementException | StaleElementReferenceException e) {
      System.out.println("Unable to empty the Cart. Reason: " + e.toString());
    }
    // Добавить два товара в корзину
    //driver.findElement(By.linkText("Перейти в каталог")).click();
    driver.get("https://book24.ru/catalog/");
    //driver.findElement(By.linkText("В корзину")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='+'])[1]/preceding::span[3]")).click();
    //driver.findElement(By.linkText("В корзину")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='+'])[2]/preceding::span[3]")).click();


    // Открыть корзину
    driver.findElement(By.className("header-cart-d")).click();
  }

  @Test
  public void testOrder() throws Exception {
    String count = driver.findElement(By.cssSelector("body > div.page__header.js-header > header > div.header-d__middle > div > div > div.header-d__personal > div.header-d__cart > div > a > div.header-cart-d__count.js-cart-top")).getText();
    System.out.println("Товаров в корзине: " + count);

    //step 1. Click оформить заказ
    driver.findElement(By.xpath("//*[normalize-space()='"+"Оформить заказ"+"']")).click();
    //Проверка что загружена первая страница оформления заказа Получатель
    try {
      assertEquals(driver.findElement(By.cssSelector("div.form-d__name")).getText(), "Получатель");
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
    //Проверка поля ФИО предзаполненое ФИО клиента
    try {
      assertEquals(driver.findElement(By.xpath("//input[@name='']")).getAttribute("value"), "Тестовый Тест");
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
    //Проверка поля Телефон предзаполнено телефоном клиента
    try {
      assertEquals(driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Телефон'])[1]/following::input[1]")).getAttribute("value"), "+7 (927) 785-80-55");
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }

    System.out.println("STEP2");

    // STEP 2. Нажать кнопку Далее. ОП: загрузилась вторая страница Доставка
    //driver.findElement(By.xpath("//div[contains(text(), \"Далее\")]")).click();
    //driver.findElement(By.xpath("//*[normalize-space()='"+"Далее"+"']")).click();
    driver.findElement(By.cssSelector("div.button__caption")).click();

    Thread.sleep(5000);
  }

  @AfterSuite
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  /*
  @AfterClass(alwaysRun = true)
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }
  */

  private void login(String userName, String userPassword){
    driver.findElement(By.linkText("Вход")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Email'])[1]/following::input[1]")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Email'])[1]/following::input[1]")).clear();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Email'])[1]/following::input[1]")).sendKeys(userName);
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Пароль'])[1]/following::input[1]")).click();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Пароль'])[1]/following::input[1]")).clear();
    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Пароль'])[1]/following::input[1]")).sendKeys(userPassword);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    driver.findElement(By.xpath("//div[@id='login']/div/form/div/div[5]/button/div")).click();
    try {
      assertEquals(driver.findElement(By.cssSelector("span.personal-header__title-text")).getText(), "Мой кабинет");
    } catch (Error e) {
      verificationErrors.append(e.toString());
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
