package com.trmo.floracare.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.trmo.floracare.dto.LoginRequest;
import com.trmo.floracare.dto.RegisterRequest;
import com.trmo.floracare.entities.User;
import com.trmo.floracare.mapper.UserMapper;
import com.trmo.floracare.services.UserService;
import com.trmo.floracare.services.impl.GoogleTokenVerifierService;
import com.trmo.floracare.services.impl.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private GoogleTokenVerifierService googleTokenVerifier;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        userService.save(newUser);
        String jwt = jwtService.createToken(newUser.getId().toString());

        return ResponseEntity.ok(Collections.singletonMap("authToken", jwt));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
        String jwt = jwtService.createToken(user.getId().toString());
        return ResponseEntity.ok(Collections.singletonMap("authToken", jwt));
    }


    @PostMapping("/google")
    public ResponseEntity<?> googleSignIn(@RequestBody Map<String, String> payload) {
        String googleToken = payload.get("token");
        try {
            Optional<GoogleIdToken.Payload> optionalToken = googleTokenVerifier.verify(googleToken);
            if (optionalToken.isPresent()) {
                GoogleIdToken.Payload tokenPayload = optionalToken.get();
                String googleId = tokenPayload.getSubject();
                String email = tokenPayload.getEmail();
                String name = (String) tokenPayload.get("name");
                String avatarUrl = (String) tokenPayload.get("picture");

                User user = userService.findByGoogleId(googleId).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setGoogleId(googleId);
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setAvatarUrl(avatarUrl);
                    return userService.save(newUser);
                });
                String jwt = jwtService.createToken(user.getId().toString());
                return ResponseEntity.ok(Collections.singletonMap("authToken", jwt));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google Token");

        } catch (Exception e) {
            log.error(String.valueOf(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
        }
    }
}

