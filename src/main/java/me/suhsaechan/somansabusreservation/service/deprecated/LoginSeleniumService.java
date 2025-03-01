//package me.suhsaechan.somansabusreservation.service.deprecated;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.openqa.selenium.Alert;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Cookie;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.TimeoutException;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.Set;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class LoginSeleniumService {
//
//  private final SeleniumDriverService seleniumDriverService;
//
//  // 로그인 페이지 URL (예: https://cs.android.busin.co.kr/login.aspx)
//  @Value("${somansa.busin.login-page}")
//  private String loginPageUrl;
//
//  // 로그인 API URL은 여기서 직접 사용하지 않습니다. (폼 전송이 진행됨)
//  @Value("${somansa.busin.login-api}")
//  private String loginApiUrl;
//
//  // 로그인에 사용할 아이디 (예: chan4760@somasna.com)
//  @Value("${somansa.busin.login-id}")
//  private String loginId;
//
//  public void performLogin() {
//    WebDriver driver = seleniumDriverService.getDriver();
//    log.info("Navigating to login page: {}", loginPageUrl);
//    driver.get(loginPageUrl);
//
//    // 페이지가 완전히 로드될 때까지 대기 (document.readyState == "complete")
//    new WebDriverWait(driver, Duration.ofSeconds(20))
//        .until(webDriver -> ((JavascriptExecutor) webDriver)
//            .executeScript("return document.readyState").equals("complete"));
//    log.info("Page fully loaded.");
//
//    // 로그인 입력 필드(ID="LoginId")가 나타날 때까지 대기
//    new WebDriverWait(driver, Duration.ofSeconds(10))
//        .until(ExpectedConditions.presenceOfElementLocated(By.id("LoginId")));
//
//    // 초기 GET 요청 후 쿠키 확인
//    Set<Cookie> initialCookies = driver.manage().getCookies();
//    for (Cookie cookie : initialCookies) {
//      log.info("Initial Cookie: {} = {}", cookie.getName(), cookie.getValue());
//    }
//
//    // JavaScript 주입: dummy FCMPlugin 정의
//    JavascriptExecutor js = (JavascriptExecutor) driver;
//    js.executeScript(
//        "window.FCMPlugin = {" +
//            "   getToken: function(success, error) {" +
//            "       success('dummy_fcm_token_123456');" +
//            "   }" +
//            "};"
//    );
//    // JavaScript 주입: dummy openDatabase 정의
//    js.executeScript(
//        "if (typeof openDatabase === 'undefined') {" +
//            "   window.openDatabase = function(name, version, displayName, size) {" +
//            "       console.log('Dummy openDatabase called', name, version, displayName, size);" +
//            "       return {" +
//            "           transaction: function(callback, errorCallback, successCallback) {" +
//            "               console.log('Dummy transaction started');" +
//            "               var tx = {" +
//            "                   executeSql: function(sql, params, success, error) {" +
//            "                       console.log('Dummy executeSql called:', sql, params);" +
//            "                       if (typeof success === 'function') {" +
//            "                           success(tx, []);" +
//            "                       }" +
//            "                   }" +
//            "               };" +
//            "               callback(tx);" +
//            "               if (typeof successCallback === 'function') {" +
//            "                   successCallback();" +
//            "               }" +
//            "           }" +
//            "       };" +
//            "   };" +
//            "   console.log('Dummy openDatabase is defined.');" +
//            "}"
//    );
//    log.info("Injected dummy FCMPlugin and openDatabase.");
//
//    // dummy 함수들이 등록되었는지 확인 (재차 확인)
//    Boolean dummyReady = (Boolean) js.executeScript(
//        "return (typeof window.FCMPlugin !== 'undefined') && (typeof window.openDatabase !== 'undefined');"
//    );
//    log.info("Dummy functions available: {}", dummyReady);
//    if (!dummyReady) {
//      log.error("Dummy 함수 등록에 실패했습니다.");
//      throw new RuntimeException("Dummy functions not available");
//    }
//
//    // 주입 후 추가 대기 (예: 1초) - 스크립트 안정성 확보
//    try {
//      Thread.sleep(1000);
//    } catch (InterruptedException ie) {
//      Thread.currentThread().interrupt();
//      log.error("Sleep interrupted", ie);
//    }
//
//    // 로그인 입력 필드에 로그인 ID 입력
//    WebElement loginInput = driver.findElement(By.id("LoginId"));
//    loginInput.clear();
//    loginInput.sendKeys(loginId);
//    log.info("Entered login ID: {}", loginId);
//
//    // 로그인 버튼 클릭 (버튼 id가 "LinkOk1"로 가정)
//    WebElement loginButton = driver.findElement(By.id("LinkOk1"));
//    loginButton.click();
//    log.info("Login button clicked.");
//
//    // 클릭 후, 쿠키를 확인하여 세션 쿠키가 제대로 업데이트되었는지 확인 (디버깅용)
//    Set<Cookie> postCookies = driver.manage().getCookies();
//    for (Cookie cookie : postCookies) {
//      log.info("Post-click Cookie: {} = {}", cookie.getName(), cookie.getValue());
//    }
//
//    // 예상치 못한 alert (오류 메시지)가 있으면 처리
//    try {
//      WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(3));
//      Alert alert = alertWait.until(ExpectedConditions.alertIsPresent());
//      log.error("Unexpected alert present: " + alert.getText());
//      alert.dismiss();
//      log.info("Alert dismissed.");
//    } catch (TimeoutException te) {
//      log.info("No unexpected alert present.");
//    }
//
//    // 로그인 후 URL이 "/default.aspx"를 포함하는지 대기 (로그인 성공 시 이동)
//    try {
//      new WebDriverWait(driver, Duration.ofSeconds(10))
//          .until(ExpectedConditions.urlContains("/default.aspx"));
//      log.info("Login successful, current URL: {}", driver.getCurrentUrl());
//    } catch (TimeoutException te) {
//      log.error("Login did not complete within the expected time.");
//      throw te;
//    }
//  }
//}
