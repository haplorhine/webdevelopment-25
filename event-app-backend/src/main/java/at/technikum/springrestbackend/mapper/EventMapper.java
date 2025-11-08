package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
//    @Mapping(target = "hostId", source = "host.id")
    EventDto toDto(EventEntity eventEntity);
    EventEntity toEntity(EventDto dto);
}
