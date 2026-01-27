package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.EventDto;
import at.technikum.springrestbackend.entity.Category;
import at.technikum.springrestbackend.entity.EventEntity;
import at.technikum.springrestbackend.entity.ImageEntity;
import at.technikum.springrestbackend.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventMapperTest {

    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Test
    void toDto_shouldMapAllFields() {
        // Given
        UUID eventId = UUID.randomUUID();
        UUID hostId = UUID.randomUUID();
        UUID imageId = UUID.randomUUID();
        LocalDateTime createdDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().plusDays(7);
        LocalDateTime endDate = LocalDateTime.now().plusDays(8);
        LocalDateTime salesStart = LocalDateTime.now();
        LocalDateTime salesEnd = LocalDateTime.now().plusDays(6);

        UserEntity host = new UserEntity();
        host.setId(hostId);
        host.setUsername("testhost");

        ImageEntity image = new ImageEntity();
        image.setId(imageId);

        EventEntity entity = new EventEntity();
        entity.setId(eventId);
        entity.setTitle("Test Event");
        entity.setDescription("Test Description");
        entity.setHost(host);
        entity.setImage(image);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        entity.setLocation("Test Location");
        entity.setCategory(Category.CONCERT);
        entity.setMaxParticipants(100);
        entity.setTicketPrice(25.0);
        entity.setSalesStart(salesStart);
        entity.setSalesEnd(salesEnd);
        entity.setCreatedDate(createdDate);

        // When
        EventDto dto = eventMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(eventId);
        assertThat(dto.getTitle()).isEqualTo("Test Event");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getHostId()).isEqualTo(hostId);
        assertThat(dto.getHostName()).isEqualTo("testhost");
        assertThat(dto.getImageId()).isEqualTo(imageId);
        assertThat(dto.getStartDate()).isEqualTo(startDate);
        assertThat(dto.getEndDate()).isEqualTo(endDate);
        assertThat(dto.getLocation()).isEqualTo("Test Location");
        assertThat(dto.getCategory()).isEqualTo(Category.CONCERT);
        assertThat(dto.getMaxParticipants()).isEqualTo(100);
        assertThat(dto.getTicketPrice()).isEqualTo(25.0);
        assertThat(dto.getSalesStart()).isEqualTo(salesStart);
        assertThat(dto.getSalesEnd()).isEqualTo(salesEnd);
        assertThat(dto.getCreatedDate()).isEqualTo(createdDate);
    }

    @Test
    void toDto_shouldHandleNullHost() {
        // Given
        EventEntity entity = new EventEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Test Event");

        // When
        EventDto dto = eventMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getHostId()).isNull();
        assertThat(dto.getHostName()).isNull();
    }

    @Test
    void toDto_shouldHandleNullImage() {
        // Given
        EventEntity entity = new EventEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Test Event");

        // When
        EventDto dto = eventMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getImageId()).isNull();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // Given
        UUID eventId = UUID.randomUUID();
        LocalDateTime createdDate = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.now().plusDays(7);
        LocalDateTime endDate = LocalDateTime.now().plusDays(8);
        LocalDateTime salesStart = LocalDateTime.now();
        LocalDateTime salesEnd = LocalDateTime.now().plusDays(6);

        EventDto dto = new EventDto();
        dto.setId(eventId);
        dto.setTitle("Test Event");
        dto.setDescription("Test Description");
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setLocation("Test Location");
        dto.setCategory(Category.SPORT_EVENT);
        dto.setMaxParticipants(100);
        dto.setTicketPrice(25.0);
        dto.setSalesStart(salesStart);
        dto.setSalesEnd(salesEnd);
        dto.setCreatedDate(createdDate);

        // When
        EventEntity entity = eventMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(eventId);
        assertThat(entity.getTitle()).isEqualTo("Test Event");
        assertThat(entity.getDescription()).isEqualTo("Test Description");
        assertThat(entity.getStartDate()).isEqualTo(startDate);
        assertThat(entity.getEndDate()).isEqualTo(endDate);
        assertThat(entity.getLocation()).isEqualTo("Test Location");
        assertThat(entity.getCategory()).isEqualTo(Category.SPORT_EVENT);
        assertThat(entity.getMaxParticipants()).isEqualTo(100);
        assertThat(entity.getTicketPrice()).isEqualTo(25.0);
        assertThat(entity.getSalesStart()).isEqualTo(salesStart);
        assertThat(entity.getSalesEnd()).isEqualTo(salesEnd);
        assertThat(entity.getCreatedDate()).isEqualTo(createdDate);
        assertThat(entity.getHost()).isNull();
        assertThat(entity.getImage()).isNull();
    }

    @Test
    void updateEntityFromDto_shouldUpdateAllFields() {
        // Given
        EventEntity existingEntity = new EventEntity();
        existingEntity.setId(UUID.randomUUID());
        existingEntity.setTitle("Old Title");
        existingEntity.setDescription("Old Description");
        existingEntity.setLocation("Old Location");
        existingEntity.setCategory(Category.CONCERT);

        LocalDateTime newStartDate = LocalDateTime.now().plusDays(10);
        LocalDateTime newEndDate = LocalDateTime.now().plusDays(11);
        LocalDateTime newSalesStart = LocalDateTime.now();
        LocalDateTime newSalesEnd = LocalDateTime.now().plusDays(9);

        EventDto dto = new EventDto();
        dto.setTitle("New Title");
        dto.setDescription("New Description");
        dto.setStartDate(newStartDate);
        dto.setEndDate(newEndDate);
        dto.setLocation("New Location");
        dto.setCategory(Category.WORKSHOP);
        dto.setMaxParticipants(200);
        dto.setTicketPrice(50.0);
        dto.setSalesStart(newSalesStart);
        dto.setSalesEnd(newSalesEnd);

        UUID originalId = existingEntity.getId();
        LocalDateTime originalCreatedDate = existingEntity.getCreatedDate();

        // When
        eventMapper.updateEntityFromDto(dto, existingEntity);

        // Then
        assertThat(existingEntity.getId()).isEqualTo(originalId);
        assertThat(existingEntity.getCreatedDate()).isEqualTo(originalCreatedDate);
        assertThat(existingEntity.getTitle()).isEqualTo("New Title");
        assertThat(existingEntity.getDescription()).isEqualTo("New Description");
        assertThat(existingEntity.getStartDate()).isEqualTo(newStartDate);
        assertThat(existingEntity.getEndDate()).isEqualTo(newEndDate);
        assertThat(existingEntity.getLocation()).isEqualTo("New Location");
        assertThat(existingEntity.getCategory()).isEqualTo(Category.WORKSHOP);
        assertThat(existingEntity.getMaxParticipants()).isEqualTo(200);
        assertThat(existingEntity.getTicketPrice()).isEqualTo(50.0);
        assertThat(existingEntity.getSalesStart()).isEqualTo(newSalesStart);
        assertThat(existingEntity.getSalesEnd()).isEqualTo(newSalesEnd);
    }
}
