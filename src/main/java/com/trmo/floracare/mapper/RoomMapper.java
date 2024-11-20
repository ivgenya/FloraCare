package com.trmo.floracare.mapper;

import com.trmo.floracare.dto.PlantPhotoDTO;
import com.trmo.floracare.dto.PlantViewDTO;
import com.trmo.floracare.dto.RoomDTO;
import com.trmo.floracare.entities.Plant;
import com.trmo.floracare.entities.PlantPhoto;
import com.trmo.floracare.entities.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room map(RoomDTO dto);

    @Mapping(target = "plants", source = "plants")
    RoomDTO mapToDTO(Room room);

    @Mapping(target = "photos", source = "photos")
    PlantViewDTO mapToPlantViewDTO(Plant plant);

    @Mapping(target = "image", source = "image")
    @Mapping(target = "plantId", source = "plant.id")
    PlantPhotoDTO mapToPlantPhotoDTO(PlantPhoto plantPhoto);

    List<PlantPhotoDTO> mapToPlantPhotoDTOList(List<PlantPhoto> photos);

}