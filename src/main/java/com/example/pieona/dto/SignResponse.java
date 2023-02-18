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

    private Long id;

    private String nickname;

    private String memId;

    private String memPw;

    private String gender;

    private String image;

    private String type;

    private String content;

    private boolean isOn;

    private List<Authority> roles = new ArrayList<>();

    private TokenDto token;

    public SignResponse(User user) {
        this.id = user.getId();
        this.memId = user.getMemId();
        this.nickname = user.getNickname();
        this.roles = user.getRoles();
    }

}
