package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.ImageDto;
import at.technikum.springrestbackend.entity.ImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageDto toDto(ImageEntity imageEntity);
    ImageEntity toEntity(ImageDto dto);
}

