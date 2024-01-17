package com.example.backend.initDB;
import com.example.backend.member.entity.Auth;
import com.example.backend.member.entity.Member;
import com.example.backend.member.entity.SocialType;
import com.example.backend.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberInitDB {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final MemberRepository memberRepository;
        private final PasswordEncoder passwordEncoder;

        public void dbInit1() {

            Member user = Member.builder()
                    .username("dkfma1@naver.com")
                    .password(passwordEncoder.encode("dkfmaPassword1!"))
                    .nickname("니니꿍")
                    .phoneNum("01012341234")
                    .role(Auth.ROLE_USER)
                    .socialType(SocialType.DEFAULT)
                    .build();

            memberRepository.save(user);
        }
    }
}