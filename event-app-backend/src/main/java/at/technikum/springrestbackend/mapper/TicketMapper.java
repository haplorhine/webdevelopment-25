package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.entity.TicketEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketDto toDto(TicketEntity ticketEntity);
    TicketEntity toEntity(TicketDto dto);
}
