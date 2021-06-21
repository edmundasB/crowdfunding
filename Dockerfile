FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8280
ADD /build/libs/crowdfunding-service-0.0.1-SNAPSHOT.jar crowdfunding-service.jar
ENTRYPOINT ["java", "-jar", "crowdfunding-service.jar"]