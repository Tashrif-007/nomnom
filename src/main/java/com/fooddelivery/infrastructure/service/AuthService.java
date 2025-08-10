package com.fooddelivery.application.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    String generateToken(String userId, String email);
    boolean validateToken(String token);
    String getUserIdFromToken(String token);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}
