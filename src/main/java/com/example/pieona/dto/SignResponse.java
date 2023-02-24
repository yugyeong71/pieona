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

    //private Long id;

    //private String nickname;

    private String email;

    //private String password;

    private String gender;

    //private String image;

    //private String content;

    //private boolean isOn;

    //private List<Authority> roles = new ArrayList<>();

    private TokenDto token;


}
