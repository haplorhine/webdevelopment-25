package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter

public class TicketDto {

    private Long eventId;

    private Long userId;

    @NotNull
    private LocalDateTime purchaseDate;

    private Double price;

    private TicketStatus status; // ENUM: ACTIVE, CANCELLED

}