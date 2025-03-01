//package me.suhsaechan.somansabusreservation.service.deprecated;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import me.suhsaechan.somansabusreservation.config.CommonHttpClient;
//import me.suhsaechan.somansabusreservation.service.SomansaBusInService;
//import okhttp3.MediaType;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BusInReservationSeleniumService {
//
//  // 예약 API URL (기본값: https://cs.android.busin.co.kr/driving/ride_reserv.aspx/Reserv)
//  @Value("${somansa.busin.reservation-url:https://cs.android.busin.co.kr/driving/ride_reserv.aspx/Reserv}")
//  private String reservationUrl;
//
//  // 로그인 서비스(쿠키를 포함한 세션 관리를 위해 사용)
//  private final SomansaBusInService somansaBusInService;
//
//  /**
//   * 예약을 진행합니다.
//   *
//   * @param createdate  예약일 (예: "2025-02-18")
//   * @param disptid     예약에 해당하는 운행 ID (예: 46565)
//   * @param passengerid 탑승자 ID (예: 126491)
//   * @param caralias    예약 구분 (예: "출근")
//   * @param clientid    클라이언트 ID (예: "busman")
//   */
//  public void reserve(String createdate, int disptid, int passengerid, String caralias, String clientid) {
//    // 로그인 수행 (쿠키가 CommonHttpClient의 CookieJar에 저장됩니다)
//    somansaBusInService.autoLogin();
//
//    // JSON 형식의 예약 요청 페이로드 구성 (키와 문자열 값은 반드시 큰따옴표로 감싸야 합니다)
//    String payload = String.format(
//        "{\"passengerid\":%d,\"disptid\":%d,\"caralias\":\"%s\",\"clientid\":\"%s\",\"createdate\":\"%s\"}",
//        passengerid, disptid, caralias, clientid, createdate
//    );
//    log.info("Reservation payload: {}", payload);
//
//    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//    RequestBody body = RequestBody.create(payload, mediaType);
//
//    Request request = new Request.Builder()
//        .url(reservationUrl)
//        .post(body)
//        .addHeader("Content-Type", "application/json; charset=UTF-8")
//        // 필요 시 Referer, Origin 등 추가 헤더도 설정 가능
//        .build();
//
//    try (Response response = CommonHttpClient.getClient().newCall(request).execute()) {
//      if (!response.isSuccessful()) {
//        log.error("Reservation request failed with code: {}", response.code());
//        return;
//      }
//      String responseBody = response.body().string();
//      log.info("Reservation response body: {}", responseBody);
//
//      // 응답 JSON 파싱 (필요한 경우)
//      ObjectMapper mapper = new ObjectMapper();
//      JsonNode rootNode = mapper.readTree(responseBody);
//      JsonNode dNode = rootNode.path("d");
//      if (dNode.isMissingNode()) {
//        log.error("Reservation response does not contain 'd' field.");
//      } else {
//        int dValue = dNode.asInt();
//        log.info("Reservation response 'd' value: {}", dValue);
//      }
//    } catch (IOException e) {
//      log.error("Exception during reservation request", e);
//    }
//  }
//}
