FROM amazoncorretto:21-alpine
WORKDIR /app
COPY ./build/libs/cheeeese-0.0.1-SNAPSHOT.jar /app/cheeeese.jar
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "cheeeese.jar"]