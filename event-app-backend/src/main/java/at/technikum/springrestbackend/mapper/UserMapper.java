package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.UserCreationDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "imageId", source = "image.id")
    UserDto toDto(UserEntity userEntity);

    // image wird im Service gesetzt
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "active", source = "isActive")
    UserEntity toEntity(UserCreationDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    // image wird im Service aktualisiert
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "active", source = "isActive")
    void updateEntityFromDto(UserCreationDto dto, @MappingTarget UserEntity entity);
}
