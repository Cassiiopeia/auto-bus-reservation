//package me.suhsaechan.somansabusreservation.service.deprecated;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.openqa.selenium.WebDriver;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class HomeSeleniumService {
//
//  private final SeleniumDriverService seleniumDriverService;
//
//  public String fetchHomePageHtml() {
//    WebDriver driver = seleniumDriverService.getDriver();
//    String html = driver.getPageSource();
//    log.info("Fetched home page HTML, length: {}", html.length());
//    return html;
//  }
//}
