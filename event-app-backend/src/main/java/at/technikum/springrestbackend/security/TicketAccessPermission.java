package at.technikum.springrestbackend.security;

import at.technikum.springrestbackend.entity.TicketEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TicketAccessPermission implements AccessPermission {
    @Override
    public boolean supports(Authentication authentication, String className) {
        return className.equals(TicketEntity.class.getName());
    }

    @Override
    public boolean hasPermission(Authentication authentication, UUID resourceId) {
       UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

       return principal.getId().equals(resourceId);
    }
}
