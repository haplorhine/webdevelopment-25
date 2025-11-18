package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.entity.TicketEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "createdDate", target = "purchaseDate")
    TicketDto toDto(TicketEntity ticketEntity);

    @Mapping(source = "eventId", target = "event.id")
    @Mapping(source = "userId", target = "user.id")
    TicketEntity toEntity(TicketDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateEntityFromDto(TicketDto dto, @org.mapstruct.MappingTarget TicketEntity entity);
}
