package com.trmo.floracare.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PlantPhotoDTO {
    private String image;
    private UUID plantId;
}
