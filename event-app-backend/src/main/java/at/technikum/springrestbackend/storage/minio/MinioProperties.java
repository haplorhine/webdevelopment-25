package at.technikum.springrestbackend.storage.minio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("minio")
public class MinioProperties {
    private String url;
    private int port;
    private String user;
    private String password;
    private String bucket;
}