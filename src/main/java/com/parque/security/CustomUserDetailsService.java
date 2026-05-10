package com.parque.security;

import com.parque.auth.model.InternalRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom user details service for loading users with their roles.
 * TODO: Replace with database lookup from user repository
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Load from database using UserRepository
        // For now, hardcoded demo users
        
        if ("admin".equals(username)) {
            return createUserDetails(username, "admin12345", InternalRole.ADMIN);
        } else if ("manager".equals(username)) {
            return createUserDetails(username, "manager123", InternalRole.MANAGER);
        } else if ("employee".equals(username)) {
            return createUserDetails(username, "employee123", InternalRole.EMPLOYEE);
        } else if ("user".equals(username)) {
            return createUserDetails(username, "user12345", InternalRole.USER);
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    /**
     * Helper method to create UserDetails with specified role
     */
    private UserDetails createUserDetails(String username, String rawPassword, InternalRole role) {
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(role.getRoleName())
        );
        return new User(
            username,
            passwordEncoder.encode(rawPassword),
            authorities
        );
    }
}
