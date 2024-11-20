package com.trmo.floracare.controllers;

import com.trmo.floracare.dto.RoomDTO;
import com.trmo.floracare.entities.Room;
import com.trmo.floracare.entities.User;
import com.trmo.floracare.mapper.PlantPhotoMapper;
import com.trmo.floracare.mapper.RoomMapper;
import com.trmo.floracare.services.RoomService;
import com.trmo.floracare.services.UserService;
import com.trmo.floracare.services.impl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Value("${path.uploads}")
    private String uploadDir;
    @Autowired
    private RoomService service;
    @Autowired
    private RoomMapper mapper;
    @Autowired
    private PlantPhotoMapper photoMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<?> addRoom(@RequestHeader("Authorization") String authorizationHeader,
                                     @RequestBody RoomDTO roomRequest) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String userId = jwtService.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        Optional<User> optionalUser = userService.findById(UUID.fromString(userId));

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();
        Room room = mapper.map(roomRequest);
        room.setUser(user);

        if (roomRequest.getImage() != null && !roomRequest.getImage().isEmpty()) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(roomRequest.getImage());
                String imageUrl = saveImage(imageBytes);
                room.setImage(imageUrl);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image: " + e.getMessage());
            }
        }
        service.save(room);
        return ResponseEntity.status(HttpStatus.CREATED).body("Room created successfully");
    }

    private String saveImage(byte[] imageBytes) throws IOException {
        String imageName = UUID.randomUUID() + ".jpg";
        Path imagePath = Paths.get(uploadDir, imageName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, imageBytes);
        return "/uploads/" + imageName;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getUserRooms(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token missing or invalid");
        }
        String token = authorizationHeader.substring(7);
        String userId = jwtService.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        Optional<User> optionalUser = userService.findById(UUID.fromString(userId));

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();
        List<RoomDTO> userRooms = service.findByUser(user)
                .stream()
                .map(mapper::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userRooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomDetails(@RequestHeader("Authorization") String authorizationHeader, @PathVariable UUID roomId) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token missing or invalid");
        }
        String token = authorizationHeader.substring(7);
        String userId = jwtService.getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        Optional<Room> roomOpt = service.findById(roomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }

        Room room = roomOpt.get();
        RoomDTO roomDTO = mapper.mapToDTO(room);

        return ResponseEntity.ok(roomDTO);
    }

    @PostMapping(value = "")
    @ResponseBody
    public List<RoomDTO> findAll() {
        return service.getAll()
                .stream()
                .map(mapper::mapToDTO)
                .collect(Collectors.toList());
    }
}
