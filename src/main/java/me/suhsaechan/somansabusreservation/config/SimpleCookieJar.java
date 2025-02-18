package me.suhsaechan.somansabusreservation.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SimpleCookieJar implements CookieJar {
  private final List<Cookie> cookieStore = new ArrayList<>();

  @Override
  public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
    cookieStore.addAll(cookies);
    for (Cookie cookie : cookies) {
      log.info("Cookie saved: {}={}, Domain: {}, Path: {}, ExpiresAt: {}",
          cookie.name(), cookie.value(), cookie.domain(), cookie.path(), cookie.expiresAt());
    }
  }

  @Override
  public List<Cookie> loadForRequest(HttpUrl url) {
    List<Cookie> validCookies = new ArrayList<>();
    for (Cookie cookie : cookieStore) {
      if (cookie.matches(url)) {
        validCookies.add(cookie);
      }
    }
    return validCookies;
  }

  // 디버깅 등 용도: 현재 저장된 쿠키 반환
  public List<Cookie> getCookies() {
    return new ArrayList<>(cookieStore);
  }
}
