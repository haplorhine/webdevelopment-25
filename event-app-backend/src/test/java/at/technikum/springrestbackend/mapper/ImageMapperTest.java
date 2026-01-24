package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.ImageDto;
import at.technikum.springrestbackend.entity.ImageEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ImageMapperTest {

    private final ImageMapper imageMapper = Mappers.getMapper(ImageMapper.class);

    @Test
    void toDto_shouldMapAllFields() {
        // Given
        UUID imageId = UUID.randomUUID();

        ImageEntity entity = new ImageEntity();
        entity.setId(imageId);
        entity.setExternalId("ext-123");
        entity.setName("test-image.jpg");
        entity.setContentType("image/jpeg");

        // When
        ImageDto dto = imageMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(imageId);
    }

    @Test
    void toDto_shouldHandleNullValues() {
        // Given
        ImageEntity entity = new ImageEntity();
        entity.setId(UUID.randomUUID());

        // When
        ImageDto dto = imageMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isNotNull();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // Given
        UUID imageId = UUID.randomUUID();

        ImageDto dto = new ImageDto(imageId);

        // When
        ImageEntity entity = imageMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(imageId);
    }

    @Test
    void toEntity_shouldHandleNullValues() {
        // Given
        UUID imageId = UUID.randomUUID();
        ImageDto dto = new ImageDto(imageId);

        // When
        ImageEntity entity = imageMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(imageId);
        assertThat(entity.getName()).isNull();
        assertThat(entity.getExternalId()).isNull();
        assertThat(entity.getContentType()).isNull();
    }
}
