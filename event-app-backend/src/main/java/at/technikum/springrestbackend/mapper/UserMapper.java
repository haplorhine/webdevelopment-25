package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserEntity userEntity);
    UserEntity toEntity(UserDto dto);

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "createdDate", ignore = true)
    void updateEntityFromDto(UserDto dto, @org.mapstruct.MappingTarget UserEntity entity);
}
