package me.suhsaechan.somansabusreservation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.suhsaechan.somansabusreservation.config.CommonHttpClient;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginOkhttpService {

  // 로그인 페이지 URL (초기 세션 생성용)
  @Value("${somansa.busin.url.login-page:https://cs.android.busin.co.kr/login.aspx}")
  private String loginPageUrl;

  // 로그인 API URL (예: https://cs.android.busin.co.kr/Login.aspx/LoginCheck)
  @Value("${somansa.busin.url.login-api:https://cs.android.busin.co.kr/Login.aspx/LoginCheck}")
  private String loginUrl;

  // 테스트 계정 (예: chan4760@somasna.com)
  @Value("${somansa.busin.login-id:chan4760@somasna.com}")
  private String loginId;

  // 더미 FCM 토큰 (웹 테스트용)
  private static final String DUMMY_FCM_TOKEN = "dummy_fcm_token_123456";

  // 공통 OkHttpClient 사용 (CookieJar 포함)
  private final OkHttpClient client = CommonHttpClient.getClient();

  /**
   * 초기 GET 요청으로 로그인 페이지를 불러와 세션 쿠키를 획득한 후,
   * POST 요청으로 로그인 API를 호출하여 사용자 ID(응답의 'd' 값)를 반환합니다.
   */
  public int login() {
    // 1. 초기 GET 요청으로 로그인 페이지 로드 (세션 쿠키 획득)
    Request getRequest = new Request.Builder()
        .url(loginPageUrl)
        .get()
        // 브라우저와 유사한 User-Agent 헤더 추가
        .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
        .build();
    try (Response getResponse = client.newCall(getRequest).execute()) {
      if (!getResponse.isSuccessful()) {
        log.error("초기 로그인 페이지 GET 요청 실패, 코드: {}", getResponse.code());
        return -1;
      }
      log.info("초기 로그인 페이지 GET 요청 성공, 쿠키 획득됨");
      // GET 응답 본문은 사용하지 않습니다.
    } catch (IOException e) {
      log.error("초기 로그인 페이지 GET 요청 중 예외 발생", e);
      return -1;
    }

    // 2. POST 요청을 통해 로그인 API 호출
    String payloadData = String.format("{ \"data\": \"%s,%s\" }", loginId, DUMMY_FCM_TOKEN);
    log.debug("Login payload: {}", payloadData);

    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(payloadData, mediaType);

    Request postRequest = new Request.Builder()
        .url(loginUrl)
        .post(body)
        // 브라우저와 유사한 헤더 추가
        .addHeader("Content-Type", "application/json; charset=UTF-8")
        .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
        .addHeader("Referer", loginPageUrl)
        .build();

    log.info("Sending login request to URL: {}", loginUrl);

    try (Response response = client.newCall(postRequest).execute()) {
      if (!response.isSuccessful()) {
        log.error("Login request failed with code: {}", response.code());
        // 오류 응답 시 응답 헤더 및 본문도 기록
        Headers errorHeaders = response.headers();
        for (String headerName : errorHeaders.names()) {
          log.error("Error Header {}: {}", headerName, errorHeaders.get(headerName));
        }
        String errorBody = response.body() != null ? response.body().string() : "No body";
        log.error("Error response body: {}", errorBody);
        return -1;
      }

      // 정상 응답의 헤더 로깅
      Headers headers = response.headers();
      for (String headerName : headers.names()) {
        log.debug("Header {}: {}", headerName, headers.get(headerName));
      }

      String responseBody = response.body().string();
      log.info("Login response body: {}", responseBody);

      // JSON 파싱 후 'd' 값 확인
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(responseBody);
      int dValue = rootNode.path("d").asInt();
      log.info("Login response 'd' value: {}", dValue);
      if (dValue > 0) {
        log.info("로그인 성공! 사용자 ID: {}", dValue);
      } else {
        log.warn("로그인 실패, 'd' 값: {}", dValue);
      }
      return dValue;
    } catch (IOException e) {
      log.error("Exception during login request", e);
      return -1;
    }
  }
}
