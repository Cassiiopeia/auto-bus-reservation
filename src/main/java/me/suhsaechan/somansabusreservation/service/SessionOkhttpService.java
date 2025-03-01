package me.suhsaechan.somansabusreservation.service;

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
public class SessionOkhttpService {

  @Value("${somansa.busin.url.create-session:https://cs.android.busin.co.kr/Default.aspx/CreateSession}")
  private String createSessionUrl;

  private final OkHttpClient client = CommonHttpClient.getClient();

  public boolean createSession(String rideId, int passengerId, String pushId) {
    log.info("세션 생성 시작: rideId={}, passengerId={}", rideId, passengerId);

    String data = String.format("%s,%d,,%s", rideId, passengerId, pushId);
    String payload = String.format("{ \"data\": \"%s\" }", data);
    log.debug("세션 생성 페이로드: {}", payload);

    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(payload, mediaType);

    Request request = new Request.Builder()
        .url(createSessionUrl)
        .post(body)
        .addHeader("Content-Type", "application/json; charset=UTF-8")
        .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
        .addHeader("Referer", "https://cs.android.busin.co.kr/default.aspx?device=")
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("세션 생성 실패, 코드: {}", response.code());
        return false;
      }
      log.info("세션 생성 성공: rideId={}, passengerId={}", rideId, passengerId);
      return true;
    } catch (IOException e) {
      log.error("세션 생성 중 예외 발생", e);
      return false;
    }
  }
}