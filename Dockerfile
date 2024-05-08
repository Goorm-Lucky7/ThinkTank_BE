# Stage 1: Build the application
FROM gradle:8.0-jdk17 AS build

# Set the working directory in the container
WORKDIR /workspace/ThinkTank_BE

# Spring 소스 코드를 이미지에 복사
COPY . .

# gradle 빌드 시 proxy 설정을 gradle.properties에 추가
RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

RUN gradle wrapper

# gradlew를 이용한 프로젝트 필드
RUN ./gradlew clean build

# Stage 2: Run the application
FROM krmp-d2hub-idock.9rum.cc/goorm/eclipse-temurin:17-jre

COPY --from=build /workspace/ThinkTank_BE/build/libs/thinktank-0.0.1-SNAPSHOT.jar .

# DATABASE_URL을 환경 변수로 삽입
ENV DATABASE_URL=jdbc:mysql://mysql:3306/mysql?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true

# 빌드 결과 jar 파일을 실행
# Start the application
CMD ["java", "-Dhttp.proxyHost=krmp-proxy.9rum.cc", "-Dhttp.proxyPort=3128","-Dhttps.proxyHost=krmp-proxy.9rum.cc", "-Dhttps.proxyPort=3128", "-Dspring.profiles.active=dev", "-jar", "thinktank-0.0.1-SNAPSHOT.jar"]
