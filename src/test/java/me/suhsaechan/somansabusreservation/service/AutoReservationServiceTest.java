package me.suhsaechan.somansabusreservation.service;

import static me.suhsaechan.somansabusreservation.global.util.LogUtil.lineLog;
import static me.suhsaechan.somansabusreservation.global.util.LogUtil.timeLog;
import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@Profile("dev")
@Slf4j
class AutoReservationServiceTest {
  @Autowired
  AutoReservationService autoReservationService;

  @Test
  public void mainTest() {
    lineLog("테스트 시작");
    timeLog(this::테스트_예약);
    lineLog("테스트 종료");
  }

  public void 테스트_예약(){
    autoReservationService.autoReserve();
  }
}