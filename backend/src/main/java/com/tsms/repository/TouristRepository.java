package com.tsms.repository;

import com.tsms.model.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * TouristRepository — handles all database operations for Tourist
 * Spring Data JPA auto-generates SQL for standard CRUD methods
 */
@Repository
public interface TouristRepository extends JpaRepository<Tourist, Long> {

    // Find tourists by status (e.g. "Safe", "In Danger", "Missing")
    List<Tourist> findByStatus(String status);

    // Find tourists whose name contains a keyword (case-insensitive)
    List<Tourist> findByNameContainingIgnoreCase(String name);
}
