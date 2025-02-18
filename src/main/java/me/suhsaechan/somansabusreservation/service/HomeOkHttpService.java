package me.suhsaechan.somansabusreservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.suhsaechan.somansabusreservation.config.CommonHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class HomeOkHttpService {

  // 홈 페이지 URL (예: https://cs.android.busin.co.kr/driving/ride_list.aspx?rt=s)
  @Value("${somansa.busin.home-page}")
  private String homeUrl;

  private final OkHttpClient client = CommonHttpClient.getClient();

  /**
   * 로그인 후 세션 쿠키를 이용하여 홈 페이지 HTML을 반환합니다.
   */
  public String fetchHomePage() {
    Request request = new Request.Builder()
        .url(homeUrl)
        .get()
        .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1")
        .addHeader("Referer", "https://cs.android.busin.co.kr/login.aspx")
        .build();
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("Home page fetch failed with code: {}", response.code());
        return null;
      }
      String html = response.body().string();
      log.info("Fetched home page HTML, length: {}", html.length());
      return html;
    } catch (IOException e) {
      log.error("Exception fetching home page", e);
      return null;
    }
  }
}
