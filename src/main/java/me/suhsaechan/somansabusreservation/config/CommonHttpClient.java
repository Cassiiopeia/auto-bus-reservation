package me.suhsaechan.somansabusreservation.config;

import okhttp3.OkHttpClient;

public class CommonHttpClient {
  // 단순 In-Memory CookieJar 구현체
  private static final SimpleCookieJar cookieJar = new SimpleCookieJar();

  private static final OkHttpClient client = new OkHttpClient.Builder()
      .cookieJar(cookieJar)
      .build();

  public static OkHttpClient getClient() {
    return client;
  }
}
