package com.example.backend.member.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private Member member;
    private Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        // 해당 User의 권한을 리턴하는 곳.
        Collection<GrantedAuthority> collect = new ArrayList<>();

        collect.add(new SimpleGrantedAuthority(member.getRole().name()));


        return collect;
    }

    @Override
    public String getName() {
        return member.getNickname();
    }

    public String getUsername(){
        return member.getUsername();
    }

    public String getRole(){
        return member.getRole().name();
    }
}
