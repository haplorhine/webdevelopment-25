package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "hostId", source = "host.id")
    @Mapping(target = "hostName", source = "host.username")
    @Mapping(target = "imageId", source = "image.id")
    EventDto toDto(EventEntity eventEntity);

    @Mapping(target = "host", ignore = true)
    @Mapping(target = "image", ignore = true)
    EventEntity toEntity(EventDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateEntityFromDto(EventDto dto, @MappingTarget EventEntity entity);
}