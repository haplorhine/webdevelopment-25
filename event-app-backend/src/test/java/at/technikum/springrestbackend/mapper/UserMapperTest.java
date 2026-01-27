package at.technikum.springrestbackend.mapper;

import at.technikum.springrestbackend.dto.UserCreationDto;
import at.technikum.springrestbackend.dto.UserDto;
import at.technikum.springrestbackend.entity.Country;
import at.technikum.springrestbackend.entity.ImageEntity;
import at.technikum.springrestbackend.entity.Salutation;
import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.entity.UserType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDto_shouldMapAllFields() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID imageId = UUID.randomUUID();

        ImageEntity image = new ImageEntity();
        image.setId(imageId);

        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setUsername("testuser");
        entity.setEmail("test@example.com");
        entity.setSalutation(Salutation.MR);
        entity.setCountry(Country.AUSTRIA);
        entity.setUserType(UserType.USER);
        entity.setActive(true);
        entity.setImage(image);

        // When
        UserDto dto = userMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(userId);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
        assertThat(dto.getSalutation()).isEqualTo(Salutation.MR);
        assertThat(dto.getCountry()).isEqualTo(Country.AUSTRIA);
        assertThat(dto.getUserType()).isEqualTo(UserType.USER);
        assertThat(dto.isActive()).isTrue();
        assertThat(dto.getImageId()).isEqualTo(imageId);
    }

    @Test
    void toDto_shouldHandleNullImage() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setUsername("testuser");
        entity.setEmail("test@example.com");

        // When
        UserDto dto = userMapper.toDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getImageId()).isNull();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // Given
        UserCreationDto dto = new UserCreationDto();
        dto.setUsername("newuser");
        dto.setEmail("newuser@example.com");
        dto.setPassword("securePassword123");
        dto.setSalutation(Salutation.MS);
        dto.setCountry(Country.GERMANY);
        dto.setUserType(UserType.HOST);
        dto.setIsActive(true);

        // When
        UserEntity entity = userMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getUsername()).isEqualTo("newuser");
        assertThat(entity.getEmail()).isEqualTo("newuser@example.com");
        assertThat(entity.getPassword()).isEqualTo("securePassword123");
        assertThat(entity.getSalutation()).isEqualTo(Salutation.MS);
        assertThat(entity.getCountry()).isEqualTo(Country.GERMANY);
        assertThat(entity.getUserType()).isEqualTo(UserType.HOST);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getImage()).isNull();
    }

    @Test
    void updateEntityFromDto_shouldUpdateAllFields() {
        // Given
        UserEntity existingEntity = new UserEntity();
        existingEntity.setId(UUID.randomUUID());
        existingEntity.setUsername("olduser");
        existingEntity.setEmail("old@example.com");
        existingEntity.setCountry(Country.AUSTRIA);
        existingEntity.setUserType(UserType.USER);

        UserCreationDto dto = new UserCreationDto();
        dto.setUsername("updateduser");
        dto.setEmail("updated@example.com");
        dto.setPassword("newPassword456");
        dto.setSalutation(Salutation.MR);
        dto.setCountry(Country.GERMANY);
        dto.setUserType(UserType.ADMIN);
        dto.setIsActive(true);

        UUID originalId = existingEntity.getId();

        // When
        userMapper.updateEntityFromDto(dto, existingEntity);

        // Then
        assertThat(existingEntity.getId()).isEqualTo(originalId);
        assertThat(existingEntity.getUsername()).isEqualTo("updateduser");
        assertThat(existingEntity.getEmail()).isEqualTo("updated@example.com");
        assertThat(existingEntity.getPassword()).isEqualTo("newPassword456");
        assertThat(existingEntity.getSalutation()).isEqualTo(Salutation.MR);
        assertThat(existingEntity.getCountry()).isEqualTo(Country.GERMANY);
        assertThat(existingEntity.getUserType()).isEqualTo(UserType.ADMIN);
        assertThat(existingEntity.isActive()).isTrue();
    }
}
