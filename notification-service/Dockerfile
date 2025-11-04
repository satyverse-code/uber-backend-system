# syntax=docker/dockerfile:1

# ===== Build stage =====
FROM maven:3.9.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -e -DskipTests clean package

# ===== Run stage =====
FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8092
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
