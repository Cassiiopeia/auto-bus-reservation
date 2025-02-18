package me.suhsaechan.somansabusreservation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SomansaBusInService {

  // 로그인 API URL (예: https://cs.android.busin.co.kr/Login.aspx/LoginCheck)
  @Value("${somansa.busin.login-api}")
  private String busInLoginUrl;

  // OkHttpClient에 CookieJar를 등록하여 쿠키를 자동 저장/전달하도록 설정
  private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
      .cookieJar(new SimpleCookieJar())
      .build();

  /**
   * 자동 로그인 메서드
   * 1. 로그인에 사용할 계정과 더미 FCM 토큰을 JSON 페이로드에 포함
   * 2. POST 요청을 통해 로그인 API 호출
   * 3. 응답 헤더와 쿠키, 응답 본문을 상세하게 로깅
   */
  public void autoLogin() {
    // 로그인 데이터 구성
    String loginId = "jeong_heejeong@somansa.com";
    String fcmToken = "dummy_fcm_token_123456";
    String payloadData = String.format("{ \"data\": \"%s,%s\" }", loginId, fcmToken);
    log.debug("Login payload: {}", payloadData);

    // 요청 바디 생성 (application/json)
    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(payloadData, mediaType);

    // POST 요청 빌드 (Content-Type 헤더 추가)
    Request request = new Request.Builder()
        .url(busInLoginUrl)
        .post(body)
        .addHeader("Content-Type", "application/json; charset=UTF-8")
        .build();

    log.info("Sending login request to URL: {}", busInLoginUrl);

    // 4. 요청 실행 및 응답 처리
    try (Response response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("Login request failed with code: {}", response.code());
        return;
      }

      // 4-1. 응답 헤더 로깅 (쿠키 등)
      Headers headers = response.headers();
      log.debug("Response Headers:");
      for (String headerName : headers.names()) {
        log.debug("{}: {}", headerName, headers.get(headerName));
      }

      // 4-2. 현재 CookieJar에 저장된 쿠키 확인 및 로깅
      List<Cookie> cookies = ((SimpleCookieJar) okHttpClient.cookieJar()).getCookies();
      if (cookies.isEmpty()) {
        log.warn("No cookies saved in CookieJar.");
      } else {
        log.info("Cookies saved in CookieJar:");
        for (Cookie cookie : cookies) {
          log.info("Cookie: {}={}, Domain: {}, Path: {}, ExpiresAt: {}",
              cookie.name(), cookie.value(), cookie.domain(), cookie.path(), cookie.expiresAt());
        }
      }

      // 4-3. 응답 본문 읽기 및 로깅
      String responseBody = response.body().string();
      log.info("Login response body: {}", responseBody);

      // 4-4. JSON 파싱 후 'd' 값 로깅
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(responseBody);
      JsonNode dNode = rootNode.path("d");
      if (dNode.isMissingNode()) {
        log.error("Response does not contain 'd' field.");
      } else {
        int dValue = dNode.asInt();
        log.info("Login response 'd' value: {}", dValue);
        if (dValue > 0) {
          log.info("로그인 성공! 사용자 ID: {}", dValue);
          // 이후 예약 API 호출 시 이 세션 쿠키를 함께 사용하면 됩니다.
        } else {
          log.warn("로그인 실패, 'd' 값: {}", dValue);
        }
      }
    } catch (IOException e) {
      log.error("Exception during login request", e);
    }
  }

  /**
   * OkHttp의 CookieJar 구현체 (간단한 In-Memory 쿠키 저장소)
   */
  private static class SimpleCookieJar implements CookieJar {
    private final List<Cookie> cookieStore = new ArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
      cookieStore.addAll(cookies);
      for (Cookie cookie : cookies) {
        // 쿠키가 저장될 때마다 로깅
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

    // 현재 저장된 모든 쿠키를 반환 (로깅 용도)
    public List<Cookie> getCookies() {
      return new ArrayList<>(cookieStore);
    }
  }
}
