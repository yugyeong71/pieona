package com.example.pieona.dto;

import com.example.pieona.oauth2.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
public class SignRequest {

    private Long id;

    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    private String password;

    private String nickname;

    private String gender;

    private String image;

    private String content;

    private boolean isOn;

    private Role role;

}
