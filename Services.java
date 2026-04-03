// ══════════════════════════════════════════════════════════════
// AlertService.java - Core alert logic with SMS
// ══════════════════════════════════════════════════════════════
package com.safetrail.service;

import com.safetrail.model.Alert;
import com.safetrail.model.Tourist;
import com.safetrail.repository.AlertRepository;
import com.safetrail.repository.TouristRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    @Autowired private AlertRepository alertRepo;
    @Autowired private TouristRepository touristRepo;
    @Autowired private JavaMailSender mailSender;

    @Value("${twilio.account.sid}") private String twilioSid;
    @Value("${twilio.auth.token}") private String twilioToken;
    @Value("${twilio.phone.number}") private String twilioPhone;
    @Value("${admin.email}") private String adminEmail;
    @Value("${emergency.phone}") private String emergencyPhone;

    // ── SOS Trigger ──
    public void triggerSOS(Long touristId, Double lat, Double lng) {
        Tourist tourist = touristRepo.findById(touristId)
            .orElseThrow(() -> new RuntimeException("Tourist not found"));

        // Create alert in DB
        Alert alert = new Alert();
        alert.setTourist(tourist);
        alert.setType(Alert.AlertType.SOS);
        alert.setStatus(Alert.AlertStatus.ACTIVE);
        alert.setLatitude(lat);
        alert.setLongitude(lng);
        alert.setZoneName(tourist.getCurrentZone());
        alert.setMessage("SOS triggered by " + tourist.getName() + " at " + lat + ", " + lng);
        alertRepo.save(alert);

        // Update tourist status
        tourist.setStatus(Tourist.TouristStatus.SOS);
        touristRepo.save(tourist);

        // Send SMS to emergency contact
        sendSMS(tourist.getEmergencyContact(),
            "🆘 SOS ALERT: " + tourist.getName() +
            " needs help at GPS: " + lat + ", " + lng +
            ". Zone: " + tourist.getCurrentZone() +
            ". Please contact authorities: 100");

        // Send SMS to admin
        sendSMS(emergencyPhone,
            "SafeTrail SOS: Tourist " + tourist.getName() +
            " (" + tourist.getPhone() + ") triggered SOS at " +
            lat + ", " + lng + ". Zone: " + tourist.getCurrentZone());

        // Send email to admin
        sendEmail(adminEmail,
            "🆘 SOS ALERT - SafeTrail",
            "Tourist: " + tourist.getName() + "\n" +
            "Phone: " + tourist.getPhone() + "\n" +
            "Location: " + lat + ", " + lng + "\n" +
            "Zone: " + tourist.getCurrentZone() + "\n" +
            "Emergency Contact: " + tourist.getEmergencyContact() + "\n" +
            "Time: " + LocalDateTime.now() + "\n\n" +
            "Please dispatch help immediately!"
        );
    }

    // ── Zone Violation Alert ──
    public void triggerZoneAlert(Tourist tourist, String zoneName, String riskLevel) {
        Alert alert = new Alert();
        alert.setTourist(tourist);
        alert.setType(Alert.AlertType.ZONE_VIOLATION);
        alert.setStatus(Alert.AlertStatus.OPEN);
        alert.setZoneName(zoneName);
        alert.setLatitude(tourist.getLatitude());
        alert.setLongitude(tourist.getLongitude());
        alert.setMessage(tourist.getName() + " entered " + riskLevel + " zone: " + zoneName);
        alertRepo.save(alert);

        // SMS warning to tourist
        sendSMS(tourist.getPhone(),
            "⚠️ SafeTrail Warning: You have entered a " + riskLevel +
            " zone (" + zoneName + "). Please be careful and follow safety guidelines.");
    }

    // ── Low Battery Alert ──
    public void triggerLowBatteryAlert(Tourist tourist) {
        Alert alert = new Alert();
        alert.setTourist(tourist);
        alert.setType(Alert.AlertType.LOW_BATTERY);
        alert.setStatus(Alert.AlertStatus.OPEN);
        alert.setMessage("Low battery: " + tourist.getBatteryLevel() + "% - " + tourist.getName());
        alertRepo.save(alert);

        sendSMS(tourist.getPhone(),
            "🔋 SafeTrail: Your device battery is at " + tourist.getBatteryLevel() +
            "%. Please charge soon to stay connected with safety monitoring.");
    }

    // ── Resolve Alert ──
    public void resolveAlert(Long alertId, String resolvedBy) {
        Alert alert = alertRepo.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setStatus(Alert.AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(resolvedBy);
        alertRepo.save(alert);

        // Update tourist status back to ACTIVE
        Tourist tourist = alert.getTourist();
        if (tourist.getStatus() == Tourist.TouristStatus.SOS) {
            tourist.setStatus(Tourist.TouristStatus.ACTIVE);
            touristRepo.save(tourist);
        }
    }

    public List<Alert> getActiveAlerts() {
        return alertRepo.findByStatus(Alert.AlertStatus.ACTIVE);
    }

    public List<Alert> getAllAlerts() {
        return alertRepo.findAllByOrderByTriggeredAtDesc();
    }

    public List<Alert> getAlertsByTourist(Long touristId) {
        return alertRepo.findByTouristIdOrderByTriggeredAtDesc(touristId);
    }

    // ── SMS Helper (Twilio) ──
    private void sendSMS(String toPhone, String message) {
        try {
            Twilio.init(twilioSid, twilioToken);
            Message.creator(new PhoneNumber(toPhone), new PhoneNumber(twilioPhone), message).create();
        } catch (Exception e) {
            System.err.println("SMS failed to " + toPhone + ": " + e.getMessage());
        }
    }

    // ── Email Helper ──
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
        }
    }
}


// ══════════════════════════════════════════════════════════════
// LocationService.java - GPS tracking + zone check + AI anomaly
// ══════════════════════════════════════════════════════════════
package com.safetrail.service;

import com.safetrail.model.LocationHistory;
import com.safetrail.model.Tourist;
import com.safetrail.model.Zone;
import com.safetrail.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LocationService {

    @Autowired private TouristRepository touristRepo;
    @Autowired private LocationHistoryRepository locationHistoryRepo;
    @Autowired private ZoneService zoneService;
    @Autowired private AlertService alertService;

    // Called every 30 seconds from tourist's device
    public void updateLocation(Long touristId, Double lat, Double lng, Integer battery) {
        Tourist tourist = touristRepo.findById(touristId)
            .orElseThrow(() -> new RuntimeException("Tourist not found"));

        // Update current location
        tourist.setLatitude(lat);
        tourist.setLongitude(lng);
        tourist.setBatteryLevel(battery);
        tourist.setLastSeen(LocalDateTime.now());

        // Save location history
        LocationHistory history = new LocationHistory();
        history.setTouristId(touristId);
        history.setLatitude(lat);
        history.setLongitude(lng);
        history.setTimestamp(LocalDateTime.now());
        locationHistoryRepo.save(history);

        // Check zone boundaries
        Zone zone = zoneService.getZoneForLocation(lat, lng);
        if (zone != null) {
            tourist.setCurrentZone(zone.getName());
            // Fire alert if in warning or danger zone
            if (zone.getRiskLevel() != Zone.RiskLevel.SAFE) {
                alertService.triggerZoneAlert(tourist, zone.getName(), zone.getRiskLevel().name());
            }
        }

        // Low battery check
        if (battery != null && battery <= 20) {
            alertService.triggerLowBatteryAlert(tourist);
        }

        touristRepo.save(tourist);

        // AI Anomaly Detection
        detectAnomalies(tourist, lat, lng, history);
    }

    // Simple anomaly detection: sudden large movement (teleport detection)
    private void detectAnomalies(Tourist tourist, Double lat, Double lng, LocationHistory current) {
        List<LocationHistory> recent = locationHistoryRepo
            .findTop10ByTouristIdOrderByTimestampDesc(tourist.getId());

        if (recent.size() >= 2) {
            LocationHistory prev = recent.get(1);
            double distance = calculateDistance(prev.getLatitude(), prev.getLongitude(), lat, lng);
            long timeDiff = java.time.Duration.between(prev.getTimestamp(), current.getTimestamp()).getSeconds();

            // Speed in m/s — over 30 m/s (108 km/h on foot) = anomaly
            if (timeDiff > 0 && (distance / timeDiff) > 30) {
                System.out.println("⚠️ Anomaly detected for " + tourist.getName() +
                    ": Speed " + (distance/timeDiff) + " m/s");
                // Create unusual movement alert
                alertService.triggerZoneAlert(tourist, tourist.getCurrentZone(), "ANOMALY");
            }
        }
    }

    // Haversine formula for GPS distance in meters
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                   Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    public List<Tourist> getAllLiveLocations() {
        return touristRepo.findAll();
    }

    public List<LocationHistory> getLocationHistory(Long touristId) {
        return locationHistoryRepo.findByTouristIdOrderByTimestampDesc(touristId);
    }
}
