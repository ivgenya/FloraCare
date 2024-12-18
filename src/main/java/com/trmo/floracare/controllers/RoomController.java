package com.trmo.floracare.controllers;

import com.trmo.floracare.dto.RoomDTO;
import com.trmo.floracare.entities.Room;
import com.trmo.floracare.entities.User;
import com.trmo.floracare.mapper.PlantPhotoMapper;
import com.trmo.floracare.mapper.RoomMapper;
import com.trmo.floracare.services.RoomService;
import com.trmo.floracare.services.UserService;
import com.trmo.floracare.services.impl.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room")
@Slf4j
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
    public ResponseEntity<?> addRoom(@RequestBody RoomDTO roomRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();

        Optional<User> userOpt = userService.findById(UUID.fromString(userId));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();
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
        return new ResponseEntity<>(service.save(room), HttpStatus.CREATED);
    }

    @PutMapping("/{roomId}/update-name")
    public ResponseEntity<?> updateRoomName(
            @PathVariable UUID roomId,
            @RequestBody Map<String, String> requestBody) {
        Optional<Room> roomOpt = service.findById(roomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }
        Room room = roomOpt.get();
        String newName = requestBody.get("name");
        if (newName == null || newName.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Room name is required");
        }
        room.setName(newName);
        service.save(room);
        return ResponseEntity.ok("Room name updated successfully");
    }


    private String saveImage(byte[] imageBytes) throws IOException {
        String imageName = UUID.randomUUID() + ".jpg";
        Path imagePath = Paths.get(uploadDir, imageName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, imageBytes);
        return "/uploads/" + imageName;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getUserRooms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();

        Optional<User> userOpt = userService.findById(UUID.fromString(userId));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();
        List<RoomDTO> userRooms = service.findByUser(user)
                .stream()
                .map(mapper::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userRooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomDetails(@PathVariable UUID roomId) {
        Optional<Room> roomOpt = service.findById(roomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }
        Room room = roomOpt.get();
        RoomDTO roomDTO = mapper.mapToDTO(room);
        return ResponseEntity.ok(roomDTO);
    }

    @PostMapping("/{roomId}/link-device")
    public ResponseEntity<?> linkDeviceToRoom(
            @PathVariable UUID roomId,
            @RequestBody Map<String, String> request) {
        String macAddress = request.get("macAddress");
        String name = request.get("name");

        if (macAddress == null || macAddress.isEmpty()) {
            return ResponseEntity.badRequest().body("MAC address is required.");
        }
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required.");
        }

        try {
            Room updatedRoom = service.linkDeviceToRoom(roomId, macAddress, name);
            return ResponseEntity.ok(updatedRoom);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable UUID roomId) {
        try {
            service.delete(roomId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Room deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the room");
        }
    }

    @DeleteMapping("/{roomId}/device/{deviceId}")
    public ResponseEntity<?> deleteDeviceFromRoom(
            @PathVariable UUID roomId,
            @PathVariable UUID deviceId) {
        try {
            log.info(String.valueOf(roomId));
            log.info(String.valueOf(deviceId));
            Optional<Room> roomOpt = service.findById(roomId);
            log.info(String.valueOf(roomOpt.isEmpty()));
            if (roomOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found in the specified room");
            }
            Room room = roomOpt.get();
            log.info(room.getName());
            room.getDevices().removeIf(device -> device.getId().equals(deviceId));
            service.save(room);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Device deleted from room successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room or device not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the device from the room");
        }
    }
}
