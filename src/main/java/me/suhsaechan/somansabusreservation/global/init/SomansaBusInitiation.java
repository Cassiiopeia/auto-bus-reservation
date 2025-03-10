package me.suhsaechan.somansabusreservation.global.init;

import static me.suhsaechan.somansabusreservation.global.util.LogUtil.lineLog;
import static me.suhsaechan.somansabusreservation.global.util.LogUtil.logServerInitDuration;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.suhsaechan.somansabusreservation.global.docs.GithubIssueService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SomansaBusInitiation implements ApplicationRunner {

	private final GithubIssueService githubIssueService;

	@Override
	// 모든 Bean 등록 완료시 실행
	public void run(ApplicationArguments args) throws Exception {
		lineLog("SERVER START");
		lineLog("데이터 초기화 시작");
		LocalDateTime startTime = LocalDateTime.now();

		// Github 이슈 업데이트
		githubIssueService.syncGithubIssues();

		logServerInitDuration(startTime);
		log.info("서버 데이터 초기화 및 업데이트 완료");
	}
}
