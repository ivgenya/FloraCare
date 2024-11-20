package com.trmo.floracare.mapper;

import com.trmo.floracare.dto.PlantDTO;
import com.trmo.floracare.dto.RoomDTO;
import com.trmo.floracare.entities.Plant;
import com.trmo.floracare.entities.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlantMapper {
    PlantDTO mapToDTO(Plant plant);
    Plant map(PlantDTO dto);
}
