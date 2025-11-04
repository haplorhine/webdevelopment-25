package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EventDto {
    private Long eventId;
    private String title;
    private Category category;
    private String imageURL;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxParticipants;
    private LocalDateTime salesStart;
    private LocalDateTime salesEnd;
    private Long hostId; // UserId des Hosts
}
