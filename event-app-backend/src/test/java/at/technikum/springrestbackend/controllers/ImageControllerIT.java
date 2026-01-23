package at.technikum.springrestbackend.controllers;

import at.technikum.springrestbackend.repositories.ImageRepository;
import at.technikum.springrestbackend.storage.FileStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import java.util.Base64;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ImageControllerIT {

    private static final String PNG_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAAgAAAAIAQMAAAD+wSzIAAAABlBMVEX///+/v7+jQ3Y5AAAADklEQVQI12P4AIX8EAgALgAD/aNpbtEAAAAASUVORK5CYII";

    @Autowired
    private MockMvc mvc;

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:10.3.39");

    @MockitoBean
    private FileStorage minioStorage;

    @Autowired
    private ImageRepository imageRepository; // can be used to verify the uuid is correct in the response

    @Test
    @WithMockUser(roles = "USER")
    void validImage_uploadImage_returnsIdAndCreated() throws Exception {
        // given
        when(minioStorage.upload(any())).thenReturn(UUID.randomUUID().toString());
        byte[] imageBytes = Base64.getDecoder().decode(PNG_BASE64);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "event_image.jpg",
                "image/jpeg",
                imageBytes
        );

        // when
        ResultActions resultactions = mvc.perform(multipart("/images")
                .file(file));

        // then
        resultactions.andExpect(status().isCreated());
        resultactions.andExpect(jsonPath("$.id", notNullValue()));
    }
}
