package com.example.pieona.oauth2;

import com.example.pieona.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
public class OAuth2Attributes {

    private Map<String, Object> attributes;

    private String nameAttributeKey;

    private String nickname;

    private String memId;

    private Provider provider;

    private String oauthId;

    private String gender;

    private String image;

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String oauthId, String nickname, String memId, String gender, String image, Provider provider){
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.oauthId = oauthId;
        this.nickname = nickname;
        this.memId = memId;
        this.gender = gender;
        this.image = image;
        this.provider = provider;
    }

    @SneakyThrows
    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        log.info("userNameAttributeName = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userNameAttributeName));
        log.info("attributes = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(attributes));

        if(registrationId == "kakao")
            return ofKakao(userNameAttributeName, attributes);

        return null;
    }


    @SuppressWarnings("uncheked")
    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes){
        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        // kakao_account 안에 또 profile 이라는 JSON 객체가 있다. (nickname, profile_image)
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2Attributes.builder()
                .oauthId(attributes.get(userNameAttributeName).toString())
                .nickname((String) profile.get("nickname"))
                .memId((String) account.get("email"))
                .gender((String) account.get("gender"))
                .image((String) profile.get("image"))
                .provider(Provider.KAKAO)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }


}
