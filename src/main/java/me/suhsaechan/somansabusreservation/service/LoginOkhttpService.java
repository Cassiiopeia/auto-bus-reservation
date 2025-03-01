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

  @Value("${somansa.busin.url.login-page:https://cs.android.busin.co.kr/Login.aspx?device=}")
  private String loginPageUrl;

  @Value("${somansa.busin.url.login-api:https://cs.android.busin.co.kr/Login.aspx/LoginCheck}")
  private String loginUrl;

  @Value("${somansa.busin.login-id:chan4760@somansa.com}")
  private String loginId;

  private static final String PUSH_ID = "pc";

  private final OkHttpClient client = CommonHttpClient.getClient();

  public int login() {
    log.info("로그인 시작: {}", loginId);

    // 1. 로그인 페이지 GET (세션 쿠키 획득)
    Request getRequest = new Request.Builder()
        .url(loginPageUrl)
        .get()
        .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
        .build();

    try (Response getResponse = client.newCall(getRequest).execute()) {
      if (!getResponse.isSuccessful()) {
        log.error("로그인 페이지 GET 실패, 코드: {}", getResponse.code());
        return -1;
      }
      log.debug("로그인 페이지 GET 성공, 세션 쿠키 획득");
    } catch (IOException e) {
      log.error("로그인 페이지 GET 중 예외 발생", e);
      return -1;
    }

    // 2. 로그인 API POST
    String payloadData = String.format("{ \"data\": \"%s,%s\" }", loginId, PUSH_ID);
    log.debug("로그인 페이로드: {}", payloadData);

    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(payloadData, mediaType);

    Request postRequest = new Request.Builder()
        .url(loginUrl)
        .post(body)
        .addHeader("Content-Type", "application/json; charset=UTF-8")
        .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
        .addHeader("Referer", loginPageUrl)
        .build();

    try (Response response = client.newCall(postRequest).execute()) {
      if (!response.isSuccessful()) {
        log.error("로그인 POST 실패, 코드: {}", response.code());
        return -1;
      }

      String responseBody = response.body().string();
      log.debug("로그인 응답: {}", responseBody);

      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(responseBody);
      int passengerId = rootNode.path("d").asInt();
      log.info("로그인 결과, passengerId: {}", passengerId);

      if (passengerId > 0) {
        log.info("로그인 성공: {}", loginId);
      } else {
        log.warn("로그인 실패, d 값: {}", passengerId);
      }
      return passengerId;
    } catch (IOException e) {
      log.error("로그인 POST 중 예외 발생", e);
      return -1;
    }
  }
}