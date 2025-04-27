package com.onlineshop.user_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String message;
    public AuthResponse(String token, String message) { this.token = token;  this.message = message;}
}