package com.trmo.floracare.services;

import com.trmo.floracare.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findById(UUID id);
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByEmail(String email);
    User save(User user);
}
