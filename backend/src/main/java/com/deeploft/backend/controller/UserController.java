package com.deeploft.backend.controller;

import com.deeploft.backend.model.User;
import com.deeploft.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsersByRole(@RequestParam String role) {
        return userService.getUsersByRole(role);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        if (user.getRole() == null) user.setRole("STUDENT");
        Optional<User> registeredUser = userService.registerUser(user);
        if (registeredUser.isPresent()) {
            return ResponseEntity.ok(registeredUser.get());
        } else {
            return ResponseEntity.badRequest().body("Email already in use");
        }
    }

    @PostMapping("/upload-profile-pic")
    public ResponseEntity<?> uploadProfilePic(@RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
        String url = userService.saveProfilePic(file, email);
        if (url != null) {
            return ResponseEntity.ok(Map.of("url", url));
        } else {
            return ResponseEntity.badRequest().body("Upload failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        Optional<User> user = userService.loginUser(loginUser.getEmail(), loginUser.getPassword());
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {
        Optional<User> user = userService.updateProfile(updatedUser);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok().build();
    }
}