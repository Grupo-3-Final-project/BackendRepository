package com.parque.security;

import com.parque.auth.model.InternalRole;
import com.parque.auth.repository.InternalCredentialRepository;
import com.parque.entity.InternalCredential;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final InternalCredentialRepository internalCredentialRepository;

    public CustomUserDetailsService(InternalCredentialRepository internalCredentialRepository) {
        this.internalCredentialRepository = internalCredentialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        InternalCredential credential = internalCredentialRepository.findByUsername(username)
                .filter(InternalCredential::getActive)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return createUserDetails(credential.getUsername(), credential.getPasswordHash(), credential.getRole());
    }

    private UserDetails createUserDetails(String username, String passwordHash, InternalRole role) {
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(role.getRoleName())
        );
        return new User(
            username,
            passwordHash,
            authorities
        );
    }
}
