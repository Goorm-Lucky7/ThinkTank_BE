FROM amazoncorretto:17
EXPOSE 8080
COPY /build/libs/thinktank-0.0.1-SNAPSHOT.jar /home/web/app/workdir/thinktank.jar
WORKDIR /home/web/app/workdir

# 중괄호로 설정
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","-Dspring.profiles.active=dev","/home/web/app/workdir/thinktank.jar"]
