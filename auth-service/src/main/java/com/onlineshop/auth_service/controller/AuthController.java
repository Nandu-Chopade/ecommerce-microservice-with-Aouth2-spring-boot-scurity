package com.onlineshop.auth_service.controller;

import com.onlineshop.auth_service.client.UserClient;
import com.onlineshop.auth_service.dto.ApiResponse;
import com.onlineshop.auth_service.dto.AuthRequest;
import com.onlineshop.auth_service.dto.AuthResponse;
import com.onlineshop.auth_service.dto.UserDto;
import com.onlineshop.auth_service.services.CustomUserDetailsService;
import com.onlineshop.auth_service.util.JwtUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        UserDto newUser = new UserDto();
        newUser.setUsername(req.getUsername());
        newUser.setPassword(passwordEncoder.encode(req.getPassword()));

        try {
            userClient.registerUser(newUser);
        } catch (FeignException.Conflict ex) {
            // 409 from user-service
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User '" + req.getUsername() + "' already exists");
        } catch (FeignException fe) {
            // other 4xx/5xx from user-service
            return ResponseEntity
                    .status(fe.status())
                    .body("User Service error: " + fe.getMessage());
        }

        // Optionally immediately log in the user by issuing a token
        String token = jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(req.getUsername())
                        .password(newUser.getPassword())
                        .authorities(Collections.emptyList())
                        .build()
        );
        return ResponseEntity.ok(new AuthResponse(token));
    }




    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest req) {
        try {
            // Step 1: Call user-service to authenticate and get token
            ResponseEntity<AuthResponse> authResponse = userClient.login(req);

            // Step 2: Extract the token from response
            String token = authResponse.getBody().getToken();

            // Step 3: Prepare a success response
            ApiResponse<AuthResponse> response = new ApiResponse<>(
                    "Login successful",
                    new AuthResponse(token),
                    HttpStatus.OK.value(),
                    true
            );

            return ResponseEntity.ok(response);  // Return a success response with 200 OK

        } catch (Exception e) {
            // Step 4: Handle failure scenario
            ApiResponse<AuthResponse> errorResponse = new ApiResponse<>(
                    "Login failed: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // Return failure with 500
        }
    }
}