package at.technikum.springrestbackend.storage.minio;

import at.technikum.springrestbackend.storage.FileException;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MinioStorageTest {

    private MinioClient minioClient;
    private MinioStorage minioStorage;

    @BeforeEach
    void setUp() {
        minioClient = mock(MinioClient.class);
        MinioProperties minioProperties = new MinioProperties();
        minioProperties.setBucket("test-bucket");
        minioStorage = new MinioStorage(minioProperties, minioClient);
    }

    @Test
    void upload_shouldUploadFileAndReturnUuid() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        byte[] content = "test-content".getBytes();
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        when(file.getSize()).thenReturn((long) content.length);
        when(file.getContentType()).thenReturn("text/plain");

        String id = minioStorage.upload(file);

        // wir erwarten mindestens eine nicht-leere ID
        assertThat(id).isNotBlank();
        // Es reicht für diesen Test, dass putObject mit irgendeinem PutObjectArgs aufgerufen wurde
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void upload_shouldWrapExceptionsInFileException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> minioStorage.upload(file))
                .isInstanceOf(FileException.class)
                .hasMessageContaining("Upload file failed for file with id=")
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void load_shouldReturnInputStream() throws Exception {
        String id = "file-id";
        GetObjectResponse response = mock(GetObjectResponse.class);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

        InputStream result = minioStorage.load(id);

        assertThat(result).isSameAs(response);
        verify(minioClient, times(1)).getObject(any(GetObjectArgs.class));
    }

    @Test
    void load_shouldWrapExceptionsInFileException() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> minioStorage.load("some-id"))
                .isInstanceOf(FileException.class)
                .hasMessageContaining("Load file failed for file with external id=")
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
