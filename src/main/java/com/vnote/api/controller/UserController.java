package com.vnote.api.controller;

import com.vnote.api.model.User;
import com.vnote.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1") // Matches SDD Base URL
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // SDD: POST /api/v1/auth/register
    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username already exists!");
        }
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    // SDD: POST /api/v1/auth/login
    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password"); // Note: In real app, use BCrypt

        Optional<User> user = userRepository.findByUsernameAndPasswordHash(username, password);

        if (user.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            // We return ID here so the frontend knows which profile to load
            response.put("userId", user.get().getUserId());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
    }

    // SDD: GET /api/v1/users/profile (We use /{id} for simplicity without JWT)
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(404).body("User not found");
    }

    // SDD: PUT /api/v1/users/profile
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setFullName(userDetails.getFullName());
            existingUser.setUsername(userDetails.getUsername());
            userRepository.save(existingUser);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
        }
        return ResponseEntity.status(404).body("User not found");
    }

    // Feature: Edit Password
    @PutMapping("/users/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwords) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPasswordHash(passwords.get("newPassword"));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        }
        return ResponseEntity.status(404).body("User not found");
    }

    // Feature: Upload Photo (New Requirement)
    @PostMapping("/users/{id}/photo")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setProfileImage(file.getBytes());
                userRepository.save(user);
                return ResponseEntity.ok(Map.of(
                        "message", "Photo uploaded successfully",
                        "fileReference", "/api/v1/users/" + id
                ));
            }
            return ResponseEntity.status(404).body("User not found");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file");
        }
    }
}