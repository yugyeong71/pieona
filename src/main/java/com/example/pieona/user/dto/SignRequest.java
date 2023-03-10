package com.example.pieona.user.dto;

import lombok.*;

@Getter @Setter
public class SignRequest {

    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String gender;

    private String image;

    private boolean isOn;


}
