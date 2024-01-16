package com.example.backend.member.oauth2;

import java.util.HashMap;
import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);

        super.attributes = ((HashMap<String, Object>) attributes.get("response"));

        System.out.println(attributes);
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

}
