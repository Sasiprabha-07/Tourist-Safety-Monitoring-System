# 🧭 SafeTrail – Smart Tourist Safety Monitoring System

A complete full-stack safety monitoring system for tourists.

---

## 📦 Project Structure

```
SafeTrail/
├── frontend/
│   └── index.html              ← Complete frontend (open in browser)
│
├── backend/
│   ├── pom.xml                 ← Maven dependencies
│   ├── application.properties  ← Config (DB, Twilio, Email, JWT)
│   ├── SafeTrailApplication.java
│   ├── Tourist.java            ← Entity
│   ├── Models.java             ← Alert + Zone entities
│   ├── Controllers.java        ← REST API controllers
│   ├── Services.java           ← Business logic + SMS + AI detection
│   └── Security.java           ← JWT + Spring Security
│
└── database/
    └── safetrail_database.sql  ← MySQL schema + seed data
```

---

## 🚀 Step-by-Step Setup

### Step 1: MySQL Database Setup
```sql
-- Open MySQL Workbench or terminal:
mysql -u root -p

-- Run the SQL file:
source safetrail_database.sql;
```

### Step 2: Backend – Spring Boot Setup
```bash
# Make sure Java 17+ and Maven are installed
java -version  # should be 17+
mvn -version

# Create Spring Boot project structure:
mkdir -p src/main/java/com/safetrail/{model,controller,service,repository,security,dto}
mkdir -p src/main/resources

# Copy all .java files into correct packages
# Copy application.properties to src/main/resources/

# Update application.properties with your real values:
# - spring.datasource.password = your MySQL password
# - twilio.account.sid = from twilio.com/console
# - twilio.auth.token = from twilio.com/console
# - spring.mail.username/password = your Gmail app password

# Build and run:
mvn clean install
mvn spring-boot:run

# Backend runs at: http://localhost:8080
```

### Step 3: Frontend Setup
```
Simply open frontend/index.html in your browser.
For production, deploy to Firebase Hosting or Netlify.
```

---

## 🔌 REST API Endpoints

### Auth
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/login` | Login, get JWT token |

### Tourists
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/tourists/register` | Register new tourist |
| GET | `/api/tourists` | Get all tourists (Admin) |
| GET | `/api/tourists/{id}` | Get tourist by ID |
| PUT | `/api/tourists/{id}` | Update profile |
| POST | `/api/tourists/{id}/checkin` | Tourist check-in (safe) |

### Location
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/location/update` | Update GPS location |
| GET | `/api/location/live` | Get all live locations |
| GET | `/api/location/history/{id}` | Location history |

### Alerts
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/alerts/sos/{touristId}` | Trigger SOS |
| GET | `/api/alerts/active` | Get active alerts |
| GET | `/api/alerts` | Get all alerts |
| PUT | `/api/alerts/{id}/resolve` | Resolve alert |

### Zones
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/zones` | Get all zones |
| POST | `/api/zones` | Create zone (Admin) |
| PUT | `/api/zones/{id}` | Update zone (Admin) |
| GET | `/api/zones/check?lat=&lng=` | Check GPS zone status |

---

## 🔐 JWT Authentication

All protected endpoints require:
```
Authorization: Bearer <your-jwt-token>
```
Get the token from `/api/auth/login`.

---

## 📱 Tourist Mobile App (Location Update)

Call this every 30 seconds from the tourist device:
```javascript
// POST /api/location/update
{
  "touristId": 1,
  "latitude": 13.0567,
  "longitude": 80.2832,
  "batteryLevel": 72
}
```

---

## 🚨 SOS Flow

1. Tourist presses SOS button
2. App calls: `POST /api/alerts/sos/1?lat=13.05&lng=80.28`
3. Backend:
   - Creates SOS alert in MySQL
   - Sends SMS to emergency contact (Twilio)
   - Sends SMS to control room
   - Sends email to admin
4. Admin dashboard shows SOS in real time
5. Admin dispatches help, clicks "Resolve"

---

## 🤖 AI Features (Built-in)

- **Anomaly Detection**: If tourist GPS jumps > 30 m/s (walking speed), unusual movement alert fires
- **Zone Boundary Check**: Every location update checked against all zone boundaries using Haversine formula
- **Low Battery Alert**: Auto SMS when battery ≤ 20%

---

## 🌐 Deployment

### Backend → AWS EC2
```bash
mvn clean package
scp target/safetrail-backend.jar ec2-user@your-ec2-ip:/home/ec2-user/
ssh ec2-user@your-ec2-ip
java -jar safetrail-backend.jar
```

### Frontend → Firebase Hosting
```bash
npm install -g firebase-tools
firebase login
firebase init hosting
firebase deploy
```

### Database → AWS RDS (MySQL)
- Create RDS MySQL instance
- Update `spring.datasource.url` with RDS endpoint

---

## 🛠️ Tech Stack Summary

| Layer | Technology |
|-------|-----------|
| Frontend | HTML + CSS + JavaScript |
| Backend | Java 17 + Spring Boot 3 |
| Database | MySQL 8.0 |
| Auth | JWT + Spring Security |
| SMS | Twilio API |
| Email | Gmail SMTP |
| GPS | Google Maps API (frontend) |
| AI | Java-based anomaly detection |
| Deployment | AWS EC2 + RDS + Firebase |

---

Made with ❤️ for tourist safety.
