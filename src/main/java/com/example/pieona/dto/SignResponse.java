package com.example.pieona.dto;

import com.example.pieona.entity.Authority;
import com.example.pieona.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SignResponse {

    private String email;

    private String gender;

    private TokenDto token;


}
