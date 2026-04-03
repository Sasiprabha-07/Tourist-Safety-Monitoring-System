-- ============================================================
-- SafeTrail Database Schema
-- MySQL 8.0+
-- Run this script to set up the complete database
-- ============================================================

CREATE DATABASE IF NOT EXISTS safetrail_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE safetrail_db;

-- ── Users / Auth ──────────────────────────────────────────────
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('TOURIST','ADMIN','POLICE') NOT NULL DEFAULT 'TOURIST',
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ── Tourists ──────────────────────────────────────────────────
CREATE TABLE tourists (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT,
    name                VARCHAR(100) NOT NULL,
    email               VARCHAR(100) NOT NULL UNIQUE,
    phone               VARCHAR(20),
    nationality         VARCHAR(50),
    emergency_contact   VARCHAR(20),
    blood_group         VARCHAR(5),
    medical_notes       TEXT,
    -- Location
    latitude            DECIMAL(10, 8),
    longitude           DECIMAL(11, 8),
    current_zone        VARCHAR(100),
    -- Status
    status              ENUM('ACTIVE','OFFLINE','SOS','WARNING') DEFAULT 'ACTIVE',
    battery_level       INT,
    last_seen           DATETIME,
    registered_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ── Safety Zones ──────────────────────────────────────────────
CREATE TABLE zones (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    latitude        DECIMAL(10, 8) NOT NULL,
    longitude       DECIMAL(11, 8) NOT NULL,
    radius_meters   DECIMAL(10, 2) DEFAULT 500,
    max_capacity    INT DEFAULT 100,
    risk_level      ENUM('SAFE','WARNING','DANGER') DEFAULT 'SAFE',
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- ── Alerts ────────────────────────────────────────────────────
CREATE TABLE alerts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tourist_id      BIGINT NOT NULL,
    type            ENUM('SOS','ZONE_VIOLATION','UNUSUAL_MOVEMENT','LOW_BATTERY','ZONE_ENTRY') NOT NULL,
    status          ENUM('ACTIVE','OPEN','RESOLVED') DEFAULT 'OPEN',
    latitude        DECIMAL(10, 8),
    longitude       DECIMAL(11, 8),
    zone_name       VARCHAR(100),
    message         TEXT,
    triggered_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved_at     DATETIME NULL,
    resolved_by     VARCHAR(100) NULL,
    FOREIGN KEY (tourist_id) REFERENCES tourists(id) ON DELETE CASCADE
);

-- ── Location History ──────────────────────────────────────────
CREATE TABLE location_history (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tourist_id      BIGINT NOT NULL,
    latitude        DECIMAL(10, 8) NOT NULL,
    longitude       DECIMAL(11, 8) NOT NULL,
    accuracy_m      DECIMAL(8, 2),
    battery_level   INT,
    zone_name       VARCHAR(100),
    timestamp       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tourist_id) REFERENCES tourists(id) ON DELETE CASCADE,
    INDEX idx_tourist_time (tourist_id, timestamp)
);

-- ── SMS / Email Logs ──────────────────────────────────────────
CREATE TABLE notification_logs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_id        BIGINT,
    type            ENUM('SMS','EMAIL') NOT NULL,
    recipient       VARCHAR(100) NOT NULL,
    message         TEXT,
    status          ENUM('SENT','FAILED') DEFAULT 'SENT',
    sent_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (alert_id) REFERENCES alerts(id) ON DELETE SET NULL
);

-- ── Check-ins ─────────────────────────────────────────────────
CREATE TABLE checkins (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tourist_id      BIGINT NOT NULL,
    latitude        DECIMAL(10, 8),
    longitude       DECIMAL(11, 8),
    zone_name       VARCHAR(100),
    note            VARCHAR(255),
    checked_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tourist_id) REFERENCES tourists(id) ON DELETE CASCADE
);

-- ============================================================
-- Seed Data – Default Zones (Chennai)
-- ============================================================
INSERT INTO zones (name, latitude, longitude, radius_meters, max_capacity, risk_level) VALUES
('Marina Beach',             13.0567, 80.2832, 800,  500, 'SAFE'),
('Kapaleeshwarar Temple',    13.0337, 80.2699, 300,  200, 'SAFE'),
('Fort St. George',          13.0802, 80.2885, 400,  150, 'SAFE'),
('Old Market Area',          13.0822, 80.2785, 500,  100, 'WARNING'),
('Industrial Port',          13.0950, 80.2927, 1200,  50, 'DANGER'),
('Santhome Cathedral',       13.0338, 80.2786, 250,  100, 'SAFE'),
('Elliot Beach',             12.9990, 80.2714, 600,  300, 'SAFE');

-- ============================================================
-- Seed Data – Admin & Demo Users
-- ============================================================
-- Password: admin1234 (BCrypt)
INSERT INTO users (email, password, role) VALUES
('admin@safetrail.com', '$2a$10$xN3/gF9kL2mP8vQ5sT1oXuRbC6dIhJwY4eA0EzMlV7nKpOqBfWiDs', 'ADMIN'),
('police@safetrail.com','$2a$10$xN3/gF9kL2mP8vQ5sT1oXuRbC6dIhJwY4eA0EzMlV7nKpOqBfWiDs', 'POLICE'),
('tourist@demo.com',    '$2a$10$xN3/gF9kL2mP8vQ5sT1oXuRbC6dIhJwY4eA0EzMlV7nKpOqBfWiDs', 'TOURIST');

-- ============================================================
-- Useful Queries for Admin Dashboard
-- ============================================================

-- Active tourists with their current zone and status
-- SELECT t.name, t.phone, t.current_zone, t.status, t.battery_level, t.last_seen
-- FROM tourists t WHERE t.status != 'OFFLINE' ORDER BY t.last_seen DESC;

-- All active/open alerts
-- SELECT a.id, t.name, t.phone, a.type, a.zone_name, a.triggered_at, a.status
-- FROM alerts a JOIN tourists t ON a.tourist_id = t.id
-- WHERE a.status != 'RESOLVED' ORDER BY a.triggered_at DESC;

-- Alert count per day (last 7 days)
-- SELECT DATE(triggered_at) as day, COUNT(*) as total
-- FROM alerts
-- WHERE triggered_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
-- GROUP BY DATE(triggered_at) ORDER BY day;

-- Tourists in danger zones
-- SELECT t.name, t.phone, t.emergency_contact, t.latitude, t.longitude, t.current_zone
-- FROM tourists t
-- JOIN zones z ON t.current_zone = z.name
-- WHERE z.risk_level = 'DANGER';
