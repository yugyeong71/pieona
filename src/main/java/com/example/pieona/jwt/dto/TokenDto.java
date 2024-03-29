package com.example.pieona.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    요청과 응답을 위한 토큰 DTO
 */
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class TokenDto {

    private String access_token;

    private String refresh_token;

}
