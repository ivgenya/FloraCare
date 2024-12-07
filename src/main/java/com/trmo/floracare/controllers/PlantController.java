package com.trmo.floracare.controllers;

import com.trmo.floracare.dto.PlantDTO;
import com.trmo.floracare.dto.PlantPhotoDTO;
import com.trmo.floracare.entities.Plant;
import com.trmo.floracare.entities.PlantPhoto;
import com.trmo.floracare.mapper.PlantMapper;
import com.trmo.floracare.services.PlantService;
import com.trmo.floracare.services.impl.JwtService;
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
import java.time.LocalDate;
import java.util.*;

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
    public ResponseEntity<?> addPlant(@RequestBody PlantDTO plant) {
        try {
            if (plant.getPhotos() != null && !plant.getPhotos().isEmpty()) {
                for (PlantPhotoDTO photoDTO : plant.getPhotos()) {
                    if (photoDTO.getImage() != null && !photoDTO.getImage().isEmpty()) {
                        try {
                            byte[] imageBytes = Base64.getDecoder().decode(photoDTO.getImage());
                            String imageUrl = saveImage(imageBytes);
                            photoDTO.setImage(imageUrl);
                            photoDTO.setMain(true);
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

    @PostMapping("/add-photo")
    public ResponseEntity<?> addPlantPhoto(@RequestBody PlantPhotoDTO dto) {
        Optional<Plant> plantOpt = plantService.findById(dto.getPlantId());
        if (plantOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plant not found");
        }
        Plant plant = plantOpt.get();
        try {
            byte[] imageBytes = Base64.getDecoder().decode(dto.getImage());
            String imageUrl = saveImage(imageBytes);

            PlantPhoto photo = new PlantPhoto();
            photo.setImage(imageUrl);
            photo.setPlant(plant);
            plant.getPhotos().add(photo);
            plantService.update(plant);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving image: " + e.getMessage());
        }
        PlantDTO plantDTO = mapper.mapToDTO(plant);
        return ResponseEntity.ok(plantDTO);
    }


    @GetMapping("/{plantId}")
    public ResponseEntity<?> getPlantDetails(@PathVariable UUID plantId) {
        Optional<Plant> plantOpt = plantService.findById(plantId);
        if (plantOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }
        Plant plant = plantOpt.get();
        PlantDTO plantDTO = mapper.mapToDTO(plant);
        return ResponseEntity.ok(plantDTO);
    }

    @GetMapping("/watering-schedule")
    public ResponseEntity<Map<LocalDate, List<String>>> getWateringSchedule() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();
        Map<LocalDate, List<String>> schedule = plantService.getWateringScheduleForUser(userId);
        return ResponseEntity.ok(schedule);
    }

    @PostMapping("/mark-watering")
    public ResponseEntity<?> markWatering(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();

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

    @DeleteMapping("/{plantId}")
    public ResponseEntity<?> deletePlant(@PathVariable UUID plantId) {
        try {
            plantService.delete(plantId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Room deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the room");
        }
    }

    private String saveImage(byte[] imageBytes) throws IOException {
        String imageName = UUID.randomUUID() + ".jpg";
        Path imagePath = Paths.get(plantUploadDir, imageName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, imageBytes);
        return "/plant-uploads/" + imageName;
    }
}
