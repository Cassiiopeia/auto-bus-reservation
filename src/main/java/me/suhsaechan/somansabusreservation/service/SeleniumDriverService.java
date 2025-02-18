package me.suhsaechan.somansabusreservation.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class SeleniumDriverService {

  private WebDriver driver;

  public SeleniumDriverService() {
    // WebDriverManager를 사용하여 ChromeDriver 설정
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    //    options.addArguments("--headless=new");
    // iOS 환경과 유사한 User-Agent 설정
    options.addArguments("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) " +
        "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    // 필요하다면 headless 옵션을 끄거나 켤 수 있음
    // options.addArguments("--headless=new");

    driver = new ChromeDriver(options);
    // 암시적 대기: 요소를 찾을 때 최대 10초 대기
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    log.info("Selenium WebDriver initialized.");
  }

  public WebDriver getDriver() {
    return driver;
  }

  @PreDestroy
  public void cleanup() {
    if (driver != null) {
      driver.quit();
      log.info("Selenium WebDriver quit.");
    }
  }
}
