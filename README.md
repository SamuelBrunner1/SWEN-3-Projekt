# Brunner-Wydra-SwenProjekt

## Projektbeschreibung
Sprint 1 umfasst:
- Einrichtung des Projekts mit Spring Boot
- Anbindung an eine PostgreSQL-Datenbank (über JPA/Hibernate)
- Erste REST-Endpoints für die Entität `Dokument`
- Erstellung eines Docker-Setups für Server & Datenbank
- Unit-Tests zum Nachweis der Funktionalität

---

## Voraussetzungen
- Java 23 (oder kompatible Version, z. B. Java 21 LTS)
- Gradle (Wrapper liegt im Projekt bei)
- Docker & Docker Compose
- PostgreSQL-Client (optional, um DB direkt zu testen)

## Protokoll
- Datenbank aufsetzen Befehle:
  - Datenbank nach application.yml aufsetzen: docker compose up -d
  - In Datenbank einloggen: docker exec -it swen_postgres psql -U postgres -d swenprojekt




