# Collab Notes — Spring Boot Backend

## Texnologiyalar
- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- WebSocket (Spring native)
- PostgreSQL
- JPA / Hibernate
- Lombok

## Ishga tushirish

### 1. PostgreSQL bazasini yarating
```sql
CREATE DATABASE collab_notes;
```

### 2. application.yml ni sozlang
```
src/main/resources/application.yml
```
Agar kerak bo'lsa username/password o'zgartiring:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/collab_notes
    username: postgres
    password: postgres
```

### 3. Build va run
```bash
./mvnw clean package -DskipTests
java -jar target/collab-notes-1.0.0.jar
```
yoki IDE dan to'g'ridan-to'g'ri:
```bash
./mvnw spring-boot:run
```

Server: `http://localhost:8080`

---

## API Endpoints

### Auth
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Ro'yxatdan o'tish |
| POST | `/api/auth/login` | Kirish |
| GET  | `/api/auth/me` | Profil (JWT kerak) |

### Notes
| Method | URL | Description |
|--------|-----|-------------|
| GET    | `/api/notes` | Barcha note'lar |
| POST   | `/api/notes` | Note yaratish |
| GET    | `/api/notes/{id}` | Bitta note |
| PUT    | `/api/notes/{id}` | Tahrirlash |
| DELETE | `/api/notes/{id}` | O'chirish |
| POST   | `/api/notes/{id}/collaborators` | Hamkor qo'shish |
| DELETE | `/api/notes/{id}/collaborators/{userId}` | Hamkorni olib tashlash |
| GET    | `/api/notes/{id}/versions` | Oxirgi 5 versiya |
| GET    | `/api/notes/{id}/online` | Online foydalanuvchilar |

### Comments
| Method | URL | Description |
|--------|-----|-------------|
| GET    | `/api/notes/{noteId}/comments` | Kommentariyalar |
| POST   | `/api/notes/{noteId}/comments` | Kommentariya qo'shish |
| PATCH  | `/api/notes/{noteId}/comments/{id}/resolve` | Yechildi deb belgilash |
| DELETE | `/api/notes/{noteId}/comments/{id}` | O'chirish |

### WebSocket
```
ws://localhost:8080/ws/notes/{noteId}?token=JWT_TOKEN
```

**Message formati:**
```json
{
  "type": "CONTENT_CHANGE",
  "noteId": 1,
  "content": "yangi matn...",
  "user": { "id": 1, "username": "ali" }
}
```

**Message turlari:** `CONTENT_CHANGE`, `TITLE_CHANGE`, `CURSOR_MOVE`, `USER_JOINED`, `USER_LEFT`, `COMMENT_ADDED`

---

## Loyiha tuzilmasi
```
src/main/java/com/collnotes/app/
├── config/          # Security, WebSocket konfiguratsiyalari
├── controller/      # REST kontrollerlar
├── dto/             # Request/Response ob'ektlar
├── entity/          # JPA entity'lar
├── exception/       # Global exception handler
├── repository/      # Spring Data JPA
├── security/        # JWT filter va util
├── service/         # Biznes logika
└── websocket/       # Real-time WebSocket handler
```
