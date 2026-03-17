package com.tsms.repository;

import com.tsms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * UserRepository — handles database operations for User login
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username for login validation
    Optional<User> findByUsername(String username);
}
