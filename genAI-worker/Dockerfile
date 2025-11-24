FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# komplettes Modul kopieren (inkl. pom.xml, mvnw, .mvn, src/ ...)
COPY . .

# falls mvnw nicht als ausführbar eingecheckt ist
#RUN chmod +x mvnw || true

RUN mvn clean package -DskipTests

FROM eclipse-temurin:23-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
