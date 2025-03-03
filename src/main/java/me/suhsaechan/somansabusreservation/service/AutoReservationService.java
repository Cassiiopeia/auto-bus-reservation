package me.suhsaechan.somansabusreservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.suhsaechan.somansabusreservation.object.constants.BusRoute;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoReservationService {

  private final LoginOkhttpService loginService;
  private final SessionOkhttpService sessionService;
  private final ReservationOkhttpService reservationService;

  private static final String PUSH_ID = "pc";
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * 사용자 ID와 버스 노선, 예약 날짜를 받아 예약을 진행합니다.
   *
   * @param userId 사용자 ID (이메일)
   * @param busRoute 예약할 버스 경로
   * @param reservationDate 예약 날짜
   * @return 예약 성공 여부
   */
  public boolean reserveBusForUser(String userId, BusRoute busRoute, LocalDate reservationDate) {
    log.info("사용자 {} 버스 예약 시작: {}, 예약일: {}",
        userId, busRoute.getDescription(), reservationDate.format(DATE_FORMATTER));

    // 1. 로그인
    int passengerId = loginService.login(userId);
    if (passengerId <= 0) {
      log.error("로그인 실패, 예약 중단: {}", userId);
      return false;
    }
    log.info("로그인 성공, passengerId: {}", passengerId);

    // 2. 세션 생성
    boolean sessionCreated = sessionService.createSession(userId, passengerId, PUSH_ID);
    if (!sessionCreated) {
      log.error("세션 생성 실패, 예약 중단: {}", userId);
      return false;
    }
    log.info("세션 생성 성공");

    // 3. 예약 (지정된 날짜로)
    boolean reservationSuccess = reservationService.makeReservation(passengerId, busRoute, reservationDate);
    if (reservationSuccess) {
      log.info("자동 예약 완료: {}, 사용자: {}, 날짜: {}",
          busRoute.getDescription(), userId, reservationDate.format(DATE_FORMATTER));
      return true;
    } else {
      log.error("예약 실패: {}, 사용자: {}", busRoute.getDescription(), userId);
      return false;
    }
  }

  /**
   * 매일 자정 12시 13분에 자동으로 실행되는 예약 스케줄러
   * 이 메소드는 모든 활성 사용자에 대해 3일 후 예약을 처리합니다.
   */
  @Scheduled(cron = "0 13 0 * * ?") // 매일 오전 12시 13분
  public void scheduledAutoReservation() {
    log.info("자동 예약 스케줄러 실행 시작 - 3일 후 예약");

    // 3일 후 날짜 계산
    LocalDate threeDaysLater = LocalDate.now().plusDays(3);
    log.info("예약 대상 날짜: {}", threeDaysLater.format(DATE_FORMATTER));

    // TODO: 향후 DB에서 사용자 목록과 선호 버스를 조회하도록 변경
    // 현재는 하드코딩된 사용자로 테스트
    reserveBusForUser("chan4760@somansa.com", BusRoute.BUS_0710_DANGSAN_2, threeDaysLater);

    // 아래는 DB 연동 후 구현될 로직의 예시입니다
    /*
    List<UserPreference> activeUsers = userPreferenceRepository.findAllActiveUsers();
    for (UserPreference user : activeUsers) {
        boolean success = reserveBusForUser(
            user.getUserId(),
            user.getPreferredBusRoute(),
            threeDaysLater
        );

        // 예약 결과 저장
        ReservationResult result = new ReservationResult();
        result.setUserId(user.getUserId());
        result.setReservationDate(threeDaysLater);
        result.setBusRoute(user.getPreferredBusRoute().getDescription());
        result.setSuccess(success);
        reservationResultRepository.save(result);

        // TODO: 알림 전송 로직 구현
    }
    */

    log.info("자동 예약 스케줄러 실행 완료");
  }

  /**
   * 특정 사용자에 대해 지정된 버스와 3일 후 날짜로 예약합니다.
   *
   * @param userId 사용자 ID (이메일)
   * @param busRoute 예약할 버스 경로
   * @return 예약 성공 여부
   */
  public boolean reserveBusForUserInThreeDays(String userId, BusRoute busRoute) {
    LocalDate threeDaysLater = LocalDate.now().plusDays(3);
    log.debug("3일 후 날짜 계산: {}", threeDaysLater.format(DATE_FORMATTER));
    return reserveBusForUser(userId, busRoute, threeDaysLater);
  }

  // 기존 메소드 유지 (하위 호환성)
  @Scheduled(cron = "0 0 6 * * ?") // 매일 오전 6시
  public void autoReserve() {
    log.info("레거시 자동 예약 시작");

    String defaultUserId = "chan4760@somansa.com";

//    BusRoute defaultBusRoute = BusRoute.BUS_0705_GUNJA_1;
//    boolean success = reserveBusForUser(defaultUserId, defaultBusRoute, LocalDate.now());
//    log.info("레거시 자동 예약 완료, 결과: {}", success ? "성공" : "실패");

    log.info("레거시 자동 예약 완료, 결과: {}",  reserveBusForUser(defaultUserId, BusRoute.BUS_0705_GUNJA_1, LocalDate.now()) ? "성공" : "실패");
    log.info("레거시 자동 예약 완료, 결과: {}",  reserveBusForUser(defaultUserId, BusRoute.BUS_0735_GUNJA_2, LocalDate.now()) ? "성공" : "실패");
    log.info("레거시 자동 예약 완료, 결과: {}",  reserveBusForUser(defaultUserId, BusRoute.BUS_1745_GUNJA_1, LocalDate.now()) ? "성공" : "실패");
    log.info("레거시 자동 예약 완료, 결과: {}",  reserveBusForUser(defaultUserId, BusRoute.BUS_1815_GUNJA_2, LocalDate.now()) ? "성공" : "실패");
  }
}