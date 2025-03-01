package me.suhsaechan.somansabusreservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.suhsaechan.somansabusreservation.config.CommonHttpClient;
import me.suhsaechan.somansabusreservation.object.constants.BusRoute;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationOkhttpService {

  @Value("${somansa.busin.url.reservation:https://cs.android.busin.co.kr/driving/ride_on.aspx/Reserv}")
  private String reservationUrl;

  private final OkHttpClient client = CommonHttpClient.getClient();

  public boolean makeReservation(int passengerId, BusRoute busRoute) {
    log.info("예약 시작: passengerId={}, busRoute={}", passengerId, busRoute.getDescription());

    String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode payload = mapper.createObjectNode();
    payload.put("passengerid", passengerId);
    payload.put("disptid", busRoute.getDisptid());
    payload.put("caralias", busRoute.getCaralias());
    payload.put("clientid", "busman");
    payload.put("createdate", today);

    String payloadJson = payload.toString();
    log.debug("예약 페이로드: {}", payloadJson);

    // Referer 헤더의 shuttletype 값을 URL 인코딩
    String encodedShuttleType = URLEncoder.encode(busRoute.getCaralias(), StandardCharsets.UTF_8);
    String refererUrl = "https://cs.android.busin.co.kr/driving/ride_on.aspx?rt=r&disptid=" + busRoute.getDisptid() +
        "&shuttletype=" + encodedShuttleType + "&scr=0&isshuttle=0";
    log.debug("Referer URL: {}", refererUrl);

    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(payloadJson, mediaType);

    Request request = new Request.Builder()
        .url(reservationUrl)
        .post(body)
        .addHeader("Content-Type", "application/json; charset=UTF-8")
        .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
        .addHeader("Referer", refererUrl)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("예약 실패, 코드: {}, 응답: {}", response.code(), response.body() != null ? response.body().string() : "없음");
        return false;
      }
      log.info("예약 성공: {}", busRoute.getDescription());
      return true;
    } catch (IOException e) {
      log.error("예약 중 예외 발생", e);
      return false;
    }
  }
}