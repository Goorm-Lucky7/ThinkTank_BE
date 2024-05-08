FROM openjdk:17-jdk
VOLUME /tmp
EXPOSE 8080
COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar", "-Duser.timezone=Asia/Seoul", "/app.jar"]

