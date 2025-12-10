# SWEN3 Paperless Projekt

### Hinweis zum Frontend
Bitte das Frontend immer im Inkognito-Modus öffnen,
da der Browser-Cache sonst alte JavaScript-Versionen laden kann.

## Anleitung
- `docker compose down`
- `docker compose build`
- `docker compose up`

### Zugriff
- http://localhost
- http://localhost:8082/api/dokumente

### RabbitMQ
RabbitMQ Management UI: http://localhost:15672
Benutzername: guest
PW: guest

### MinIO File Storage
MinIO File Storage (optional): http://localhost:9001
Benutzername: minioadmin
PW: minioadmin

### GitHub Repository
GitHub: https://github.com/SamuelBrunner1/SWEN-3-Projekt

## Features

### User Login/Registrierung
User können Profile anlegen
Jeder User hat nur auf seine eigenen Dokumente Zugriff
