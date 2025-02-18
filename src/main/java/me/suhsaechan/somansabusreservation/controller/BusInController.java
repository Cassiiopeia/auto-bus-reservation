package me.suhsaechan.somansabusreservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.suhsaechan.somansabusreservation.service.HomeOkHttpService;
import me.suhsaechan.somansabusreservation.service.HomeSeleniumService;
import me.suhsaechan.somansabusreservation.service.LoginOkhttpService;
import me.suhsaechan.somansabusreservation.service.LoginSeleniumService;
import me.suhsaechan.somansabusreservation.service.LogoutOkHttpService;
import me.suhsaechan.somansabusreservation.service.LogoutSeleniumService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BusInController {

  private final LoginOkhttpService loginOkhttpService;
  private final HomeOkHttpService homeOkHttpService;
  private final LogoutOkHttpService logoutOkHttpService;

  private final LoginSeleniumService loginSeleniumService;
  private final HomeSeleniumService homeSeleniumService;
  private final LogoutSeleniumService logoutSeleniumService;

  @GetMapping("/api/test/okhttp")
  public String testFlow() {
    log.info("로그인 시도");
    int userId = loginOkhttpService.login();
    if (userId <= 0) {
      return "로그인 실패";
    }
    log.info("로그인 완료, 사용자 ID: {}", userId);

    log.info("홈페이지 진입 시도");
    String homeHtml = homeOkHttpService.fetchHomePage();
    if (homeHtml == null) {
      return "홈페이지 진입 실패";
    }
    log.info("홈페이지 진입 완료, HTML 길이: {}", homeHtml.length());

    log.info("로그아웃 시도");
    boolean logoutSuccess = logoutOkHttpService.logout();
    log.info("로그아웃 완료: {}", logoutSuccess);

    return logoutSuccess ? "로그아웃 성공" : "로그아웃 실패";
  }


  @GetMapping("/api/test/selenium")
  public String seleniumTestFlow() {
    log.info("로그인 시도");
    try {
      loginSeleniumService.performLogin();
    } catch (Exception e) {
      log.error("로그인 실패", e);
      return "로그인 실패: " + e.getMessage();
    }
    log.info("로그인 완료");

    log.info("홈페이지 진입 시도");
    String homeHtml = homeSeleniumService.fetchHomePageHtml();
    log.info("홈페이지 진입 완료, HTML 길이: {}", homeHtml != null ? homeHtml.length() : 0);

    log.info("로그아웃 시도");
    try {
      logoutSeleniumService.performLogout();
    } catch (Exception e) {
      log.error("로그아웃 실패", e);
      return "로그아웃 실패: " + e.getMessage();
    }
    log.info("로그아웃 완료");

    return "전체 플로우 완료 - 홈페이지 HTML 길이: " + (homeHtml != null ? homeHtml.length() : "null");
  }
}
