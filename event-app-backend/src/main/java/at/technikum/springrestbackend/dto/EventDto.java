package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.Category;
import jakarta.validation.constraints.Min;
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

    private UUID imageId;

    private String description;

    @NotBlank
    private String location;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Min(1)
    private Integer maxParticipants;

    @NotNull
    private LocalDateTime salesStart;

    @NotNull
    private LocalDateTime salesEnd;

    private Double ticketPrice;

    @NotNull
    private UUID hostId; // UserId des Hosts

    private String hostName;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
