package com.universityadministration.helper;

import com.energymanagementsystem.ems.dto.Token;

import java.time.LocalDateTime;

public interface TokenProvider {
    Token generateAccessToken(String subject);

    Token generateRefreshToken(String subject);

    String getUsernameFromToken(String token);

    LocalDateTime getExpiryDateFromToken(String token);

    boolean validateToken(String token);
}
