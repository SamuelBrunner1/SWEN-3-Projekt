# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/log-batch-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /app/input /app/archive

ENTRYPOINT ["java","-jar","app.jar"]
