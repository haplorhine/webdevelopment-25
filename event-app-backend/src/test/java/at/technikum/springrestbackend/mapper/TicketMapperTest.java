package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.TicketDto;
import at.technikum.springrestbackend.entity.EventEntity;
import at.technikum.springrestbackend.entity.TicketEntity;
import at.technikum.springrestbackend.entity.TicketStatus;
import at.technikum.springrestbackend.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketMapperTest {

    private final TicketMapper ticketMapper = Mappers.getMapper(TicketMapper.class);

    @Test
    void toDto_shouldMapAllFields() {
        // Given
        UUID ticketId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdDate = LocalDateTime.now();

        EventEntity event = new EventEntity();
        event.setId(eventId);

        UserEntity user = new UserEntity();
        user.setId(userId);

        TicketEntity entity = new TicketEntity();
        entity.setId(ticketId);
        entity.setEvent(event);
        entity.setUser(user);
        entity.setStatus(TicketStatus.ACTIVE);
        entity.setCreatedDate(createdDate);

        // When
        TicketDto dto = ticketMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(ticketId);
        assertThat(dto.getEventId()).isEqualTo(eventId);
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getStatus()).isEqualTo(TicketStatus.ACTIVE);
        assertThat(dto.getPurchaseDate()).isEqualTo(createdDate);
    }

    @Test
    void toDto_shouldHandleNullEvent() {
        // Given
        TicketEntity entity = new TicketEntity();
        entity.setId(UUID.randomUUID());
        entity.setStatus(TicketStatus.ACTIVE);

        // When
        TicketDto dto = ticketMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getEventId()).isNull();
    }

    @Test
    void toDto_shouldHandleNullUser() {
        // Given
        TicketEntity entity = new TicketEntity();
        entity.setId(UUID.randomUUID());
        entity.setStatus(TicketStatus.CANCELLED);

        // When
        TicketDto dto = ticketMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUserId()).isNull();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // Given
        UUID ticketId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime purchaseDate = LocalDateTime.now();

        TicketDto dto = new TicketDto();
        dto.setId(ticketId);
        dto.setEventId(eventId);
        dto.setUserId(userId);
        dto.setStatus(TicketStatus.CANCELLED);
        dto.setPurchaseDate(purchaseDate);

        // When
        TicketEntity entity = ticketMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(ticketId);
        assertThat(entity.getEvent()).isNotNull();
        assertThat(entity.getEvent().getId()).isEqualTo(eventId);
        assertThat(entity.getUser()).isNotNull();
        assertThat(entity.getUser().getId()).isEqualTo(userId);
        assertThat(entity.getStatus()).isEqualTo(TicketStatus.CANCELLED);
    }

    @Test
    void updateEntityFromDto_shouldUpdateStatus() {
        // Given
        TicketEntity existingEntity = new TicketEntity();
        existingEntity.setId(UUID.randomUUID());
        existingEntity.setStatus(TicketStatus.ACTIVE);
        existingEntity.setCreatedDate(LocalDateTime.now());

        TicketDto dto = new TicketDto();
        dto.setStatus(TicketStatus.CANCELLED);

        UUID originalId = existingEntity.getId();
        LocalDateTime originalCreatedDate = existingEntity.getCreatedDate();

        // When
        ticketMapper.updateEntityFromDto(dto, existingEntity);

        // Then
        assertThat(existingEntity.getId()).isEqualTo(originalId);
        assertThat(existingEntity.getCreatedDate()).isEqualTo(originalCreatedDate);
        assertThat(existingEntity.getStatus()).isEqualTo(TicketStatus.CANCELLED);
    }
}
