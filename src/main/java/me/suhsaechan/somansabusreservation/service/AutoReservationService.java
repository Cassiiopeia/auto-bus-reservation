package me.suhsaechan.somansabusreservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.suhsaechan.somansabusreservation.object.constants.BusRoute;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoReservationService {

  private final LoginOkhttpService loginService;
  private final SessionOkhttpService sessionService;
  private final ReservationOkhttpService reservationService;

  private static final String RIDE_ID = "chan4760@somansa.com";
  private static final String PUSH_ID = "pc";
  private static final BusRoute DEFAULT_BUS_ROUTE = BusRoute.BUS_0710_DANGSAN_2;

  @Scheduled(cron = "0 0 6 * * ?") // 매일 오전 6시
  public void autoReserve() {
    log.info("자동 예약 시작");

    // 1. 로그인
    int passengerId = loginService.login();
    if (passengerId <= 0) {
      log.error("로그인 실패, 예약 중단");
      return;
    }

    // 2. 세션 생성
    boolean sessionCreated = sessionService.createSession(RIDE_ID, passengerId, PUSH_ID);
    if (!sessionCreated) {
      log.error("세션 생성 실패, 예약 중단");
      return;
    }

    // 3. 예약
    boolean reservationSuccess = reservationService.makeReservation(passengerId, DEFAULT_BUS_ROUTE);
    if (reservationSuccess) {
      log.info("자동 예약 완료: {}", DEFAULT_BUS_ROUTE.getDescription());
    } else {
      log.error("예약 실패");
    }
  }
}