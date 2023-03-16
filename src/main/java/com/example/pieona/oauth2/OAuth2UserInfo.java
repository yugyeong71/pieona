package com.example.pieona.oauth2;

import java.util.Map;

public abstract class OAuth2UserInfo {

    /*
        소셜 타입별로 유저 정보를 가지는 추상클래스
     */

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId(); // 소셜 식별 값

    public abstract String getNickname();

    public abstract String getImageUrl();

}
