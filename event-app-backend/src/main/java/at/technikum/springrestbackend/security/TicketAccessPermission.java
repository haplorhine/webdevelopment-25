package at.technikum.springrestbackend.security;

import at.technikum.springrestbackend.entity.TicketEntity;
import at.technikum.springrestbackend.repositories.TicketRepository;
import at.technikum.springrestbackend.entity.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TicketAccessPermission implements AccessPermission {

    private final TicketRepository ticketRepository;

    @Override
    public boolean supports(Authentication authentication, String className) {
        return className.equals(TicketEntity.class.getName());
    }

    @Override
    public boolean hasPermission(Authentication authentication, UUID resourceId) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (principal.getRole().equals(UserType.ADMIN.name())) {
            return true;
        }

        return ticketRepository.findById(resourceId)
                .map(ticket -> ticket.getUser().getId().equals(principal.getId()))
                .orElse(false);
    }
}