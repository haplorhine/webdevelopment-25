package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.UserCreationDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserEntity userEntity);
    UserEntity toEntity(UserCreationDto dto);

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "createdDate", ignore = true)
    void updateEntityFromDto(UserCreationDto dto, @org.mapstruct.MappingTarget UserEntity entity);
}
