package com.example.pieona.oauth2;

import com.example.pieona.user.Role;
import com.example.pieona.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey; // OAuth2 로그인 시 키가 되는 필드 값 (PK)
    private OAuth2UserInfo oAuth2UserInfo; // 소셜 타입별 로그인 유저 정보

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo){
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    /*
        SocialType 에 맞는 메소드 호출하여 OAuthAttributes 객체 반환
        userNameAttributeName -> OAuth2 로그인 시 키(PK)가 되는 값
        attributes -> OAuth2 서비스의 유저 정보들
        소셜별 of 메소드(ofKaKao)는 각각 소셜 로그인 API 에서 제공하는
        회원의 식별값(id), attributes, nameAttributeKey 를 저장 후 build
     */
    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {
        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    /*
        of 메소드로 OAuthAttributes 객체가 생성되어, 유저 정보들이 담긴 OAuth2UserInfo 가 소셜 타입별로 주입된 상태
        OAuth2UserInfo 에서 socialId(식별값), nickname, image 를 가져와서 build
        email -> UUID 로 중복 없는 랜덤 값 생성
        role -> GUEST 로 설정
     */
    public User toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
        return User.builder()
                .socialType(socialType)
                .socialId(oauth2UserInfo.getId())
                .email(UUID.randomUUID() + "@socialUser.com")
                .nickname(oauth2UserInfo.getNickname())
                .image(oauth2UserInfo.getImageUrl())
                .role(Role.GUEST)
                .build();
    }
}