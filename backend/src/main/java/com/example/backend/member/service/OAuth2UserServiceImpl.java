package com.example.backend.member.service;

import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.member.entity.CustomOAuth2User;
import com.example.backend.member.entity.Member;
import com.example.backend.member.entity.SocialType;
import com.example.backend.member.oauth2.OAuth2UserInfo;
import com.example.backend.member.oauth2.OAuth2UserInfoFactory;
import com.example.backend.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("loadUser: {}", oAuth2User.getAttributes());

        try {
            log.info(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
            return process(userRequest, oAuth2User);
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        SocialType providerType = SocialType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());

        Optional<Member> savedUser = memberRepository.findByUsername(userInfo.getEmail());

        Member member;

        if (savedUser.isPresent()) {
            log.info("같은 이메일로 가입된 회원 ");
            if (providerType.equals(savedUser.get().getSocialType())) {
                member = savedUser.get();
            }else {
                throw new AppException(ErrorCode.USER_DUPLICATED, providerType+"으로 가입된 회원입니다.");
            }
        } else {
            member = createUser(userInfo, providerType);
        }

        return new CustomOAuth2User(member, user.getAttributes());
    }

    private Member createUser(OAuth2UserInfo userInfo, SocialType socialType) {
        UUID tempPassword = UUID.randomUUID();

        Member member = Member.createMember(userInfo.getEmail(), String.valueOf(tempPassword), userInfo.getName(), "01012341234", socialType);
        memberRepository.save(member);
        return member;
    }
}
