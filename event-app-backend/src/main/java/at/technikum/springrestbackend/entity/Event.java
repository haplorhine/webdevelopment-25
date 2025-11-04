package at.technikum.springrestbackend.entity;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;

public class Event {

    @Id
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private Category category;

    private String imageURL;

    @Size(max = 1000)
    private String description;

    @NotBlank
    private String location;

    @NotBlank
    private LocalDateTime startDate;

    @NotBlank
    private LocalDateTime endDate;

    private Integer maxParticipants;

    private LocalDateTime salesStart;

    private LocalDateTime salesEnd;

}
