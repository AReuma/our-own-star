package com.example.backend.member.service;

import com.example.backend.artistBoard.dto.PostListResponseDTO;
import com.example.backend.artistBoard.dto.PostListVoteResponseDTO;
import com.example.backend.artistBoard.dto.market.MarketPostListResponseDTO;
import com.example.backend.artistBoard.entity.*;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoard;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardLike;
import com.example.backend.artistBoard.repository.ArtistBoardBookmarkRepository;
import com.example.backend.artistBoard.repository.ArtistBoardLikeRepository;
import com.example.backend.artistBoard.repository.market.ArtistMarketBoardLikeRepository;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.idolCategory.repository.IdolCategoryRepository;
import com.example.backend.member.dto.*;
import com.example.backend.member.dto.mypage.UserInfoCommentResponse;
import com.example.backend.member.entity.Auth;
import com.example.backend.member.entity.JoinIdol;
import com.example.backend.member.entity.Member;
import com.example.backend.member.entity.SocialType;
import com.example.backend.member.repository.JoinIdolRepository;
import com.example.backend.member.repository.MemberRedisRepository;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.security.jwt.JWTUtil;
import com.example.backend.security.jwt.email.provider.EmailProvider;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.backend.artistBoard.entity.QArtistBoard.artistBoard;
import static com.example.backend.artistBoard.entity.QArtistBoardComment.artistBoardComment;
import static com.example.backend.artistBoard.entity.QArtistBoardLike.artistBoardLike;
import static com.example.backend.artistBoard.entity.QArtistFollow.artistFollow;
import static com.example.backend.artistBoard.entity.market.QArtistMarketBoard.artistMarketBoard;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final EmailProvider emailProvider;
    private final MemberRedisRepository memberRedisRepository;
    private final IdolCategoryRepository idolCategoryRepository;
    private final JoinIdolRepository joinIdolRepository;
    private final ArtistBoardLikeRepository artistBoardLikeRepository;
    private final ArtistBoardBookmarkRepository artistBoardBookmarkRepository;
    private final ArtistMarketBoardLikeRepository artistMarketBoardLikeRepository;
    private final EntityManager em;

    @Override
    public ResponseEntity<LoginResponseDTO> login(String username, String password) {
        Member findMember = memberRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER_ID, "회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, findMember.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "잘못된 비밀번호입니다.");
        }

        String accessToken = jwtUtil.createAccessToken(findMember.getUsername(), findMember.getNickname(), findMember.getRole().toString());
        String refreshToken = jwtUtil.createRefreshToken(findMember.getUsername(), findMember.getNickname(), findMember.getRole().toString());

        return ResponseEntity.ok(new LoginResponseDTO(accessToken, refreshToken));
    }

    @Transactional
    @Override
    public ResponseEntity<String> register(String username, String password, String nickname, String phoneNum) {

        memberRepository.findByUsername(username)
                .ifPresent(member -> { throw new AppException(ErrorCode.USER_DUPLICATED, "중복된 아이디입니다.");});
        memberRepository.findByPhoneNum(phoneNum)
                .ifPresent(member -> { throw new AppException(ErrorCode.USER_DUPLICATED, "이미 사용한 전화번호입니다.");});

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .phoneNum(phoneNum)
                .socialType(SocialType.DEFAULT)
                .role(Auth.ROLE_USER)
                .build();

        memberRepository.save(member);
        return ResponseEntity.ok().body("회원가입 성공");
    }

    @Override
    public ResponseEntity<Boolean> checkUsername(String username) {
        memberRepository.findByUsername(username)
                .ifPresent(member -> {
                    throw new AppException(ErrorCode.USER_DUPLICATED, "사용 불가능한 아이디입니다.");
                });

        return ResponseEntity.ok().body(Boolean.TRUE);
    }

    @Override
    public ResponseEntity<String> emailCertification(EmailCertificationRequestDTO dto) {

        String username = dto.getUsername();

        boolean isExistId = memberRepository.existsByUsername(username);
        if (isExistId) throw new AppException(ErrorCode.USER_DUPLICATED, "사용 불가능한 아이디입니다.");

        // 인증코드
        String certificationNumber = getCertificationNumber();

        boolean isSuccessed = emailProvider.sendCertificationMail(username, certificationNumber);
        if(!isSuccessed) throw new AppException(ErrorCode.EMAIL_SEND_FAIL, "email send fail");

        memberRedisRepository.save(new EmailCertificationResponseDTO(username, certificationNumber));
        return ResponseEntity.ok().body("성공");
    }

    @Override
    public ResponseEntity<Boolean> emailCertificationCode(String username, String code) {

        EmailCertificationResponseDTO findEmailCertificationCode = memberRedisRepository.findById(username).orElseThrow(() -> new AppException(ErrorCode.CACHE_DATA_NOT_EXISTED, "Cache Data is not existed"));

        log.info("findEmailCertificationCode: {}", findEmailCertificationCode.getCode());

        if (findEmailCertificationCode.getCode().equals(code)){
            return ResponseEntity.ok().body(Boolean.TRUE);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Boolean.FALSE);
    }

    @Override
    public ResponseEntity<OtherUserInfoResponse> getOtherUserInfo(String artist, String nickname, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 찾는 회원이 카테고리에 가입된 회원인지 확인
        JoinIdol otherUser = checkJoinMemberByNickname(nickname, artist);
        JoinIdol user = checkJoinMemberByUsername(username, artist);


        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // 찾는 회원이 팔로우 한 회원 횟수 찾기
        int follower = Math.toIntExact(queryFactory.selectFrom(artistFollow)
                .where(artistFollow.follower.eq(otherUser))
                .fetchCount());

        // 찾는 회원을 팔로우 한 회원 횟수 찾기
        int following = Math.toIntExact(queryFactory.selectFrom(artistFollow)
                .where(artistFollow.following.eq(otherUser))
                .fetchCount());

        ArtistFollow isFollow = queryFactory.selectFrom(artistFollow)
                .where(artistFollow.following.eq(otherUser)
                        .and(artistFollow.follower.eq(user))
                ).fetchOne();

        return ResponseEntity.ok(new OtherUserInfoResponse(otherUser.getNickname(), otherUser.getJoinIdolMemberProfile().getImgUrl(), otherUser.getUserInfo(), follower, following, isFollow != null));
    }

    @Override
    public ResponseEntity<List<PostListResponseDTO>> getOtherUserInfoBoard(String artist, String nickname, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol otherUser = checkJoinMemberByNickname(nickname, artist);
        JoinIdol user = checkJoinMemberByUsername(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<Tuple> fetch = queryFactory
                .select(
                        artistBoard.boardType,
                        artistBoard
                )
                .from(artistBoard)
                .where(
                        artistBoard.writer.eq(otherUser)
                )
                .orderBy(artistBoard.createdDate.desc())
                //.offset(4L * (page -1))
                //.limit(4)
                .fetch();

        //select * from artist_board LEFT JOIN artist_follow on artist_follow.following_id = artist_board.join_idol_id where follower_id = 6 or join_idol_id = 6;
        List<PostListResponseDTO> responseDTOs = new ArrayList<>();

        for (Tuple tuple : fetch) {
            BoardType boardType = tuple.get(artistBoard.boardType);
            ArtistBoard findArtistBoard = tuple.get(artistBoard);

            Optional<ArtistBoardLike> boardLickCheck = artistBoardLikeRepository.findByArtistBoardIdAndMemberNickname(findArtistBoard.getId(), user.getNickname());
            Optional<ArtistBoardBookmark> boardBookmark = artistBoardBookmarkRepository.findByArtistBoardIdAndMemberNickname(findArtistBoard.getId(), user.getNickname());

            Optional<PostListVoteResponseDTO> postListVoteResponseDTO = Optional.empty();
            Optional<List<String>> image = Optional.empty();

            switch (boardType){
                case VOTE -> postListVoteResponseDTO = createPostListVoteResponseDTO(findArtistBoard);
                case IMAGE -> image = createImageList(findArtistBoard);
            }

            int artistBoardCommentCount = 0;
            if (findArtistBoard.getArtistBoardComments().size() > 0){
                artistBoardCommentCount
                        = findArtistBoard.getArtistBoardComments().size();
            }

            assert findArtistBoard != null;
            responseDTOs.add(new PostListResponseDTO(
                    findArtistBoard.getId(),
                    findArtistBoard.getContent(),
                    findArtistBoard.getWriter().getNickname(),
                    findArtistBoard.getCreatedDate(),
                    boardType,
                    findArtistBoard.getWriter().getJoinIdolMemberProfile().getImgUrl(),
                    boardLickCheck.isPresent(),
                    findArtistBoard.getLikeCount(),
                    boardBookmark.isPresent(),
                    findArtistBoard.getBookmarkCount(),
                    artistBoardCommentCount,
                    postListVoteResponseDTO,
                    image));
            //responseDTOs.add(new PostResponseDTO(findArtistBoard.getContent(), findArtistBoard.getWriter().getNickname(), findArtistBoard.getCreatedDate(), boardType, postVoteResponseDTO, image));
        }

        return ResponseEntity.ok(responseDTOs);
    }

    @Override
    public ResponseEntity<List<UserInfoCommentResponse>> getUserInfoMyComment(String artist, String nickname, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol otherUser = checkJoinMemberByNickname(nickname, artist);
        JoinIdol user = checkJoinMemberByUsername(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<ArtistBoardComment> commentList = queryFactory.selectFrom(artistBoardComment)
                .where(artistBoardComment.writer.eq(otherUser))
                .orderBy(artistBoardComment.createdDate.desc())
                .fetch();

        List<UserInfoCommentResponse> responses = new ArrayList<>();

        for (ArtistBoardComment comment : commentList) {
            // 댓글 자식 갯수 세야함.
            int commentSize = comment.getChildren().size();
            Optional<ArtistBoardLike> boardLickCheck = artistBoardLikeRepository.findByArtistBoardIdAndMemberNickname(comment.getArtistBoard().getId(), user.getNickname());
            Optional<ArtistBoardBookmark> boardBookmark = artistBoardBookmarkRepository.findByArtistBoardIdAndMemberNickname(comment.getArtistBoard().getId(), user.getNickname());

            if(!comment.getIsDeleted()) {
                responses.add(new UserInfoCommentResponse(comment.getArtistBoard().getId(), comment.getId(), comment.getContent(), comment.getWriter().getNickname(), comment.getCreatedDate(),
                        comment.getWriter().getJoinIdolMemberProfile().getImgUrl(), boardLickCheck.isPresent(), comment.getLikeCount(), boardBookmark.isPresent(), comment.getBookmarkCount(), commentSize));
            }
        }

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<List<PostListResponseDTO>> getUserInfoMyLike(String artist, String nickname, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol otherUser = checkJoinMemberByNickname(nickname, artist);
        JoinIdol user = checkJoinMemberByUsername(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<ArtistBoardLike> fetch = queryFactory.selectFrom(
                        artistBoardLike)
                .where(artistBoardLike.member.eq(otherUser))
                .orderBy(artistBoardLike.artistBoard.createdDate.desc())
                .fetch();

        List<PostListResponseDTO> responseDTOs = new ArrayList<>();

        for (ArtistBoardLike tuple : fetch) {
            BoardType boardType = tuple.getArtistBoard().getBoardType();
            ArtistBoard findArtistBoard = tuple.getArtistBoard();

            Optional<ArtistBoardLike> boardLickCheck = artistBoardLikeRepository.findByArtistBoardIdAndMemberNickname(findArtistBoard.getId(), user.getNickname());
            Optional<ArtistBoardBookmark> boardBookmark = artistBoardBookmarkRepository.findByArtistBoardIdAndMemberNickname(findArtistBoard.getId(), user.getNickname());

            Optional<PostListVoteResponseDTO> postListVoteResponseDTO = Optional.empty();
            Optional<List<String>> image = Optional.empty();

            switch (boardType){
                case VOTE -> postListVoteResponseDTO = createPostListVoteResponseDTO(findArtistBoard);
                case IMAGE -> image = createImageList(findArtistBoard);
            }

            int artistBoardCommentCount = 0;
            if (findArtistBoard.getArtistBoardComments().size() > 0){
                artistBoardCommentCount
                        = findArtistBoard.getArtistBoardComments().size();
            }

            assert findArtistBoard != null;
            responseDTOs.add(new PostListResponseDTO(
                    findArtistBoard.getId(),
                    findArtistBoard.getContent(),
                    findArtistBoard.getWriter().getNickname(),
                    findArtistBoard.getCreatedDate(),
                    boardType,
                    findArtistBoard.getWriter().getJoinIdolMemberProfile().getImgUrl(),
                    boardLickCheck.isPresent(),
                    findArtistBoard.getLikeCount(),
                    boardBookmark.isPresent(),
                    findArtistBoard.getBookmarkCount(),
                    artistBoardCommentCount,
                    postListVoteResponseDTO,
                    image));
            //responseDTOs.add(new PostResponseDTO(findArtistBoard.getContent(), findArtistBoard.getWriter().getNickname(), findArtistBoard.getCreatedDate(), boardType, postVoteResponseDTO, image));
        }

        return ResponseEntity.ok(responseDTOs);
    }

    @Override
    public ResponseEntity<List<MarketPostListResponseDTO>> getUserInfoMyMarket(String artist, String nickname, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol otherUser = checkJoinMemberByNickname(nickname, artist);
        JoinIdol user = checkJoinMemberByUsername(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<ArtistMarketBoard> marketBoardList = queryFactory
                .selectFrom(artistMarketBoard)
                .where(
                        artistMarketBoard.writer.eq(otherUser)
                )
                .orderBy(artistMarketBoard.createdDate.desc())
                //.offset(12L * (page - 1))
                //.limit(12)
                .fetch();

        List<MarketPostListResponseDTO> responseDTOList = new ArrayList<>();
        for (ArtistMarketBoard marketBoard : marketBoardList) {
            Optional<ArtistMarketBoardLike> isLike = artistMarketBoardLikeRepository.findArtistMarketBoardLikeByArtistMarketBoardIdAndMemberId(marketBoard.getId(), user.getId());

            MarketPostListResponseDTO marketPostListResponseDTO = new MarketPostListResponseDTO(marketBoard.getId(), marketBoard.getTitle(), marketBoard.getPrice(), isLike.isPresent(), marketBoard.getImageUrlList().get(0).getArtistMarketBoardImgURL());
            responseDTOList.add(marketPostListResponseDTO);
        }

        return ResponseEntity.ok(responseDTOList);
    }

    @Override
    public ResponseEntity<LoginResponseDTO> renewAccessToken(String refreshToken) {
        // refreshToken 만료 확인
        boolean isExpire = jwtUtil.refreshTokenExpired(refreshToken);
        if(isExpire){ // 만료됨.
            // accessToken 새로 발급받아야함.
            throw new AppException(ErrorCode.EXPIRED_REFRESH_TOKEN, "refreshToken 만료");
        }else {
            Claims claims = jwtUtil.parseRefreshToken(refreshToken);
            String username = claims.get("username", String.class);
            String nickname = claims.get("nickname", String.class);
            String role = claims.get("role", String.class);
            String accessToken = jwtUtil.createAccessToken(username, nickname, role);

            return ResponseEntity.ok(new LoginResponseDTO(accessToken, refreshToken));
        }
    }

    private void getIdolCategoryByArtist(String artist){
        idolCategoryRepository.findByArtist(artist).orElseThrow(() -> new AppException(ErrorCode.IDOL_CATEGORY_NOT_FOUND, "아티스트가 존재하지않습니다"));
    }

    private JoinIdol checkJoinMemberByNickname(String nickname, String artist){
        return joinIdolRepository.findJoinIdolByNicknameAndIdolCategoryArtist(nickname, artist).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_JOIN_IDOL_CATEGORY, "찾는 회원은 존재하지않는 회원입니다."));
    }

    private JoinIdol checkJoinMemberByUsername(String username, String artist){
        return joinIdolRepository.findJoinIdolByMemberUsernameAndIdolCategoryArtist(username, artist).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_JOIN_IDOL_CATEGORY, "존재하지않는 회원입니다."));
    }

    public String getCertificationNumber(){
        StringBuilder certificationNumber = new StringBuilder();

        for (int count = 0; count < 4; count++){
            certificationNumber.append((int) (Math.random() * 10));
        }

        return certificationNumber.toString();
    }

    private Optional<PostListVoteResponseDTO> createPostListVoteResponseDTO(ArtistBoard findArtistBoard) {
        ArtistBoardVote artistBoardVote = findArtistBoard.getArtistBoardVote();

        List<String> choice = new ArrayList<>();
        choice.add(artistBoardVote.getChoice1());
        choice.add(artistBoardVote.getChoice2());
        choice.add(artistBoardVote.getChoice3());
        choice.add(artistBoardVote.getChoice4());


        return Optional.of(new PostListVoteResponseDTO(
                choice,
                artistBoardVote.getChoiceTotalCount(),
                artistBoardVote.getVoteExpireTime()));
    }

    private Optional<List<String>> createImageList(ArtistBoard findArtistBoard) {
        List<String> imageList = new ArrayList<>();
        for (ArtistBoardImage boardImage : findArtistBoard.getImageUrlList()) {
            imageList.add(boardImage.getArtistBoardImgURL());
        }
        return Optional.of(imageList);
    }
}
