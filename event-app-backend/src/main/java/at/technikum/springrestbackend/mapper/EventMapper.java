package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "hostId", source = "host.id")
    @Mapping(target = "hostName", source = "host.username") // <-- DAS HIER IST NEU
    EventDto toDto(EventEntity eventEntity);

    @Mapping(target = "host", ignore = true) // Beim Speichern ignorieren wir den Host aus dem DTO (wird im Service gesetzt)
    EventEntity toEntity(EventDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "host", ignore = true) // Auch beim Update den Host nicht überschreiben
    void updateEntityFromDto(EventDto dto, @org.mapstruct.MappingTarget EventEntity entity);
}