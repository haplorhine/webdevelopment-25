package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.dto.ImageDto;
import at.technikum.springrestbackend.entity.ImageEntity;
import at.technikum.springrestbackend.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ImageDto upload(@RequestParam("file") MultipartFile toUpload) {
        return imageService.upload(toUpload);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> retrieve(@PathVariable UUID id) {
        ImageEntity image = imageService.findById(id);

        Resource resource = imageService.asResource(image);
        MediaType mediaType = MediaType.parseMediaType(image.getContentType());

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(resource);
    }
}
