package com.tsms.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * User Entity — stores login credentials
 * Maps to the "users" table in MySQL
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;   // stored as plain text for simplicity (mini project)

    private String fullName;
}
