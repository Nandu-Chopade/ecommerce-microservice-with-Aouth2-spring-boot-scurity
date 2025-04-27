package com.onlineshop.auth_service.client;

import com.onlineshop.auth_service.dto.AuthRequest;
import com.onlineshop.auth_service.dto.AuthResponse;
import com.onlineshop.auth_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("user/auth/{username}")
    UserDto getUserByUsername(@PathVariable("username") String username);

    @PostMapping("user/auth/register")
    void registerUser(@RequestBody UserDto user);

    @PostMapping("user/auth/login")
    ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req);
}