FROM eclipse-temurin:23-jdk AS build
WORKDIR /app

# komplettes Modul kopieren (inkl. pom.xml, mvnw, .mvn, src/ ...)
COPY . .

# falls mvnw nicht als ausführbar eingecheckt ist
RUN chmod +x mvnw || true

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:23-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
