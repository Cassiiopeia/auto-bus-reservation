package me.suhsaechan.somansabusreservation.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Dispatcher;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CommonHttpClient {
  private static final SimpleCookieJar cookieJar = new SimpleCookieJar();
  private static final Dispatcher dispatcher = new Dispatcher();

  // 모든 인증서를 신뢰하는 TrustManager
  // 소만사 서버 인증서 교체(Sectigo DV R36, 2026-03-16) 후 JVM truststore가 체인을 신뢰하지 못해
  // PKIX path building 오류가 발생하므로, 대상 도메인이 고정된 개인 자동화 도구 특성상 검증을 우회한다.
  private static final X509TrustManager trustAllManager = new X509TrustManager() {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  };

  static {
    dispatcher.setMaxRequests(100); // 최대 100개 동시 요청
    dispatcher.setMaxRequestsPerHost(10); // 호스트당 최대 10개 요청
    log.debug("OkHttpClient Dispatcher 설정: MaxRequests=100, MaxRequestsPerHost=10");
  }

  private static final OkHttpClient client = buildClient();

  private static OkHttpClient buildClient() {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[] {trustAllManager}, new java.security.SecureRandom());
      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      return new OkHttpClient.Builder()
          .cookieJar(cookieJar)
          .dispatcher(dispatcher)
          .sslSocketFactory(sslSocketFactory, trustAllManager)
          .hostnameVerifier((hostname, session) -> true)
          .build();
    } catch (Exception e) {
      log.error("SSL 우회 OkHttpClient 생성 실패", e);
      throw new RuntimeException("OkHttpClient 초기화 실패", e);
    }
  }

  public static OkHttpClient getClient() {
    return client;
  }

  @Slf4j
  public static class SimpleCookieJar implements CookieJar {
    private final List<Cookie> cookieStore = new ArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
      cookieStore.addAll(cookies);
      for (Cookie cookie : cookies) {
        log.debug("쿠키 저장: {}={}, Domain: {}, Path: {}, ExpiresAt: {}",
            cookie.name(), cookie.value(), cookie.domain(), cookie.path(), cookie.expiresAt());
      }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
      List<Cookie> validCookies = new ArrayList<>();
      for (Cookie cookie : cookieStore) {
        if (cookie.matches(url)) {
          validCookies.add(cookie);
          log.debug("쿠키 로드: {}={} for URL: {}", cookie.name(), cookie.value(), url);
        }
      }
      return validCookies;
    }
  }
}