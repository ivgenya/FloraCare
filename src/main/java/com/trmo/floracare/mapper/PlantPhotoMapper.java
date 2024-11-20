package com.trmo.floracare.mapper;

import com.trmo.floracare.dto.PlantPhotoDTO;
import com.trmo.floracare.entities.PlantPhoto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlantPhotoMapper {
    PlantPhotoDTO mapToDTO(PlantPhoto photo);
    PlantPhoto map(PlantPhotoDTO photoDTO);
}
