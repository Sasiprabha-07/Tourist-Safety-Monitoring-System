package com.tsms.service;

import com.tsms.dto.Dto;
import com.tsms.model.Tourist;
import com.tsms.repository.TouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * TouristService — Business logic layer for tourist operations
 * Called by TouristController to perform CRUD operations
 */
@Service
@RequiredArgsConstructor
public class TouristService {

    private final TouristRepository touristRepository;

    // ---- Get all tourists ----
    public List<Tourist> getAllTourists() {
        return touristRepository.findAll();
    }

    // ---- Get tourist by ID ----
    public Tourist getTouristById(Long id) {
        return touristRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tourist not found with id: " + id));
    }

    // ---- Add new tourist ----
    public Tourist addTourist(Dto.TouristRequest req) {
        // Validate status value
        validateStatus(req.getStatus());

        Tourist tourist = Tourist.builder()
                .name(req.getName())
                .location(req.getLocation())
                .status(req.getStatus())
                .build();

        return touristRepository.save(tourist);
    }

    // ---- Update tourist (full or partial) ----
    public Tourist updateTourist(Long id, Dto.TouristRequest req) {
        Tourist existing = getTouristById(id);

        // Update only non-null fields
        if (req.getName() != null && !req.getName().isEmpty()) {
            existing.setName(req.getName());
        }
        if (req.getLocation() != null && !req.getLocation().isEmpty()) {
            existing.setLocation(req.getLocation());
        }
        if (req.getStatus() != null && !req.getStatus().isEmpty()) {
            validateStatus(req.getStatus());
            existing.setStatus(req.getStatus());
        }

        return touristRepository.save(existing);
    }

    // ---- Delete tourist ----
    public void deleteTourist(Long id) {
        if (!touristRepository.existsById(id)) {
            throw new RuntimeException("Tourist not found with id: " + id);
        }
        touristRepository.deleteById(id);
    }

    // ---- Helper: Validate status values ----
    private void validateStatus(String status) {
        if (status != null &&
            !status.equals("Safe") &&
            !status.equals("In Danger") &&
            !status.equals("Missing")) {
            throw new RuntimeException("Invalid status. Must be: Safe, In Danger, or Missing");
        }
    }
}
