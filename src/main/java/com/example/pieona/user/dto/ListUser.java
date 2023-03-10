package com.example.pieona.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ListUser {

    private Long id;

    private String nickname;

    private String email;

    private String gender;

    private String image;


}
