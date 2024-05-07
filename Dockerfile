FROM openjdk:17
LABEL description="Think tank"
VOLUME /home/web/app/petmates/upload
EXPOSE 8082
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /home/web/app/workdir2/think-tank.jar
WORKDIR /home/web/app/workdir2/
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","-Dspring.profiles.active=dev","./think-tank.jar"]
