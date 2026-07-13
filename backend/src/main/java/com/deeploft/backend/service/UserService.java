package com.deeploft.backend.service;

import com.deeploft.backend.model.User;
import com.deeploft.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final String UPLOAD_DIR = "uploads/profiles/";

    public Optional<User> registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return Optional.empty();
        }
        return Optional.of(userRepository.save(user));
    }

    public String saveProfilePic(MultipartFile file, String email) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            userRepository.findByEmail(email).ifPresent(user -> {
                user.setProfileImageUrl(fileName);
                userRepository.save(user);
            });

            return fileName;
        } catch (IOException e) {
            return null;
        }
    }

    public Optional<User> loginUser(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }

    public Optional<User> updateProfile(User updatedUser) {
        return userRepository.findByEmail(updatedUser.getEmail())
                .map(user -> {
                    user.setBankAccountNumber(updatedUser.getBankAccountNumber());
                    user.setBankName(updatedUser.getBankName());
                    return userRepository.save(user);
                });
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && user.getRole().equalsIgnoreCase(role))
                .collect(java.util.stream.Collectors.toList());
    }

    public void deleteUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> userRepository.delete(user));
    }
}