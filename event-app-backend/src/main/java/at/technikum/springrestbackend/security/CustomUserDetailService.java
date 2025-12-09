package at.technikum.springrestbackend.security;


import at.technikum.springrestbackend.entity.UserEntity;
import at.technikum.springrestbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.findUserByUsername(username);
        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), user.getUserType().toString());
    }
}
