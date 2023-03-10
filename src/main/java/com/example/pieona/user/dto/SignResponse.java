package com.example.pieona.user.dto;

import com.example.pieona.jwt.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SignResponse {

    private Long id;

    private String email;

    private String gender;

    private TokenDto token;

}
