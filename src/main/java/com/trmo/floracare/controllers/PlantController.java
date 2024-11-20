package com.trmo.floracare.controllers;

import com.trmo.floracare.dto.PlantDTO;
import com.trmo.floracare.dto.PlantPhotoDTO;
import com.trmo.floracare.entities.Plant;
import com.trmo.floracare.mapper.PlantMapper;
import com.trmo.floracare.services.PlantService;
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
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/plant")
public class PlantController {
    @Autowired
    private PlantService plantService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PlantMapper mapper;

    @Value("${path.plant-uploads}")
    private String plantUploadDir;

    @PostMapping("/add")
    public ResponseEntity<?> addPlant(@RequestHeader("Authorization") String authorizationHeader, @RequestBody PlantDTO plant) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token missing or invalid");
        }

        String token = authorizationHeader.substring(7);

        String userId = jwtService.getUserIdFromToken(token);
        try {
            if (plant.getPhotos() != null && !plant.getPhotos().isEmpty()) {
                for (PlantPhotoDTO photoDTO : plant.getPhotos()) {
                    if (photoDTO.getImage() != null && !photoDTO.getImage().isEmpty()) {
                        try {
                            byte[] imageBytes = Base64.getDecoder().decode(photoDTO.getImage());
                            String imageUrl = saveImage(imageBytes);
                            photoDTO.setImage(imageUrl);
                        } catch (IOException e) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Error saving image: " + e.getMessage());
                        }
                    }
                }
            }
            Plant createdPlant = plantService.save(mapper.map(plant));
            return new ResponseEntity<>(createdPlant, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    private String saveImage(byte[] imageBytes) throws IOException {
        String imageName = UUID.randomUUID() + ".jpg";
        Path imagePath = Paths.get(plantUploadDir, imageName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, imageBytes);
        return "/plant-uploads/" + imageName;
    }

    @GetMapping("/watering-schedule")
    public ResponseEntity<Map<LocalDate, List<String>>> getWateringSchedule(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String token = authorizationHeader.substring(7);
        String userId = jwtService.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Map<LocalDate, List<String>> schedule = plantService.getWateringScheduleForUser(userId);
        return ResponseEntity.ok(schedule);
    }

    @PostMapping("/mark-watering")
    public ResponseEntity<?> markWatering(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Map<String, String> request) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token missing or invalid");
        }

        String token = authorizationHeader.substring(7);
        String userId = jwtService.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user.");
        }

        String dateStr = request.get("date");
        if (dateStr == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Date is required.");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format.");
        }

        try {
            plantService.markWateringForDate(userId, date);
            return ResponseEntity.ok("Watering marked for all plants.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error marking watering: " + e.getMessage());
        }
    }
}
