package com.trmo.floracare.mapper;

import com.trmo.floracare.dto.UserDTO;
import com.trmo.floracare.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO mapToDTO(User room);

    User map(UserDTO dto);
}
