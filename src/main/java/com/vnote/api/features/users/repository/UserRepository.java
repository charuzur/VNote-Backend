package com.vnote.api.features.users.repository;

import com.vnote.api.features.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPasswordHash(String username, String password);
}