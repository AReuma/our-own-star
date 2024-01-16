package com.example.backend.member.oauth2;

import com.example.backend.member.entity.SocialType;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(SocialType socialType, Map<String, Object> attributes) {
        System.out.println(socialType);

        switch (socialType) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case KAKAO: return new KakaoOAuth2UserInfo(attributes);
            case NAVER: return new NaverOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
