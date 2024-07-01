package com.example.backend.member.service.myPage;

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
import com.example.backend.common.s3.FileDetail;
import com.example.backend.common.s3.S3FileUploadService;
import com.example.backend.idolCategory.repository.IdolCategoryRepository;
import com.example.backend.member.dto.mypage.UserInfoCommentResponse;
import com.example.backend.member.dto.mypage.UserInfoResponse;
import com.example.backend.member.entity.JoinIdol;
import com.example.backend.member.entity.JoinIdolMemberProfile;
import com.example.backend.member.repository.JoinIdolMemberProfileRepository;
import com.example.backend.member.repository.JoinIdolRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
public class MyPageServiceImpl implements MyPageService{

    private final IdolCategoryRepository idolCategoryRepository;
    private final JoinIdolRepository joinIdolRepository;
    private final ArtistBoardLikeRepository artistBoardLikeRepository;
    private final ArtistBoardBookmarkRepository artistBoardBookmarkRepository;
    private final ArtistMarketBoardLikeRepository artistMarketBoardLikeRepository;
    private final S3FileUploadService s3FileUploadService;
    private final JoinIdolMemberProfileRepository joinIdolMemberProfileRepository;
    private final EntityManager em;

    private void getIdolCategoryByArtist(String artist){
        idolCategoryRepository.findByArtist(artist).orElseThrow(() -> new AppException(ErrorCode.IDOL_CATEGORY_NOT_FOUND, "아티스트가 존재하지않습니다"));
    }

    private JoinIdol checkJoinMember(String username, String artist){
        return joinIdolRepository.findJoinIdolByMemberUsernameAndIdolCategoryArtist(username, artist).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_JOIN_IDOL_CATEGORY, "권한이 없는 회원입니다."));
    }
    @Override
    public ResponseEntity<UserInfoResponse> getUserInfo(String artist, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // 내가 팔로우 한 회원 횟수 찾기
        int follower = Math.toIntExact(queryFactory.selectFrom(artistFollow)
                .where(artistFollow.follower.eq(user))
                .fetchCount());

        // 나를 팔로우 한 회원 횟수 찾기
        int following = Math.toIntExact(queryFactory.selectFrom(artistFollow)
                .where(artistFollow.following.eq(user))
                .fetchCount());

        return ResponseEntity.ok(new UserInfoResponse(user.getNickname(), user.getJoinIdolMemberProfile().getImgUrl(), user.getUserInfo(), follower, following));
    }

    @Override
    public ResponseEntity<List<PostListResponseDTO>> getUserInfoMyBoard(String artist, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<Tuple> fetch = queryFactory
                .select(
                        artistBoard.boardType,
                        artistBoard
                )
                .from(artistBoard)
                .where(
                        artistBoard.writer.eq(user)
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
    public ResponseEntity<List<UserInfoCommentResponse>> getUserInfoMyComment(String artist, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<ArtistBoardComment> commentList = queryFactory.selectFrom(artistBoardComment)
                .where(artistBoardComment.writer.eq(user))
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
    public ResponseEntity<List<PostListResponseDTO>> getUserInfoMyLike(String artist, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<ArtistBoardLike> fetch = queryFactory.selectFrom(
                        artistBoardLike)
                .where(artistBoardLike.member.eq(user))
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
    public ResponseEntity<List<MarketPostListResponseDTO>> getUserInfoMyMarket(String artist, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<ArtistMarketBoard> marketBoardList = queryFactory
                .selectFrom(artistMarketBoard)
                .where(
                        artistMarketBoard.writer.eq(user)
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
    public ResponseEntity<Boolean> checkNicknameDub(String artist, String modifyNickname, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        checkJoinMember(username, artist);

        Optional<JoinIdol> checkNicknameDub = joinIdolRepository.findByNickname(modifyNickname);

        return ResponseEntity.ok(checkNicknameDub.isEmpty());
    }

    @Override
    @Transactional
    public ResponseEntity<String> modifyUserInfo(String artist, String nickname, String userInfo, MultipartFile userProfileImg, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        if(userProfileImg != null){ // 변경할 이미지가 있음.
            FileDetail fileDetail = FileDetail.multipartOf(userProfileImg);
            if(fileDetail.getName() != "basic_profile.jpg"){ // 기본 프로필이 아닐 경우
                // 원래 프로필 이미지 제거
                JoinIdolMemberProfile userProfile = user.getJoinIdolMemberProfile();
                s3FileUploadService.deleteMissingImages(userProfile.getImgUrl());
            }

            // 변경 된 이미지 s3에 추가
            String imgUrl = s3FileUploadService.userProfileStore(artist, fileDetail.getPath(), userProfileImg, username);

            // 변경할 db 찾기
            JoinIdolMemberProfile userProfile = user.getJoinIdolMemberProfile();
            userProfile.updateUserProfileImg(imgUrl);

            joinIdolMemberProfileRepository.save(userProfile);

        }

        user.updateUserInfo(nickname, userInfo);
        joinIdolRepository.save(user);

        return ResponseEntity.ok("회원 정보 변경 완료");
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
