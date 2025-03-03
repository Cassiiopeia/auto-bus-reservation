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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationOkhttpService {

  @Value("${somansa.busin.url.reservation:https://cs.android.busin.co.kr/driving/ride_on.aspx/Reserv}")
  private String reservationUrl;

  private final OkHttpClient client = CommonHttpClient.getClient();
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * 지정된 날짜에 버스 예약을 수행합니다.
   *
   * @param passengerId 승객 ID
   * @param busRoute 버스 노선
   * @param reservationDate 예약 날짜
   * @return 예약 성공 여부
   */
  public boolean makeReservation(int passengerId, BusRoute busRoute, LocalDate reservationDate) {
    String formattedDate = reservationDate.format(DATE_FORMATTER);
    log.info("예약 시작 - 승객ID: {}, 버스: {}, 예약일: {}",
        passengerId, busRoute.getDescription(), formattedDate);

    // 페이로드 생성
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode payload = mapper.createObjectNode();
    payload.put("passengerid", passengerId);
    payload.put("disptid", busRoute.getDisptid());
    payload.put("caralias", busRoute.getCaralias());
    payload.put("clientid", "busman");
    payload.put("createdate", formattedDate);

    String payloadJson = payload.toString();
    log.debug("예약 페이로드: {}", payloadJson);

    // Referer 헤더의 shuttletype 값을 URL 인코딩
    String encodedShuttleType = URLEncoder.encode(busRoute.getCaralias(), StandardCharsets.UTF_8);
    String refererUrl = "https://cs.android.busin.co.kr/driving/ride_on.aspx?rt=r&disptid=" + busRoute.getDisptid() +
        "&shuttletype=" + encodedShuttleType + "&scr=0&isshuttle=0";
    log.debug("Referer URL: {}", refererUrl);

    // HTTP 요청 구성
    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(payloadJson, mediaType);

    Request request = new Request.Builder()
        .url(reservationUrl)
        .post(body)
        .addHeader("Content-Type", "application/json; charset=UTF-8")
        .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
        .addHeader("Referer", refererUrl)
        .build();

    // HTTP 요청 실행
    try (Response response = client.newCall(request).execute()) {
      int responseCode = response.code();

      if (!response.isSuccessful()) {
        String responseBody = response.body() != null ? response.body().string() : "응답 없음";
        log.error("예약 실패 - HTTP 상태 코드: {}, 응답: {}", responseCode, responseBody);
        return false;
      }

      String responseBody = response.body() != null ? response.body().string() : "";
      log.debug("예약 응답: {}", responseBody);
      log.info("예약 성공 - 버스: {}, 예약일: {}", busRoute.getDescription(), formattedDate);

      // TODO: 향후 DB에 예약 결과 저장 및 알림 발송 로직 추가
      return true;
    } catch (IOException e) {
      log.error("예약 중 예외 발생", e);
      return false;
    }
  }

  /**
   * 당일 예약을 위한 오버로딩 메소드 (하위 호환성 유지)
   *
   * @param passengerId 승객 ID
   * @param busRoute 버스 노선
   * @return 예약 성공 여부
   */
  public boolean makeReservation(int passengerId, BusRoute busRoute) {
    return makeReservation(passengerId, busRoute, LocalDate.now());
  }
}