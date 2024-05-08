FROM openjdk:17
EXPOSE 8080
COPY build/libs/thinktank-0.0.1-SNAPSHOT.jar thinktank.jar
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","/thinktank.jar"]