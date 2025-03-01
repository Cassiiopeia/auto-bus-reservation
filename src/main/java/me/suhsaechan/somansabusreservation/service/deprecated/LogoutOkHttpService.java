//package me.suhsaechan.somansabusreservation.service.deprecated;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import me.suhsaechan.somansabusreservation.config.CommonHttpClient;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class LogoutOkHttpService {
//
//  // 로그아웃 URL (예: https://cs.android.busin.co.kr/logout.aspx)
//  @Value("${somansa.busin.logout-page}")
//  private String logoutUrl;
//
//  private final OkHttpClient client = CommonHttpClient.getClient();
//
//  /**
//   * 로그아웃 요청을 보내고, 성공하면 true를 반환합니다.
//   */
//  public boolean logout() {
//    Request request = new Request.Builder()
//        .url(logoutUrl)
//        .get()
//        .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
//        .build();
//    try (Response response = client.newCall(request).execute()) {
//      if (!response.isSuccessful()) {
//        log.error("Logout request failed with code: {}", response.code());
//        return false;
//      }
//      String html = response.body().string();
//      log.info("Logout response HTML length: {}", html.length());
//      return true;
//    } catch (IOException e) {
//      log.error("Exception during logout", e);
//      return false;
//    }
//  }
//}
