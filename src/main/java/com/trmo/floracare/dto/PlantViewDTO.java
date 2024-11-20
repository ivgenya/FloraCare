package com.trmo.floracare.dto;

import com.trmo.floracare.entities.Schedule;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PlantViewDTO {
    private UUID id;
    private String name;
    private Integer minTemperature;
    private Integer maxTemperature;
    private Integer minHumidity;
    private Integer maxHumidity;
    private Integer wateringFrequency;
    private Integer fertilizingFrequency;
    private String description;
    private List<PlantPhotoDTO> photos;
    private List<Schedule> schedules;
}
