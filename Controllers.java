// ══════════════════════════════════════════════════════════════
// TouristController.java
// ══════════════════════════════════════════════════════════════
package com.safetrail.controller;

import com.safetrail.model.Tourist;
import com.safetrail.service.TouristService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tourists")
@CrossOrigin(origins = "*")
public class TouristController {

    @Autowired
    private TouristService touristService;

    // Register new tourist
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Tourist tourist) {
        return ResponseEntity.ok(touristService.register(tourist));
    }

    // Get all tourists (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('POLICE')")
    public List<Tourist> getAllTourists() {
        return touristService.getAllTourists();
    }

    // Get tourist by ID
    @GetMapping("/{id}")
    public ResponseEntity<Tourist> getTourist(@PathVariable Long id) {
        return ResponseEntity.ok(touristService.getTouristById(id));
    }

    // Update tourist profile
    @PutMapping("/{id}")
    public ResponseEntity<Tourist> updateTourist(@PathVariable Long id, @RequestBody Tourist tourist) {
        return ResponseEntity.ok(touristService.updateTourist(id, tourist));
    }

    // Tourist Check-In (safe confirmation)
    @PostMapping("/{id}/checkin")
    public ResponseEntity<?> checkIn(@PathVariable Long id) {
        touristService.checkIn(id);
        return ResponseEntity.ok("Check-in recorded. Stay safe!");
    }

    // Get tourists in a specific zone
    @GetMapping("/zone/{zoneName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('POLICE')")
    public List<Tourist> getTouristsByZone(@PathVariable String zoneName) {
        return touristService.getTouristsByZone(zoneName);
    }
}


// ══════════════════════════════════════════════════════════════
// LocationController.java
// ══════════════════════════════════════════════════════════════
package com.safetrail.controller;

import com.safetrail.dto.LocationUpdateDTO;
import com.safetrail.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // Tourist updates their GPS location (called every 30 seconds from app)
    @PostMapping("/update")
    public ResponseEntity<?> updateLocation(@RequestBody LocationUpdateDTO dto) {
        locationService.updateLocation(
            dto.getTouristId(),
            dto.getLatitude(),
            dto.getLongitude(),
            dto.getBatteryLevel()
        );
        return ResponseEntity.ok("Location updated");
    }

    // Get live location of all tourists (Admin/Map view)
    @GetMapping("/live")
    public ResponseEntity<?> getLiveLocations() {
        return ResponseEntity.ok(locationService.getAllLiveLocations());
    }

    // Get location history of a tourist
    @GetMapping("/history/{touristId}")
    public ResponseEntity<?> getLocationHistory(@PathVariable Long touristId) {
        return ResponseEntity.ok(locationService.getLocationHistory(touristId));
    }
}


// ══════════════════════════════════════════════════════════════
// AlertController.java
// ══════════════════════════════════════════════════════════════
package com.safetrail.controller;

import com.safetrail.model.Alert;
import com.safetrail.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertService alertService;

    // Tourist triggers SOS
    @PostMapping("/sos/{touristId}")
    public ResponseEntity<?> triggerSOS(@PathVariable Long touristId,
                                         @RequestParam Double lat,
                                         @RequestParam Double lng) {
        alertService.triggerSOS(touristId, lat, lng);
        return ResponseEntity.ok("SOS alert sent! Help is on the way.");
    }

    // Get all active alerts (Admin)
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('POLICE')")
    public List<Alert> getActiveAlerts() {
        return alertService.getActiveAlerts();
    }

    // Get all alerts
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('POLICE')")
    public List<Alert> getAllAlerts() {
        return alertService.getAllAlerts();
    }

    // Resolve an alert
    @PutMapping("/{alertId}/resolve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('POLICE')")
    public ResponseEntity<?> resolveAlert(@PathVariable Long alertId,
                                           @RequestParam String resolvedBy) {
        alertService.resolveAlert(alertId, resolvedBy);
        return ResponseEntity.ok("Alert resolved.");
    }

    // Get alerts for a specific tourist
    @GetMapping("/tourist/{touristId}")
    public List<Alert> getAlertsByTourist(@PathVariable Long touristId) {
        return alertService.getAlertsByTourist(touristId);
    }
}


// ══════════════════════════════════════════════════════════════
// ZoneController.java
// ══════════════════════════════════════════════════════════════
package com.safetrail.controller;

import com.safetrail.model.Zone;
import com.safetrail.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/zones")
@CrossOrigin(origins = "*")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @GetMapping
    public List<Zone> getAllZones() { return zoneService.getAllZones(); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Zone> createZone(@RequestBody Zone zone) {
        return ResponseEntity.ok(zoneService.createZone(zone));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Zone> updateZone(@PathVariable Long id, @RequestBody Zone zone) {
        return ResponseEntity.ok(zoneService.updateZone(id, zone));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.ok("Zone deleted.");
    }

    // Check if a GPS point is inside a danger/warning zone
    @GetMapping("/check")
    public ResponseEntity<?> checkZone(@RequestParam Double lat, @RequestParam Double lng) {
        return ResponseEntity.ok(zoneService.checkZoneStatus(lat, lng));
    }
}


// ══════════════════════════════════════════════════════════════
// AuthController.java - JWT Login
// ══════════════════════════════════════════════════════════════
package com.safetrail.controller;

import com.safetrail.dto.LoginRequest;
import com.safetrail.dto.LoginResponse;
import com.safetrail.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired AuthenticationManager authenticationManager;
    @Autowired UserDetailsService userDetailsService;
    @Autowired JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(token, userDetails.getUsername(),
            userDetails.getAuthorities().toString()));
    }
}
