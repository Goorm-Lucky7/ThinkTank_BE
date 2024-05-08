FROM openjdk:17-jdk
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE= ThinkTank_BE/build/libs/thinktank-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java","-jar", "-Duser.timezone=Asia/Seoul", "/app.jar"]

