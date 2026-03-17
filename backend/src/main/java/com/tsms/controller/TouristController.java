package com.tsms.controller;

import com.tsms.dto.Dto;
import com.tsms.model.Tourist;
import com.tsms.service.TouristService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * TouristController — REST API endpoints for tourist CRUD operations
 *
 * GET    /api/tourists        → Get all tourists
 * POST   /api/tourists        → Add new tourist
 * PUT    /api/tourists/{id}   → Update tourist
 * DELETE /api/tourists/{id}   → Delete tourist
 */
@RestController
@RequestMapping("/api/tourists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")   // Allow frontend (any origin) to call this API
public class TouristController {

    private final TouristService touristService;

    // ---- GET ALL TOURISTS ----
    @GetMapping
    public ResponseEntity<List<Tourist>> getAllTourists() {
        List<Tourist> tourists = touristService.getAllTourists();
        return ResponseEntity.ok(tourists);
    }

    // ---- GET TOURIST BY ID ----
    @GetMapping("/{id}")
    public ResponseEntity<Tourist> getTouristById(@PathVariable Long id) {
        Tourist tourist = touristService.getTouristById(id);
        return ResponseEntity.ok(tourist);
    }

    // ---- ADD TOURIST ----
    @PostMapping
    public ResponseEntity<Dto.ApiResponse> addTourist(@RequestBody Dto.TouristRequest request) {
        Tourist saved = touristService.addTourist(request);
        return ResponseEntity.ok(
            Dto.ApiResponse.builder()
                .success(true)
                .message("Tourist added successfully")
                .data(saved)
                .build()
        );
    }

    // ---- UPDATE TOURIST ----
    @PutMapping("/{id}")
    public ResponseEntity<Dto.ApiResponse> updateTourist(
            @PathVariable Long id,
            @RequestBody Dto.TouristRequest request) {
        Tourist updated = touristService.updateTourist(id, request);
        return ResponseEntity.ok(
            Dto.ApiResponse.builder()
                .success(true)
                .message("Tourist updated successfully")
                .data(updated)
                .build()
        );
    }

    // ---- DELETE TOURIST ----
    @DeleteMapping("/{id}")
    public ResponseEntity<Dto.ApiResponse> deleteTourist(@PathVariable Long id) {
        touristService.deleteTourist(id);
        return ResponseEntity.ok(
            Dto.ApiResponse.builder()
                .success(true)
                .message("Tourist deleted successfully")
                .build()
        );
    }
}
