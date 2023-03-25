package com.example.pieona.apple.service;

import com.example.pieona.apple.model.TokenResponse;

import java.util.Map;

public interface AppleService {

    String getAppleClientSecret(String id_token);

    TokenResponse requestCodeValidations(String client_secret, String code, String refresh_token);

    Map<String, String> getLoginMetaInfo();

    String getPayload(String id_token);

}
