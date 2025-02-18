package me.suhsaechan.somansabusreservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutSeleniumService {

  private final SeleniumDriverService seleniumDriverService;

  // 로그아웃 페이지 URL (예: https://cs.android.busin.co.kr/logout.aspx)
  @Value("${somansa.busin.url.logout-page:https://cs.android.busin.co.kr/logout.aspx}")
  private String logoutUrl;

  public void performLogout() {
    WebDriver driver = seleniumDriverService.getDriver();
    log.info("Navigating to logout page: {}", logoutUrl);
    driver.get(logoutUrl);
    // 로그아웃 페이지가 로딩될 때까지 대기 (예: URL이 logout.aspx를 포함할 때까지)
    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.urlContains("/logout.aspx"));
    log.info("Logout successful, current URL: {}", driver.getCurrentUrl());
  }
}
