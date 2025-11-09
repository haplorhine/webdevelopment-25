package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter

public class TicketDto {

    private UUID eventId;

    private UUID userId;

    @NotNull
    private LocalDateTime purchaseDate;

    private Double price;

    private TicketStatus status; // ENUM: ACTIVE, CANCELLED
}