package com.example.pieona.oauth2.exception;

// 자원 없음
public class OAuth2NotFoundException extends RuntimeException{

    public OAuth2NotFoundException(String message){
        super(message);
    }

}
