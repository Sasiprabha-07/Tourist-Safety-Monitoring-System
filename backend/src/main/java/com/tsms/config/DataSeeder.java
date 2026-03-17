package com.tsms.config;

import com.tsms.model.Tourist;
import com.tsms.model.User;
import com.tsms.repository.TouristRepository;
import com.tsms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DataSeeder — Automatically inserts default data when the app starts
 * Creates a default admin user and 3 sample tourists if the tables are empty
 */
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final TouristRepository touristRepository;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {

            // --- Seed default admin user ---
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(User.builder()
                        .username("admin")
                        .password("admin123")
                        .fullName("System Admin")
                        .build());
                System.out.println("[TSMS] Default admin created: admin / admin123");
            }

            // --- Seed sample tourist records ---
            if (touristRepository.count() == 0) {
                touristRepository.save(Tourist.builder()
                        .name("Rahul Sharma").location("Manali, Himachal Pradesh").status("Safe").build());
                touristRepository.save(Tourist.builder()
                        .name("Emily Davis").location("Goa, India").status("In Danger").build());
                touristRepository.save(Tourist.builder()
                        .name("Chen Wei").location("Ooty, Tamil Nadu").status("Missing").build());
                touristRepository.save(Tourist.builder()
                        .name("Priya Nair").location("Munnar, Kerala").status("Safe").build());
                touristRepository.save(Tourist.builder()
                        .name("James Wilson").location("Jaisalmer, Rajasthan").status("Safe").build());
                System.out.println("[TSMS] Sample tourists inserted.");
            }
        };
    }
}
