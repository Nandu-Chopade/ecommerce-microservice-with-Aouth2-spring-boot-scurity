package com.onlineshop.auth_service.services;

import com.onlineshop.auth_service.client.UserClient;
import com.onlineshop.auth_service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto user = userClient.getUserByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User not found");
        return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}