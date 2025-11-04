package at.technikum.springrestbackend.dto;

import at.technikum.springrestbackend.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class TicketDto {

        private Long ticketId;
        private Long eventId;
        private Long userId;
        private LocalDateTime purchaseDate;
        private Double price;
        private TicketStatus status; // ENUM: ACTIVE, CANCELLED

    }