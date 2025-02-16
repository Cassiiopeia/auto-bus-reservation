# OpenJDK 17 기반 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 복사
ARG JAR_FILE=build/libs/somansa-bus-reservation-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 환경 변수 설정 (서울 타임존)
ENV TZ=Asia/Seoul

# 컨테이너 포트 지정
EXPOSE 8080

# Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
