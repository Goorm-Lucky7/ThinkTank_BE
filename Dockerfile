FROM openjdk:17
LABEL description="PetMates API Web Application"
VOLUME /home/web/app/petmates/upload
EXPOSE 8080
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /home/web/app/workdir/petmates-api-app.jar
WORKDIR /home/web/app/workdir/
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","./petmates-api-app.jar"]