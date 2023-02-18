package com.example.pieona.oauth2.exception;

// 가입 실패
public class OAuth2RegistrationException extends RuntimeException{

    public OAuth2RegistrationException(String message){
        super(message);
    }
}
