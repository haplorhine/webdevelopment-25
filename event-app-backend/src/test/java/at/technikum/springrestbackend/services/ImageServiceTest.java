package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.ImageDto;
import at.technikum.springrestbackend.entity.ImageEntity;
import at.technikum.springrestbackend.mapper.ImageMapper;
import at.technikum.springrestbackend.repositories.ImageRepository;
import at.technikum.springrestbackend.storage.FileStorage;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private ImageService imageService;

    private MultipartFile multipartFile;
    private ImageEntity imageEntity;
    private ImageDto imageDto;
    private UUID imageId;

    @BeforeEach
    void setUp() {
        multipartFile = mock(MultipartFile.class);
        imageId = UUID.randomUUID();
        imageEntity = TestDataFactory.createImage(imageId);
        imageDto = new ImageDto(imageId);
    }

    @Test
    void upload_persistsImageAndReturnsDto() {
        when(multipartFile.getOriginalFilename()).thenReturn("poster.png");
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(fileStorage.upload(multipartFile)).thenReturn("external-id");
        when(imageRepository.save(any(ImageEntity.class))).thenReturn(imageEntity);
        when(imageMapper.toDto(imageEntity)).thenReturn(imageDto);

        ImageDto result = imageService.upload(multipartFile);

        assertThat(result).isEqualTo(imageDto);
        verify(fileStorage).upload(multipartFile);
        verify(imageRepository).save(any(ImageEntity.class));
    }

    @Test
    void findById_whenMissing_throwsEntityNotFound() {
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.findById(imageId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findById_returnsEntity() {
        when(imageRepository.findById(imageId)).thenReturn(Optional.of(imageEntity));

        ImageEntity result = imageService.findById(imageId);

        assertThat(result).isEqualTo(imageEntity);
        verify(imageRepository).findById(imageId);
    }

    @Test
    void asResource_wrapsInputStreamFromStorage() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("image-data".getBytes());
        when(fileStorage.load(imageEntity.getExternalId())).thenReturn(inputStream);

        Resource resource = imageService.asResource(imageEntity);

        assertThat(resource).isNotNull();
        assertThat(resource.getInputStream().readAllBytes()).isEqualTo("image-data".getBytes());
        verify(fileStorage).load(imageEntity.getExternalId());
    }
}
