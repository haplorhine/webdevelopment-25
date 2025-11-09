package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EventDto {

    private UUID id;

    @Size(min = 2, max = 50)
    private String title;

    private Category category;

    private String imageURL;

    private String description;

    @NotBlank
    private String location;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private Integer maxParticipants;

    @NotNull
    private LocalDateTime salesStart;

    @NotNull
    private LocalDateTime salesEnd;

    @NotNull
    private UUID hostId; // UserId des Hosts

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
