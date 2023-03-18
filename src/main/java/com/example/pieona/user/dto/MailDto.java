package com.example.pieona.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MailDto {

    private String address; // 사용자 메일 주소
    private String title; // 메일 제목
    private String message; // 메일 내용

}
