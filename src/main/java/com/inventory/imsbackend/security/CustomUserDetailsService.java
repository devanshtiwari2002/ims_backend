package com.inventory.imsbackend.security;

import com.inventory.imsbackend.user.User;
import com.inventory.imsbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
        throws  UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not Found")
                );
        // ROLE double type solve
        String dbRole = user.getRole();
        String finalRole = dbRole.startsWith("ROLE_") ? dbRole : "ROLE_" + dbRole;
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(finalRole);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }
}
