# ---- Build stage ------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Falls das Wrapper-Script nicht als ausführbar eingecheckt ist:
COPY . .
#RUN chmod +x mvnw || true
RUN mvn -q -DskipTests clean package

# ---- Runtime stage (mit Tesseract & Ghostscript) ----------------------------
FROM eclipse-temurin:23-jre
WORKDIR /app

# Tesseract (inkl. deu/eng) + Ghostscript installieren
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
      tesseract-ocr tesseract-ocr-deu tesseract-ocr-eng ghostscript \
      fonts-dejavu-core ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Non-root User
RUN useradd -r -u 10001 appuser && chown -R appuser:appuser /app
USER appuser

# JAR aus dem Build kopieren
COPY --from=build /app/target/*.jar app.jar

# Sinnvolle Defaults
ENV TESSERACT_LANGS=deu+eng \
    JAVA_TOOL_OPTIONS="--enable-preview"

# Start
ENTRYPOINT ["java","-jar","/app/app.jar"]
