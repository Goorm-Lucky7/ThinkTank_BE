도커파일
FROM amazoncorretto:17
## Open jdk 17

# Docker 설치를 위한 Amazon Linux 2 설정
RUN yum update -y && \
    amazon-linux-extras install docker -y


EXPOSE 8080

# JAR 파일 복사
ARG JAR_FILE=./build/libs/thinktank-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","app.jar"]
