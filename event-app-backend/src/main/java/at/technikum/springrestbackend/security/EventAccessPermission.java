package at.technikum.springrestbackend.security;

import at.technikum.springrestbackend.entity.EventEntity;
import at.technikum.springrestbackend.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventAccessPermission implements AccessPermission {

    private final EventRepository eventRepository;

    @Override
    public boolean supports(Authentication authentication, String className) {
        return className.equals(EventEntity.class.getName());
    }

    @Override
    public boolean hasPermission(Authentication authentication, UUID resourceId) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return eventRepository.findById(resourceId)
                .map(event -> {
                    return event.getHost().getId().equals(principal.getId());
                })
                .orElse(false);
    }
}