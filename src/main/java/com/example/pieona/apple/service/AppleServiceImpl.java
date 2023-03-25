package com.example.pieona.apple.service;

import com.example.pieona.apple.utills.AppleUtils;
import com.example.pieona.apple.model.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppleServiceImpl implements AppleService {

    private final AppleUtils appleUtils;

    @Override
    public String getAppleClientSecret(String id_token) {

        if (appleUtils.verifyIdentityToken(id_token)) {
            return appleUtils.createClientSecret();
        }

        return null;
    }

    @Override
    public TokenResponse requestCodeValidations(String client_secret, String code, String refresh_token) {

        TokenResponse tokenResponse = new TokenResponse();

        if (client_secret != null && code != null && refresh_token == null) {
            tokenResponse = appleUtils.validateAuthorizationGrantCode(client_secret, code);
        } else if (client_secret != null && code == null && refresh_token != null) {
            tokenResponse = appleUtils.validateAnExistingRefreshToken(client_secret, refresh_token);
        }

        return tokenResponse;
    }

    @Override
    public Map<String, String> getLoginMetaInfo() {
        return appleUtils.getMetaInfo();
    }

    @Override
    public String getPayload(String id_token) {
        return appleUtils.decodeFromIdToken(id_token).toString();
    }

}
