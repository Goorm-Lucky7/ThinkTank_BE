FROM amazoncorretto:17
## Open jdk 17




EXPOSE 8080

# JAR 파일 복사
ARG JAR_FILE=./buildtest/libs/thinktank-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","app.jar"]
