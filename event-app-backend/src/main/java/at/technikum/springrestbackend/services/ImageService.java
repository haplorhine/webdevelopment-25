package at.technikum.springrestbackend.services;

import at.technikum.springrestbackend.dto.ImageDto;
import at.technikum.springrestbackend.entity.ImageEntity;
import at.technikum.springrestbackend.mapper.ImageMapper;
import at.technikum.springrestbackend.repositories.ImageRepository;
import at.technikum.springrestbackend.storage.FileStorage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    private final ImageMapper imageMapper;

    private final FileStorage fileStorage;

    public ImageDto upload(MultipartFile toUpload) {
        String externalId = fileStorage.upload(toUpload);

        ImageEntity image = new ImageEntity();
        image.setName(toUpload.getOriginalFilename());
        image.setExternalId(externalId);
        image.setContentType(toUpload.getContentType());

        return imageMapper.toDto(imageRepository.save(image));
    }

    public ImageEntity findById(UUID id) {
        return imageRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Resource asResource(ImageEntity image) {
        InputStream stream = fileStorage.load(image.getExternalId());

        return new InputStreamResource(stream);
    }

}