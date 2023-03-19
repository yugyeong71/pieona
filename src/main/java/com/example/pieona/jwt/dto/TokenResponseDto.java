package com.example.pieona.jwt.dto;

public record TokenResponseDto(Long userId, String accessToken, String refreshToken,
                               Long accessTokenExpire, Long refreshTokenExpire) {
}
