package com.trmo.floracare.dto;

import com.trmo.floracare.entities.Device;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RoomDTO {
    private UUID id;
    private String name;
    private String image;
    private LocalDateTime createdAt;
    private List<PlantViewDTO> plants;
    private List<Device> devices;
}
