package at.technikum.springrestbackend.storage;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;

class FileExceptionTest {

    @Test
    void constructorShouldSetMessageAndCause() {
        Throwable cause = new RuntimeException("root");

        FileException ex = new FileException("upload failed", cause);

        assertThat(ex.getMessage()).isEqualTo("upload failed");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    void shouldHaveInternalServerErrorStatus() {
        ResponseStatus status = FileException.class.getAnnotation(ResponseStatus.class);

        assertThat(status).isNotNull();
        assertThat(status.value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
