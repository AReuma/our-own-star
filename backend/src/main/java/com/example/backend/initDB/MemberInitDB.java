package com.example.backend.initDB;
import com.example.backend.idolCategory.entity.IdolCategory;
import com.example.backend.idolCategory.repository.IdolCategoryRepository;
import com.example.backend.member.entity.Auth;
import com.example.backend.member.entity.JoinIdol;
import com.example.backend.member.entity.Member;
import com.example.backend.member.entity.SocialType;
import com.example.backend.member.repository.JoinIdolRepository;
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
        private final IdolCategoryRepository idolCategoryRepository;
        private final JoinIdolRepository joinIdolRepository;

        public void dbInit1() {
            Member member1 = createMember("dkfma1@naver.com", "dkfmaPassword1!", "니니야", "01012341234", Auth.ROLE_USER);
            memberRepository.save(member1);

            Member manager = createMember("dkfma2@naver.com", "dkfmaPassword2!", "매니저임", "01054325432", Auth.ROLE_MANAGER);
            memberRepository.save(manager);

            Member member2 = createMember("dkfma3@naver.com", "dkfmaPassword3!", "카이야", "01001140114", Auth.ROLE_USER);
            memberRepository.save(member2);

            Member member3 = createMember("dkfma4@naver.com", "dkfmaPassword4!", "큥이", "01005060506", Auth.ROLE_USER);
            memberRepository.save(member3);

            Member member4 = createMember("dkfma5@naver.com", "dkfmaPassword5!", "됴됴", "01005060506", Auth.ROLE_USER);
            memberRepository.save(member4);

            Member member5 = createMember("dkfma6@naver.com", "dkfmaPassword6!", "됴됴1", "01005060106", Auth.ROLE_USER);
            memberRepository.save(member5);

            Member member6 = createMember("dkfma7@naver.com", "dkfmaPassword7!", "됴됴2", "01005060206", Auth.ROLE_USER);
            memberRepository.save(member6);

            Member member7 = createMember("dkfma8@naver.com", "dkfmaPassword8!", "여섯", "01005060226", Auth.ROLE_USER);
            memberRepository.save(member7);

            IdolCategory idolCategory1 = createIdolCategory("EXO", "https://image.bugsm.co.kr/artist/images/130/801559/80155940.jpg?version=20230708002114.0", "댄스/팝", "그룹 (남성)", member1);
            idolCategoryRepository.save(idolCategory1);

            IdolCategory idolCategory2 = createIdolCategory("카이 (KAI)", "https://image.bugsm.co.kr/artist/images/130/801246/80124632.jpg?version=20230708002114.0", "댄스/팝", "솔로 (남성)", member2);
            idolCategoryRepository.save(idolCategory2);

            IdolCategory idolCategory3 = createIdolCategory("디오 (D.O.)", "https://image.bugsm.co.kr/artist/images/130/802033/80203317.jpg?version=20230914002122.0", "포크/어쿠스틱", "솔로 (남성)", member3);
            idolCategoryRepository.save(idolCategory3);

            IdolCategory idolCategory4 = createIdolCategory("백현(BAEKHYUN)", "https://image.bugsm.co.kr/artist/images/130/801202/80120269.jpg?version=20230708002114.0", "알앤비/소울", "솔로 (남성)", member4);
            idolCategoryRepository.save(idolCategory4);

            IdolCategory idolCategory5 = createIdolCategory("육성재 (비투비)", "https://image.bugsm.co.kr/artist/images/130/801234/80123436.jpg?version=20230503064029.0", "댄스/팝, 발라드", "솔로 (남성)", member5);
            idolCategoryRepository.save(idolCategory5);

            IdolCategory idolCategory6 = createIdolCategory("서은광 (비투비)", "https://image.bugsm.co.kr/artist/images/130/801234/80123430.jpg?version=20230901002126.0", "발라드", "솔로 (남성)", member6);
            idolCategoryRepository.save(idolCategory6);

            JoinIdol joinIdol = JoinIdol.createJoinIdol(member1, idolCategory1);
            joinIdolRepository.save(joinIdol);


            JoinIdol joinIdol2 = JoinIdol.createJoinIdol(member1, idolCategory2);
            joinIdolRepository.save(joinIdol2);

            JoinIdol joinIdol3 = JoinIdol.createJoinIdol(member4, idolCategory2);
            joinIdolRepository.save(joinIdol3);

            JoinIdol joinIdol4 = JoinIdol.createJoinIdol(member2, idolCategory2);
            joinIdolRepository.save(joinIdol4);
        }

        public Member createMember(String username, String password, String nickname, String phoneNum, Auth auth){
            return Member.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .nickname(nickname)
                    .phoneNum(phoneNum)
                    .role(auth)
                    .socialType(SocialType.DEFAULT)
                    .build();
        }

        public IdolCategory createIdolCategory(String artist, String artistImg, String artistGenre, String artistType, Member member){
            return IdolCategory.builder()
                    .artist(artist)
                    .artistImg(artistImg)
                    .artistGenre(artistGenre)
                    .artistType(artistType)
                    .member(member)
                    .build();
        }
    }
}