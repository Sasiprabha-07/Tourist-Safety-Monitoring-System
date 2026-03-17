package com.tsms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Tourist Entity — Maps to the "tourist" table in MySQL
 * Fields: id, name, location, status
 */
@Entity
@Table(name = "tourist")
@Data                   // Generates getters, setters, toString
@NoArgsConstructor      // No-args constructor
@AllArgsConstructor     // All-args constructor
@Builder                // Builder pattern
public class Tourist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    /**
     * Status values: "Safe" | "In Danger" | "Missing"
     */
    @Column(nullable = false)
    private String status;
}
