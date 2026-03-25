# 🛡️ Tourist Safety Management System (TSMS)

---

## 📁 Project Structure

```
TSMS/
├── frontend/                    ← HTML/CSS/JS (open in browser)
│   ├── login.html               ← Login page
│   ├── register.html            ← Registration page
│   ├── dashboard.html           ← Main dashboard
│   ├── css/
│   │   └── style.css            ← All styles
│   └── js/
│       ├── api.js               ← API call functions
│       ├── auth.js              ← Login/Register logic
│       └── dashboard.js         ← Dashboard logic
│
└── backend/                     ← Spring Boot Java project
    ├── pom.xml                  ← Maven dependencies
    └── src/main/java/com/tsms/
        ├── TsmsApplication.java         ← Main entry point
        ├── model/
        │   ├── Tourist.java             ← Tourist entity (DB table)
        │   └── User.java                ← User entity (login)
        ├── repository/
        │   ├── TouristRepository.java   ← DB queries for Tourist
        │   └── UserRepository.java      ← DB queries for User
        ├── service/
        │   ├── TouristService.java      ← Tourist business logic
        │   └── AuthService.java         ← Login/Register logic
        ├── controller/
        │   ├── TouristController.java   ← REST API for tourists
        │   └── AuthController.java      ← REST API for auth
        ├── dto/
        │   └── Dto.java                 ← Request/Response objects
        └── config/
            ├── CorsConfig.java          ← Enables CORS for frontend
            ├── DataSeeder.java          ← Auto-creates demo data
            └── GlobalExceptionHandler.java
```

---

## ✅ Prerequisites

Install these before starting:

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17 or above | https://adoptium.net |
| Apache Maven | 3.8+ | https://maven.apache.org/download.cgi |
| MySQL Server | 8.0+ | https://dev.mysql.com/downloads/mysql/ |
| VS Code | Latest | https://code.visualstudio.com |

### VS Code Extensions to Install:
1. **Extension Pack for Java** (by Microsoft)
2. **Spring Boot Extension Pack** (by Pivotal/VMware)
3. **Live Server** (by Ritwick Dey) — to serve the frontend

---

## 🚀 Step-by-Step Setup

### STEP 1 — Extract the Project
Unzip the downloaded file. You will see two folders:
```
frontend/    ← open this in VS Code too
backend/     ← open this in VS Code as the Java project
```

---

### STEP 2 — Setup MySQL Database
1. Open **MySQL Workbench** or any MySQL client
2. Run this SQL command:
```sql
CREATE DATABASE tsms_db;
```
> The app will auto-create all tables when it starts (Spring JPA handles this)

---

### STEP 3 — Configure Database Password

Open `backend/src/main/resources/application.properties` and update:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
```

---

### STEP 4 — Open Backend in VS Code

1. Open VS Code
2. **File → Open Folder** → select the `backend/` folder
3. Wait for VS Code to load the Maven project (Java extensions will activate)
4. Open **Terminal** (Ctrl + `` ` ``)
5. Run:
```bash
mvn spring-boot:run
```

You should see:
```
========================================
  TSMS Backend started at port 8080
  Open: http://localhost:8080/api
========================================
[TSMS] Default admin created: admin / admin123
[TSMS] Sample tourists inserted.
```

---

### STEP 5 — Open Frontend in VS Code

#### Option A — Using Live Server (Recommended)
1. Open VS Code
2. **File → Open Folder** → select the `frontend/` folder
3. Right-click `login.html` → **"Open with Live Server"**
4. Browser opens at `http://127.0.0.1:5500/login.html`

#### Option B — Direct File Open
1. Just double-click `login.html` to open in your browser
2. *(Note: some browsers may block Fetch API on file:// — use Live Server if you have issues)*

---

### STEP 6 — Login and Use the App

Open browser at the frontend URL and login with:

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `admin123` |

---

## 🔌 REST API Reference

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/login` | Login with username & password |
| POST | `/api/auth/register` | Register new user |
| GET | `/api/tourists` | Get all tourists |
| POST | `/api/tourists` | Add a new tourist |
| PUT | `/api/tourists/{id}` | Update tourist (name/location/status) |
| DELETE | `/api/tourists/{id}` | Delete a tourist |

### Example: Add Tourist (POST /api/tourists)
```json
{
  "name": "John Smith",
  "location": "Bangalore, India",
  "status": "Safe"
}
```

### Status Values (exact strings):
- `"Safe"`
- `"In Danger"`
- `"Missing"`

---

## 🗄️ Database Table

```sql
-- Auto-created by Spring Boot JPA --

CREATE TABLE tourist (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  name     VARCHAR(255) NOT NULL,
  location VARCHAR(255) NOT NULL,
  status   VARCHAR(50)  NOT NULL
);

CREATE TABLE users (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  username  VARCHAR(255) NOT NULL UNIQUE,
  password  VARCHAR(255) NOT NULL,
  full_name VARCHAR(255)
);
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Backend | Java 17, Spring Boot 3.2 |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| API Style | REST (JSON) |
| Frontend-Backend | Fetch API + CORS |

---

## ⚠️ Troubleshooting

**Problem: "Cannot connect to server" on login**
→ Make sure the Spring Boot backend is running (`mvn spring-boot:run`)

**Problem: CORS error in browser console**
→ Use Live Server to serve frontend (not `file://` directly)

**Problem: `Access denied for user 'root'@'localhost'`**
→ Update the password in `application.properties`

**Problem: `Port 8080 already in use`**
→ Change `server.port=8081` in `application.properties` and update `API_BASE` in `js/api.js`

**Problem: Maven not found**
→ Add Maven to your system PATH environment variable

---

## 👨‍💻 Author Notes
This is a beginner-friendly college mini project demonstrating:
- Full-stack web development with Java Spring Boot
- REST API design following MVC architecture
- MySQL database integration with JPA
- Frontend-backend integration using Fetch API
- CORS configuration for cross-origin requests
