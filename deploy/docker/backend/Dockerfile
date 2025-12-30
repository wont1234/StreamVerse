FROM m.daocloud.io/docker.io/library/maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY StreamVerse/pom.xml ./pom.xml
COPY StreamVerse/src ./src
RUN mvn -DskipTests package

FROM m.daocloud.io/docker.io/library/eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
