package com.banking.internetbanking.service;

import com.banking.internetbanking.repository.UserRepository;
import com.banking.internetbanking.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(String username, String email, String password,
            String firstName, String lastName, String phoneNumber) {
        User user = new User(
                null, username, email, passwordEncoder.encode(password),
                firstName, lastName, phoneNumber, true, false, false,
                null, null, LocalDateTime.now(), LocalDateTime.now());
        return userRepository.save(user);
    }

    public boolean updateUser(User user) {
        return userRepository.save(user) != null;
    }

    public boolean deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }

    public boolean authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() &&
                user.get().isEnabled() &&
                !user.get().isLocked() &&
                passwordEncoder.matches(password, user.get().getPasswordHash());
    }

    public void updateLastLogin(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User updatedUser = new User(
                    user.get().getId(), user.get().getUsername(), user.get().getEmail(),
                    user.get().getPasswordHash(), user.get().getFirstName(), user.get().getLastName(),
                    user.get().getPhoneNumber(), user.get().isEnabled(), user.get().isLocked(),
                    user.get().isMfaEnabled(), user.get().getMfaSecret(), LocalDateTime.now(),
                    user.get().getCreatedAt(), LocalDateTime.now());
            userRepository.save(updatedUser);
        }
    }
}
