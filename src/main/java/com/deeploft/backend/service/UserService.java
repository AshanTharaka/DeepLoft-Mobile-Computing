package com.deeploft.backend.service;

import com.deeploft.backend.model.User;
import com.deeploft.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return Optional.empty();
        }
        return Optional.of(userRepository.save(user));
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