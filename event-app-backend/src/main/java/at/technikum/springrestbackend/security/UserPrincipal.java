package at.technikum.springrestbackend.security;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.UUID;

@Getter
public class UserPrincipal extends User {
    private UUID id;
    private String role;

    public UserPrincipal(UUID id, String username, String password, String role, boolean isActive) {
        super(username, password, isActive, true, true, true, List.of(new SimpleGrantedAuthority(role)));
        this.id = id;
        this.role = role;
    }
}