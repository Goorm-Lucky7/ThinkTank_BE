#FROM openjdk:17
#VOLUME /home/web/app/thinktank/upload
#EXPOSE 8080
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} /home/web/app/workdir/thinktank-api-app.jar
#WORKDIR /home/web/app/workdir/
#ENTRYPOINT [“java”,“-jar”,“-Duser.timezone=Asia/Seoul”,“./thinktank-api-app.jar”]