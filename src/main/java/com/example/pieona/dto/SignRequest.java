package com.example.pieona.dto;

import com.example.pieona.oauth2.Role;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
public class SignRequest {

    private Long id;

    private String memId;

    private String memPw;

    private String nickname;

    private String gender;

    private String image;

    private String type;

    private String content;

    private boolean isOn;

    private Role role;

}
