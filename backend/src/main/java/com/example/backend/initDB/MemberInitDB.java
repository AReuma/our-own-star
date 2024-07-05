package com.example.backend.initDB;
import com.example.backend.artistBoard.entity.*;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoard;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardImage;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardLike;
import com.example.backend.artistBoard.repository.ArtistBoardImageRepository;
import com.example.backend.artistBoard.repository.ArtistBoardRepository;
import com.example.backend.artistBoard.repository.ArtistBoardVoteRepository;
import com.example.backend.artistBoard.repository.ArtistFollowRepository;
import com.example.backend.artistBoard.repository.market.ArtistMarketBoardImageRepository;
import com.example.backend.artistBoard.repository.market.ArtistMarketBoardLikeRepository;
import com.example.backend.artistBoard.repository.market.ArtistMarketBoardRepository;
import com.example.backend.idolCategory.entity.IdolCategory;
import com.example.backend.idolCategory.repository.IdolCategoryRepository;
import com.example.backend.member.entity.*;
import com.example.backend.member.repository.JoinIdolMemberProfileRepository;
import com.example.backend.member.repository.JoinIdolRepository;
import com.example.backend.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MemberInitDB {
    private final InitService initService;

    @PostConstruct
    public void init() {
        //initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final MemberRepository memberRepository;
        private final PasswordEncoder passwordEncoder;
        private final IdolCategoryRepository idolCategoryRepository;
        private final JoinIdolRepository joinIdolRepository;
        private final ArtistBoardRepository artistBoardRepository;
        private final ArtistFollowRepository artistFollowRepository;
        private final ArtistBoardImageRepository artistBoardImageRepository;
        private final ArtistBoardVoteRepository artistBoardVoteRepository;
        private final ArtistMarketBoardRepository artistMarketBoardRepository;
        private final ArtistMarketBoardImageRepository artistMarketBoardImageRepository;
        private final ArtistMarketBoardLikeRepository artistMarketBoardLikeRepository;
        private final JoinIdolMemberProfileRepository joinIdolMemberProfileRepository;

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

            JoinIdolMemberProfile joinIdolMemberProfile1 = JoinIdolMemberProfile.createJoinIdolMemberProfile();
            joinIdolMemberProfileRepository.save(joinIdolMemberProfile1);

            JoinIdol joinIdol = JoinIdol.createJoinIdol(member1, idolCategory1, joinIdolMemberProfile1);
            joinIdolRepository.save(joinIdol);

            JoinIdolMemberProfile joinIdolMemberProfile2 = JoinIdolMemberProfile.createJoinIdolMemberProfile();
            joinIdolMemberProfileRepository.save(joinIdolMemberProfile2);

            JoinIdol joinIdol2 = JoinIdol.createJoinIdol(member1, idolCategory2, joinIdolMemberProfile2);
            joinIdolRepository.save(joinIdol2);

            JoinIdolMemberProfile joinIdolMemberProfile3 = JoinIdolMemberProfile.createJoinIdolMemberProfile();
            joinIdolMemberProfileRepository.save(joinIdolMemberProfile3);

            JoinIdol joinIdol3 = JoinIdol.createJoinIdol(member4, idolCategory2, joinIdolMemberProfile3);
            joinIdolRepository.save(joinIdol3);

            JoinIdolMemberProfile joinIdolMemberProfile4 = JoinIdolMemberProfile.createJoinIdolMemberProfile();
            joinIdolMemberProfileRepository.save(joinIdolMemberProfile4);

            JoinIdol joinIdol4 = JoinIdol.createJoinIdol(member2, idolCategory2, joinIdolMemberProfile4);
            joinIdolRepository.save(joinIdol4);

            JoinIdolMemberProfile joinIdolMemberProfile5 = JoinIdolMemberProfile.createJoinIdolMemberProfile();
            joinIdolMemberProfileRepository.save(joinIdolMemberProfile5);

            JoinIdol joinIdol5 = JoinIdol.createJoinIdol(member5, idolCategory2, joinIdolMemberProfile5);
            joinIdolRepository.save(joinIdol5);

            JoinIdolMemberProfile joinIdolMemberProfile6 = JoinIdolMemberProfile.createJoinIdolMemberProfile();
            joinIdolMemberProfileRepository.save(joinIdolMemberProfile6);

            JoinIdol joinIdol6 = JoinIdol.createJoinIdol(member6, idolCategory2, joinIdolMemberProfile6);
            joinIdolRepository.save(joinIdol6);

            JoinIdolMemberProfile joinIdolMemberProfile7 = JoinIdolMemberProfile.createJoinIdolMemberProfile();
            joinIdolMemberProfileRepository.save(joinIdolMemberProfile7);

            JoinIdol joinIdol7 = JoinIdol.createJoinIdol(member7, idolCategory2, joinIdolMemberProfile7);
            joinIdolRepository.save(joinIdol7);

            JoinIdolMemberProfile joinIdolMemberProfile8 = JoinIdolMemberProfile.createJoinIdolMemberProfile();
            joinIdolMemberProfileRepository.save(joinIdolMemberProfile8);

            JoinIdol joinIdol8 = JoinIdol.createJoinIdol(member5, idolCategory1, joinIdolMemberProfile8);
            joinIdolRepository.save(joinIdol8);

            // 팔로우
            ArtistFollow artistFollow1 = followMember(joinIdol4, joinIdol2);
            artistFollowRepository.save(artistFollow1);

            ArtistFollow artistFollow2 = followMember(joinIdol4, joinIdol3);
            artistFollowRepository.save(artistFollow2);

            ArtistFollow artistFollow3 = followMember(joinIdol8, joinIdol);
            artistFollowRepository.save(artistFollow3);

            ArtistFollow artistFollow4 = followMember(joinIdol6, joinIdol2);
            artistFollowRepository.save(artistFollow4);

            ArtistFollow artistFollow5 = followMember(joinIdol6, joinIdol3);
            artistFollowRepository.save(artistFollow5);

            ArtistFollow artistFollow6 = followMember(joinIdol6, joinIdol4);
            artistFollowRepository.save(artistFollow6);

            ArtistFollow artistFollow7 = followMember(joinIdol8, joinIdol6);
            artistFollowRepository.save(artistFollow7);

            // 게시글 작성
            ArtistBoard artistBoard1 = ArtistBoard.createArtistBoard("content1, dkfma1@naver.com", joinIdol2, BoardType.DEFAULT);
            artistBoardRepository.save(artistBoard1);

            for (int i = 0; i < 100; i++) {
                String content = "likeCount: "+i;

                ArtistBoard artistBoard = createArtistBoard(content, joinIdol2, BoardType.DEFAULT, i);
                artistBoardRepository.save(artistBoard);
            }

            ArtistBoard artistBoard13 = ArtistBoard.createArtistBoard("content13 test, dkfma3@naver.com", joinIdol4, BoardType.DEFAULT);
            artistBoardRepository.save(artistBoard13);

            ArtistBoard artistBoard11 = ArtistBoard.createArtistBoard("NoKai, dkfma1@naver.com", joinIdol, BoardType.DEFAULT);
            artistBoardRepository.save(artistBoard11);

            ArtistBoard artistBoard2 = ArtistBoard.createArtistBoard("content2, dkfma5@naver.com, ", joinIdol3, BoardType.DEFAULT);
            artistBoardRepository.save(artistBoard2);

            ArtistBoard artistBoard3 = ArtistBoard.createArtistBoard("content3, dkfma6@naver.com", joinIdol5, BoardType.DEFAULT);
            artistBoardRepository.save(artistBoard3);

            ArtistBoard artistBoard4 = ArtistBoard.createArtistBoard("content4, dkfma7@naver.com", joinIdol6, BoardType.DEFAULT);
            artistBoardRepository.save(artistBoard4);

            ArtistBoard artistBoard5 = ArtistBoard.createArtistBoard("content5, dkfma8@naver.com", joinIdol7, BoardType.DEFAULT);
            artistBoardRepository.save(artistBoard5);

            ArtistBoard artistBoard6 = ArtistBoard.createArtistBoard("content6, dkfma5@naver.com, ", joinIdol3, BoardType.DEFAULT);
            artistBoardRepository.save(artistBoard6);

            ArtistBoard artistBoard12 = ArtistBoard.createArtistBoard("test, dkfma7@naver.com, ", joinIdol6, BoardType.IMAGE);
            ArtistBoardImage artistBoardImage5 = saveArtistBoardImg("post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/cd0ff55f-da83-4254-8ff2-481c91a3eefa.jpeg", artistBoard12);
            ArtistBoardImage artistBoardImage6 = saveArtistBoardImg("post/카이 (KAI)/dkfma3@naver.com/2024-04-25/images/5d782b13-46c1-453c-897f-6676d19b3fe1.png", artistBoard12);
            artistBoardImageRepository.save(artistBoardImage5);
            artistBoardImageRepository.save(artistBoardImage6);
            artistBoardRepository.save(artistBoard12);

            // 이미지 게시글
            ArtistBoard artistBoard7 = ArtistBoard.createArtistBoard("이날 무슨 날인지 아는사람?\n왜 경수가 없음?", joinIdol3, BoardType.IMAGE);
            ArtistBoardImage artistBoardImage1 = saveArtistBoardImg("post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/cd0ff55f-da83-4254-8ff2-481c91a3eefa.jpeg", artistBoard7);
            ArtistBoardImage artistBoardImage2 = saveArtistBoardImg("post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/5801fe98-e4b7-4208-aaca-7a407fed164e.jpeg", artistBoard7);
            artistBoardImageRepository.save(artistBoardImage1);
            artistBoardImageRepository.save(artistBoardImage2);
            artistBoardRepository.save(artistBoard7);

            ArtistBoard artistBoard10 = ArtistBoard.createArtistBoard("image2, dkfma7@naver.com, ", joinIdol6, BoardType.IMAGE);
            ArtistBoardImage artistBoardImage3 = saveArtistBoardImg("post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/d5cec1b1-18d7-4eeb-80f7-12a31c8c3e20.jpeg", artistBoard10);
            ArtistBoardImage artistBoardImage4 = saveArtistBoardImg("post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/d6fc5515-8c60-4755-8879-d303c623d981.jpeg", artistBoard10);
            artistBoardImageRepository.save(artistBoardImage3);
            artistBoardImageRepository.save(artistBoardImage4);
            artistBoardRepository.save(artistBoard10);

            // 투표 게시글
            LocalDateTime currentTime = LocalDateTime.now();
            //LocalDateTime expiryTime = currentTime.plusDays(1).plusHours(1);
            LocalDateTime expiryTime = currentTime.plusMinutes(2);

            ArtistBoard artistBoard8 = ArtistBoard.createArtistBoard("종인이 닮은 동물은 뭐라고 생가함?", joinIdol2, BoardType.VOTE);
            artistBoardRepository.save(artistBoard8);
            ArtistBoardVote vote1 = createVote("곰돌이🐻", "고양이🐈‍⬛", "트위치🐥", "", 3, artistBoard8, expiryTime);
            artistBoardVoteRepository.save(vote1);

            ArtistBoard artistBoard9 = ArtistBoard.createArtistBoard("vote2, dkfma8@naver.com, ", joinIdol7, BoardType.VOTE);
            artistBoardRepository.save(artistBoard9);
            ArtistBoardVote vote2 = createVote("선택지5", "선택지6", "선택지7", "선택지8", 4, artistBoard9, expiryTime);
            artistBoardVoteRepository.save(vote2);

            //joinidol2, 3, 4, 5, 6, 7
            // 판매 게시글
            ArtistMarketBoard artistMarketBoard1 = ArtistMarketBoard.createArtistMarketBoard("카이 포카 판매", "카이 포카 판매합니다 \n연락주세요!", joinIdol2, "-1");
            artistMarketBoardRepository.save(artistMarketBoard1);
            ArtistMarketBoardImage artistMarketBoardImage1 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/b292dd8c-fc2c-42e1-8b0a-7e848936fb25.jpeg", artistMarketBoard1);
            ArtistMarketBoardImage artistMarketBoardImage2 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/ed30c837-cd46-4356-a004-dbce634fd861.jpeg", artistMarketBoard1);
            artistMarketBoardImageRepository.save(artistMarketBoardImage1);
            artistMarketBoardImageRepository.save(artistMarketBoardImage2);

            ArtistMarketBoardLike boardLike1 = ArtistMarketBoardLike.createMarketBoardLike(artistMarketBoard1, joinIdol6);
            artistMarketBoardLikeRepository.save(boardLike1);

            ArtistMarketBoardLike boardLike2 = ArtistMarketBoardLike.createMarketBoardLike(artistMarketBoard1, joinIdol7);
            artistMarketBoardLikeRepository.save(boardLike2);

            ArtistMarketBoard artistMarketBoard2 = ArtistMarketBoard.createArtistMarketBoard("백현 굿즈 판매", "백현 굿즈 판매\n일괄 5.0", joinIdol6, "50000");
            artistMarketBoardRepository.save(artistMarketBoard2);
            ArtistMarketBoardImage artistMarketBoardImage3 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/c6069201-27e7-4bf5-ae7e-f50f76a083c8.jpeg", artistMarketBoard2);
            ArtistMarketBoardImage artistMarketBoardImage9 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/c6069201-27e7-4bf5-ae7e-f50f76a083c8.jpeg", artistMarketBoard2);
            ArtistMarketBoardImage artistMarketBoardImage4 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/26773fed-3da5-43bd-a966-79297325ed38.jpeg", artistMarketBoard2);
            artistMarketBoardImageRepository.save(artistMarketBoardImage3);
            artistMarketBoardImageRepository.save(artistMarketBoardImage4);
            artistMarketBoardImageRepository.save(artistMarketBoardImage9);

            ArtistMarketBoardLike boardLike3 = ArtistMarketBoardLike.createMarketBoardLike(artistMarketBoard2, joinIdol4);
            artistMarketBoardLikeRepository.save(boardLike3);

            ArtistMarketBoard artistMarketBoard3 = ArtistMarketBoard.createArtistMarketBoard("백현 포카 판매", "백현 포카 판매합니다.\n" +
                    "가격은 저렴한건 0.2 ~ 부터 있어요.\n\n해외 배송 가능하지만 한국 주소있는 분 우대로 판매하겠습니다. \n연락주세요\n2개 이상 거래 합니다", joinIdol7, "3000");
            artistMarketBoardRepository.save(artistMarketBoard3);
            ArtistMarketBoardImage artistMarketBoardImage5 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg", artistMarketBoard3);
            artistMarketBoardImageRepository.save(artistMarketBoardImage5);

            ArtistMarketBoard artistMarketBoard4 = ArtistMarketBoard.createArtistMarketBoard("경수 포카 판매", "경수 포카 판매합니다.\n" +
                    "가격은 저렴한건 0.2 ~ 부터 있어요.\n\n해외 배송 가능하지만 한국 주소있는 분 우대로 판매하겠습니다. \n연락주세요\n2개 이상 거래 합니다", joinIdol7, "6000");
            artistMarketBoardRepository.save(artistMarketBoard4);
            ArtistMarketBoardImage artistMarketBoardImage6 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg", artistMarketBoard4);
            artistMarketBoardImageRepository.save(artistMarketBoardImage6);

            String[] memberName = {"수호", "준면", "시우민", "민석", "변백현", "세훈", "오세훈","수호", "준면", "시우민", "백현", "도경수", "두두", "김종인",  "니니", "민석", "변백현", "백현", "도경수", "두두", "김종인",  "니니", "세훈", "오세훈"};
            String[] priceList = {"6000", "5000", "9000", "60000", "6000", "12000", "-1", "5000", "9000", "12000", "-1", "36000", "43000", "54000",  "21000", "12000", "1000", "60000", "36000", "43000", "54000",  "21000", "12000", "1000"};
            String[] imgList = {"post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/d6fc5515-8c60-4755-8879-d303c623d981.jpeg", "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/d5cec1b1-18d7-4eeb-80f7-12a31c8c3e20.jpeg",
                    "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg", "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/5801fe98-e4b7-4208-aaca-7a407fed164e.jpeg",
                    "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/cd0ff55f-da83-4254-8ff2-481c91a3eefa.jpeg", "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg",
                    "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/ed30c837-cd46-4356-a004-dbce634fd861.jpeg", "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/cd0ff55f-da83-4254-8ff2-481c91a3eefa.jpeg",
                    "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/d6fc5515-8c60-4755-8879-d303c623d981.jpeg", "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg",
                    "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg", "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/5801fe98-e4b7-4208-aaca-7a407fed164e.jpeg",
                    "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/d6fc5515-8c60-4755-8879-d303c623d981.jpeg", "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/d5cec1b1-18d7-4eeb-80f7-12a31c8c3e20.jpeg",
                    "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/ed30c837-cd46-4356-a004-dbce634fd861.jpeg", "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg",
                    "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/5801fe98-e4b7-4208-aaca-7a407fed164e.jpeg",  "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg",
                    "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/ed30c837-cd46-4356-a004-dbce634fd861.jpeg", "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/5801fe98-e4b7-4208-aaca-7a407fed164e.jpeg",
                    "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg",  "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/614a71fa-1e7a-4ab1-938f-90f20a9a0de3.jpeg",
                    "marketPost/카이 (KAI)/dkfma7@naver.com/2024-04-28/images/ed30c837-cd46-4356-a004-dbce634fd861.jpeg", "post/카이 (KAI)/dkfma3@naver.com/2024-01-31/images/5801fe98-e4b7-4208-aaca-7a407fed164e.jpeg"
            };
            for (int i = 0; i < 24; i++){
                ArtistMarketBoard artistMarketBoard5 = ArtistMarketBoard.createArtistMarketBoard(memberName[i] + " 포카 판매", i+"경수 포카 판매합니다.\n가격은 저렴한건 0.2 ~ 부터 있어요.\n\n해외 배송 가능하지만 한국 주소있는 분 우대로 판매하겠습니다. \n연락주세요\n2개 이상 거래 합니다", joinIdol6, priceList[i]);
                artistMarketBoardRepository.save(artistMarketBoard5);

                ArtistMarketBoardImage artistMarketBoardImage7 = ArtistMarketBoardImage.saveArtistBoardImg(imgList[i], artistMarketBoard5);
                artistMarketBoardImageRepository.save(artistMarketBoardImage7);

                if (i % 2 != 0){
                    ArtistMarketBoardLike boardLike4= ArtistMarketBoardLike.createMarketBoardLike(artistMarketBoard5, joinIdol4);
                    artistMarketBoardLikeRepository.save(boardLike4);
                }

                /*if (i % 2 == 0){
                    ArtistMarketBoardLike boardLike4= ArtistMarketBoardLike.createMarketBoardLike(artistMarketBoard5, joinIdol6);
                    artistMarketBoardLikeRepository.save(boardLike4);
                }*/
            }

            ArtistMarketBoard artistMarketBoard10 = ArtistMarketBoard.createArtistMarketBoard("경수 포카 판매", "경수 포카 판매합니다.\n" +
                    "가격은 저렴한건 0.2 ~ 부터 있어요.\n\n해외 배송 가능하지만 한국 주소있는 분 우대로 판매하겠습니다. \n연락주세요\n2개 이상 거래 합니다", joinIdol4, "90000");
            artistMarketBoardRepository.save(artistMarketBoard10);
            ArtistMarketBoardImage artistMarketBoardImage10 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma3@naver.com/2024-06-24/images/8fde2538-0045-4061-bb0d-21daf7382e1d.jpeg", artistMarketBoard10);
            ArtistMarketBoardImage artistMarketBoardImage11 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma3@naver.com/2024-06-24/images/d9b6ad63-769d-4ad4-91b2-dce3443db8f5.jpeg", artistMarketBoard10);
            ArtistMarketBoardImage artistMarketBoardImage12 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma3@naver.com/2024-06-24/images/8fde2538-0045-4061-bb0d-21daf7382e1d.jpeg", artistMarketBoard10);
            ArtistMarketBoardImage artistMarketBoardImage13 = ArtistMarketBoardImage.saveArtistBoardImg("marketPost/카이 (KAI)/dkfma3@naver.com/2024-06-24/images/d9b6ad63-769d-4ad4-91b2-dce3443db8f5.jpeg", artistMarketBoard10);

            artistMarketBoardImageRepository.save(artistMarketBoardImage10);
            artistMarketBoardImageRepository.save(artistMarketBoardImage11);
            artistMarketBoardImageRepository.save(artistMarketBoardImage12);
            artistMarketBoardImageRepository.save(artistMarketBoardImage13);
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

        public ArtistBoard createArtistBoard(String content, JoinIdol joinIdol, BoardType boardType, int count){

            return ArtistBoard.builder()
                    .content(content)
                    .writer(joinIdol)
                    .boardType(boardType)
                    .likeCount(count)
                    .build();
        }

        public ArtistFollow followMember(JoinIdol follower, JoinIdol following){
            return ArtistFollow.builder()
                    .follower(follower)
                    .following(following)
                    .build();
        }

        public ArtistBoardImage saveArtistBoardImg(String imgUrl, ArtistBoard artistBoard){
            ArtistBoardImage boardImage = ArtistBoardImage.builder()
                    .artistBoard(artistBoard)
                    .artistBoardImgURL(imgUrl)
                    .build();

            artistBoard.addImage(boardImage);
            return boardImage;
        }

        public ArtistBoardVote createVote(String choice1, String choice2, String choice3, String choice4, int choiceCount, ArtistBoard artistBoard, LocalDateTime voteExpireTime ){
            ArtistBoardVote vote = ArtistBoardVote.builder()
                    .choice1(choice1)
                    .choice2(choice2)
                    .choice3(choice3)
                    .choice4(choice4)
                    .choiceTotalCount(choiceCount)
                    .artistBoard(artistBoard)
                    .voteExpireTime(voteExpireTime)
                    .build();

            artistBoard.addVote(vote);

            return vote;
        }
    }
}