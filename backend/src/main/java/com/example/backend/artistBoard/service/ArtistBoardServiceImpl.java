package com.example.backend.artistBoard.service;

import com.example.backend.artistBoard.dto.*;
import com.example.backend.artistBoard.dto.market.*;
import com.example.backend.artistBoard.entity.*;
import com.example.backend.artistBoard.entity.market.*;
import com.example.backend.artistBoard.repository.*;
import com.example.backend.artistBoard.repository.market.*;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.common.s3.FileDetail;
import com.example.backend.common.s3.S3FileUploadService;
import com.example.backend.idolCategory.dto.IdolCategoryResponseDTO;
import com.example.backend.idolCategory.entity.IdolCategory;
import com.example.backend.idolCategory.repository.IdolCategoryRepository;
import com.example.backend.member.entity.JoinIdol;
import com.example.backend.member.repository.JoinIdolRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.backend.artistBoard.dto.CommentResponseDTO.convertCommentToDto;
import static com.example.backend.artistBoard.dto.market.MarketCommentResponseDTO.convertMarketCommentToDto;
import static com.example.backend.artistBoard.entity.BoardType.VOTE;
import static com.example.backend.artistBoard.entity.QArtistBoard.artistBoard;
import static com.example.backend.artistBoard.entity.QArtistBoardComment.artistBoardComment;
import static com.example.backend.artistBoard.entity.QArtistFollow.artistFollow;
import static com.example.backend.artistBoard.entity.market.QArtistMarketBoard.artistMarketBoard;
import static com.example.backend.artistBoard.entity.market.QArtistMarketBoardComment.artistMarketBoardComment;
import static com.example.backend.artistBoard.entity.market.QArtistMarketBoardLike.*;
import static com.example.backend.idolCategory.entity.QIdolCategory.idolCategory;
import static com.example.backend.member.entity.QJoinIdol.joinIdol;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtistBoardServiceImpl implements ArtistBoardService {

    private final JoinIdolRepository joinIdolRepository;
    private final IdolCategoryRepository idolCategoryRepository;
    private final ArtistBoardRepository artistBoardRepository;
    private final ArtistBoardImageRepository artistBoardImageRepository;
    private final ArtistBoardVoteRepository artistBoardVoteRepository;
    private final ArtistBoardCommentRepository artistBoardCommentRepository;
    private final ArtistBoardLikeRepository artistBoardLikeRepository;
    private final ArtistBoardBookmarkRepository artistBoardBookmarkRepository;
    private final S3FileUploadService s3FileUploadService;
    private final ArtistMarketBoardRepository artistMarketBoardRepository;
    private final ArtistMarketBoardImageRepository artistMarketBoardImageRepository;
    private final ArtistMarketBoardLikeRepository artistMarketBoardLikeRepository;
    private final ArtistMarketBoardBookmarkRepository artistMarketBoardBookmarkRepository;
    private final ArtistMarketBoardCommentRepository artistMarketBoardCommentRepository;
    private final ArtistBoardCommentLikeRepository artistBoardCommentLikeRepository;
    private final ArtistBoardCommentBookmarkRepository artistBoardCommentBookmarkRepository;
    private final ArtistMarketCommentLikeRepository marketCommentLikeRepository;
    private final ArtistMarketCommentBookmarkRepository marketCommentBookmarkRepository;
    private final ArtistPlaceRepository artistPlaceRepository;
    private final RedisVoteService redisVoteService;
    private final RedisLikeService redisLikeService;
    private final EntityManager em;

    private IdolCategory getIdolCategoryByArtist(String artist){
        return idolCategoryRepository.findByArtist(artist).orElseThrow(() -> new AppException(ErrorCode.IDOL_CATEGORY_NOT_FOUND, "아티스트가 존재하지않습니다"));
    }

    private JoinIdol checkJoinMember(String username, String artist){
        return joinIdolRepository.findJoinIdolByMemberUsernameAndIdolCategoryArtist(username, artist).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_JOIN_IDOL_CATEGORY, "권한이 없는 회원입니다."));
    }


    @Override
    @Transactional
    public ResponseEntity<String> savePost(String artist, String content, String username) {

        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        // 내용 저장
        ArtistBoard artistBoard = ArtistBoard.createArtistBoard(content, joinIdol, BoardType.DEFAULT);
        artistBoardRepository.save(artistBoard);

        return ResponseEntity.ok().body("게시글 업로드 성공");
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveImgPost(String artist, String content, List<MultipartFile> postImgs, String username) {

        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        // 내용 저장
        ArtistBoard artistBoard = ArtistBoard.createArtistBoard(content, joinIdol, BoardType.IMAGE);
        artistBoardRepository.save(artistBoard);

        // 이미지 저장
        for (MultipartFile multipartFile : postImgs) {
            FileDetail fileDetail = FileDetail.multipartOf(multipartFile);
            String imgUrl = s3FileUploadService.store(artist, fileDetail.getPath(), multipartFile, username);

            // db에 이미지 이름 저장
            ArtistBoardImage boardImg = ArtistBoardImage.saveArtistBoardImg(imgUrl, artistBoard);
            artistBoardImageRepository.save(boardImg);
        }

        return ResponseEntity.ok().body("이미지 업로드 성공");
    }


    @Override
    @Transactional
    public ResponseEntity<String> saveVotePost(String artist, String content, List<Map<String, String>> voteChoice, int voteDay, int voteHour, String username) {
        if (voteChoice.size() > 4){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("투표 선택지는 4개까지 가능합니다.");
        }

        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        // 내용 저장
        ArtistBoard artistBoard = ArtistBoard.createArtistBoard(content, joinIdol, VOTE);
        artistBoardRepository.save(artistBoard);

        LocalDateTime currentTime = LocalDateTime.now();
        //LocalDateTime expiryTime = currentTime.plusDays(voteDay).plusHours(voteHour);
        LocalDateTime expiryTime = currentTime.plusMinutes(2);

        String[] vote = new String[5];
        int count = 0;
        for (int i = 0; i < voteChoice.size(); i++) {
             vote[i] = voteChoice.get(i).get("value");
             if (!vote[i].isEmpty()){
                 count++;
             }
        }

        ArtistBoardVote voteEntity = ArtistBoardVote.createVote(vote[0], vote[1], vote[2], vote[3], count,
                artistBoard, expiryTime);

        artistBoardVoteRepository.save(voteEntity);

        return ResponseEntity.ok().body("투표 게시글 업로드 성공");
    }


    @Override
    public ResponseEntity<Integer> getUserFollowPostTotalPage(String nickname, String artist) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        int totalResults = Math.toIntExact(queryFactory
                .selectFrom(artistBoard)
                .leftJoin(artistFollow).on(artistFollow.following.nickname.eq(artistBoard.writer.nickname))
                .where(
                        (artistFollow.follower.nickname.eq(nickname).and(artistBoard.writer.idolCategory.artist.eq(artist)))
                                .or(artistBoard.writer.nickname.eq(nickname))
                )
                .fetchCount());

        Integer totalPages = (int) Math.ceil((double) totalResults / 4);

        System.out.println(totalPages);
        return ResponseEntity.ok(totalPages);
    }

    @Override
    public ResponseEntity<List<PostListResponseDTO>> getUserFollowPost(String nickname, String artist, Integer page) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<Tuple> fetch = queryFactory
                .select(
                        artistBoard.boardType,
                        artistBoard
                )
                .from(artistBoard)
                .leftJoin(artistFollow).on(artistFollow.following.nickname.eq(artistBoard.writer.nickname))
                .where(
                        (artistFollow.follower.nickname.eq(nickname).and(artistBoard.writer.idolCategory.artist.eq(artist)))
                                .or(artistBoard.writer.nickname.eq(nickname))
                )
                .orderBy(artistBoard.createdDate.desc())
                .offset(4L * (page -1))
                .limit(4)
                .fetch();

        //select * from artist_board LEFT JOIN artist_follow on artist_follow.following_id = artist_board.join_idol_id where follower_id = 6 or join_idol_id = 6;
        List<PostListResponseDTO> responseDTOs = new ArrayList<>();

        for (Tuple tuple : fetch) {
            BoardType boardType = tuple.get(artistBoard.boardType);
            ArtistBoard findArtistBoard = tuple.get(artistBoard);

            Optional<ArtistBoardLike> boardLickCheck = artistBoardLikeRepository.findByArtistBoardIdAndMemberNickname(findArtistBoard.getId(), nickname);
            Optional<ArtistBoardBookmark> boardBookmark = artistBoardBookmarkRepository.findByArtistBoardIdAndMemberNickname(findArtistBoard.getId(), nickname);

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
    public ResponseEntity<PostResponseDTO> readArtistBoard(String artist, Long boardId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        ArtistBoard findArtistBoard = queryFactory
                .selectFrom(artistBoard)
                .where(artistBoard.id.eq(boardId))
                .fetchOne();

        int artistBoardCommentCount = 0;
        if (findArtistBoard.getArtistBoardComments().size() > 0){
            artistBoardCommentCount
                    = findArtistBoard.getArtistBoardComments().size();
        }

        Optional<ArtistBoardLike> boardLickCheck = artistBoardLikeRepository.findByArtistBoardIdAndMemberNickname(boardId, joinIdol.getNickname());
        Optional<ArtistBoardBookmark> boardBookmark = artistBoardBookmarkRepository.findByArtistBoardIdAndMemberNickname(boardId, joinIdol.getNickname());

        Optional<PostVoteResponseDTO> postVoteResponseDTO = Optional.empty();
        Optional<List<String>> image = Optional.empty();

        switch (findArtistBoard.getBoardType()){
            case VOTE -> postVoteResponseDTO = createPostVoteResponseDTO(findArtistBoard, username);
            case IMAGE -> image = createImageList(findArtistBoard);
        }

        return ResponseEntity.ok().body( new PostResponseDTO(
                findArtistBoard.getId(),
                findArtistBoard.getContent(),
                findArtistBoard.getWriter().getNickname(),
                findArtistBoard.getCreatedDate(),
                findArtistBoard.getBoardType(),
                findArtistBoard.getWriter().getJoinIdolMemberProfile().getImgUrl(),
                boardLickCheck.isPresent(),
                findArtistBoard.getLikeCount(),
                boardBookmark.isPresent(),
                findArtistBoard.getBookmarkCount(),
                artistBoardCommentCount,
                postVoteResponseDTO,
                image));
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveComment(String artist, Long boardId, String username, String comment, Long boardCommentId) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        ArtistBoard artistBoard = artistBoardRepository.findById(boardId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));

        ArtistBoardComment artistBoardComment = ArtistBoardComment.createArtistBoardcomment(comment, joinIdol, artistBoard);

        if(boardCommentId != null){
            ArtistBoardComment artistBoardCommentParent = artistBoardCommentRepository.findById(boardCommentId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_COMMENT_ID, "not found comment id"));
            artistBoardComment.updateParentComment(artistBoardCommentParent);
        }

        artistBoardCommentRepository.save(artistBoardComment);
        return ResponseEntity.ok("댓글 작성 성공");
    }

    @Override
    public ResponseEntity<List<CommentResponseDTO>> readArtistBoardComment(String artist, Long boardId, String username) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<CommentResponseDTO> commentResponseDTO = new ArrayList<>();
        Map<Long, CommentResponseDTO> commentDTOHashMap = new HashMap<>();

        JoinIdol joinIdol = checkJoinMember(username, artist);

        List<ArtistBoardComment> commentList = queryFactory.selectFrom(artistBoardComment)
                .leftJoin(artistBoardComment.parent).fetchJoin()
                .where(artistBoardComment.artistBoard.id.eq(boardId))
                .orderBy(artistBoardComment.parent.id.asc().nullsFirst(), artistBoardComment.createdDate.asc())
                .fetch();

        commentList.forEach( c -> {
            Optional<ArtistBoardCommentLike> isLike = artistBoardCommentLikeRepository.findByArtistBoardCommentIdAndMemberNickname(c.getId(), joinIdol.getNickname());
            Optional<ArtistBoardCommentBookmark> isBookmark = artistBoardCommentBookmarkRepository.findByArtistBoardCommentIdAndMemberNickname(c.getId(), joinIdol.getNickname());
            CommentResponseDTO comment = convertCommentToDto(c, isLike.isPresent(), isBookmark.isPresent());
            commentDTOHashMap.put(comment.getId(), comment);

            if(c.getParent() != null) {
                commentDTOHashMap.get(c.getParent().getId()).getCommentResponseDTO().add(comment);
                commentDTOHashMap.get(c.getParent().getId()).updateCommentCount();
            }

            else commentResponseDTO.add(comment);
        });

        return ResponseEntity.ok(commentResponseDTO);
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteComment(String artist, Long boardId, String username, Long boardCommentId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        checkJoinMember(username, artist);

        ArtistBoardComment comment = queryFactory.selectFrom(artistBoardComment)
                .where(artistBoardComment.id.eq(boardCommentId))
                .fetchOne();

        List<ArtistBoardCommentLike> likeComment = artistBoardCommentLikeRepository.findByArtistBoardCommentId(comment.getId());
        artistBoardCommentLikeRepository.deleteAll(likeComment);

        List<ArtistBoardCommentBookmark> bookmarkComment = artistBoardCommentBookmarkRepository.findByArtistBoardCommentId(comment.getId());
        artistBoardCommentBookmarkRepository.deleteAll(bookmarkComment);

        if (comment.getChildren().size() > 0) {
            // 해당 댓글에 대댓글이 존재한다.
            comment.changeIsDeleted(true);
        } else {
            artistBoardCommentRepository.delete(getDeletableAncestorComment(comment));
        }

        return ResponseEntity.ok("댓글 삭제 성공");
    }

    @Override
    @Transactional
    public ResponseEntity<String> boardLike(String artist, Long boardId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        ArtistBoard artistBoard = artistBoardRepository.findById(boardId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));
        Optional<ArtistBoardLike> boardLickCheck = artistBoardLikeRepository.findByArtistBoardIdAndMemberNickname(boardId, joinIdol.getNickname());

        if(boardLickCheck.isEmpty()){
            ArtistBoardLike boardLike = ArtistBoardLike.createBoardLike(artistBoard, joinIdol);
            artistBoardLikeRepository.save(boardLike);
            return ResponseEntity.ok("좋아요 성공");
        }else {
            artistBoard.decreaseLikeCount();
            artistBoardLikeRepository.delete(boardLickCheck.get());
            return ResponseEntity.ok("좋아요 취소 성공");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> boardBookmark(String artist, Long boardId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        ArtistBoard artistBoard = artistBoardRepository.findById(boardId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));

        Optional<ArtistBoardBookmark> artistBoardBookmark = artistBoardBookmarkRepository.findByArtistBoardIdAndMemberNickname(boardId, joinIdol.getNickname());

        if(artistBoardBookmark.isEmpty()){
            ArtistBoardBookmark boardBookmark = ArtistBoardBookmark.createBoardBookmark(artistBoard, joinIdol);
            artistBoardBookmarkRepository.save(boardBookmark);
            return ResponseEntity.ok("북마크 성공");
        }else {
            artistBoard.decreaseBookmarkCount();
            artistBoardBookmarkRepository.delete(artistBoardBookmark.get());
            return ResponseEntity.ok("북마크 취소");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveMarketBoard(String title, String artist, String content, String price, List<MultipartFile> multipartFile, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        // 내용 저장
        ArtistMarketBoard artistMarketBoard = ArtistMarketBoard.createArtistMarketBoard(title, content, joinIdol, price);
        artistMarketBoardRepository.save(artistMarketBoard);

        // 이미지 저장
        for (MultipartFile image : multipartFile) {
            FileDetail fileDetail = FileDetail.multipartOf(image);
            String imgUrl = s3FileUploadService.marketBoardStore(artist, fileDetail.getPath(), image, username);

            // db에 이미지 이름 저장
            ArtistMarketBoardImage boardImg = ArtistMarketBoardImage.saveArtistBoardImg(imgUrl, artistMarketBoard);
            artistMarketBoardImageRepository.save(boardImg);
        }

        return ResponseEntity.ok().body("이미지 업로드 성공");
    }

    @Override
    public ResponseEntity<MarketPostResponseDTO> readMarketBoard(String artist, Long boardId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        ArtistMarketBoard findArtistMarketBoard = queryFactory
                .selectFrom(artistMarketBoard)
                .where(artistMarketBoard.id.eq(boardId))
                .fetchOne();

        int artistBoardCommentCount = 0;
        if (findArtistMarketBoard.getArtistMarketBoardComments().size() > 0){
            artistBoardCommentCount
                    = findArtistMarketBoard.getArtistMarketBoardComments().size();
        }

        Optional<ArtistMarketBoardLike> boardLikeCheck = artistMarketBoardLikeRepository.findArtistMarketBoardLikeByArtistMarketBoardIdAndMemberId(findArtistMarketBoard.getId(), joinIdol.getId());
        Optional<ArtistMarketBoardBookmark> boardBookmark = artistMarketBoardBookmarkRepository.findArtistMarketBoardBookmarkByArtistMarketBoardIdAndMemberId(findArtistMarketBoard.getId(), joinIdol.getId());

        Optional<List<String>> image = createMarketImageList(findArtistMarketBoard);

        return ResponseEntity.ok().body( new MarketPostResponseDTO(
                findArtistMarketBoard.getId(),
                findArtistMarketBoard.getTitle(),
                findArtistMarketBoard.getPrice(),
                findArtistMarketBoard.getContent(),
                findArtistMarketBoard.getWriter().getNickname(),
                findArtistMarketBoard.getCreatedDate(),
                findArtistMarketBoard.getWriter().getJoinIdolMemberProfile().getImgUrl(),
                findArtistMarketBoard.getMarketBoardStatus().name(),
                findArtistMarketBoard.getMarketBoardStatus().getStatus(),
                boardLikeCheck.isPresent(), // null이 아니면 ture
                findArtistMarketBoard.getLikeCount(),
                boardBookmark.isPresent(),
                findArtistMarketBoard.getBookmarkCount(),
                artistBoardCommentCount,
                image));
    }

    @Override
    public ResponseEntity<Integer> getMarketPostTotalPage(String artist, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        int totalResults = Math.toIntExact(queryFactory
                .selectFrom(artistMarketBoard)
                        .where(artistMarketBoard.writer.idolCategory.artist.eq(artist))
                .fetchCount());

        Integer totalPages = (int) Math.ceil((double) totalResults / 12);

        System.out.println(totalPages);
        return ResponseEntity.ok(totalPages);
    }

    @Override
    public ResponseEntity<List<MarketPostListResponseDTO>> readMarketBoardList(String artist, String nickname, Integer page, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<Tuple> fetch = queryFactory
                .select(
                        artistMarketBoard,
                        artistMarketBoardLike
                )
                .from(artistMarketBoard)
                .leftJoin(artistMarketBoardLike)
                .on(artistMarketBoardLike.artistMarketBoard.id.eq(artistMarketBoard.id).and(artistMarketBoardLike.member.nickname.eq(nickname)))
                .where(
                        (artistMarketBoard.writer.idolCategory.artist.eq(artist))
                )
                .orderBy(artistMarketBoard.createdDate.desc())
                .offset(12L * (page - 1))
                .limit(12)
                .fetch();

        List<MarketPostListResponseDTO> responseDTOList = new ArrayList<>();
        for (Tuple tuple : fetch) {
            ArtistMarketBoard findArtistMarketBoard = tuple.get(artistMarketBoard);
            ArtistMarketBoardLike findArtistMarketBoardLike = tuple.get(artistMarketBoardLike);

            boolean isLike = findArtistMarketBoardLike != null; // null이 아니면 true => 좋아요가 있다.

            MarketPostListResponseDTO marketPostListResponseDTO = new MarketPostListResponseDTO(findArtistMarketBoard.getId(), findArtistMarketBoard.getTitle(), findArtistMarketBoard.getPrice(), isLike, findArtistMarketBoard.getImageUrlList().get(0).getArtistMarketBoardImgURL());
            responseDTOList.add(marketPostListResponseDTO);
        }

        return ResponseEntity.ok(responseDTOList);
    }

    @Override
    @Transactional
    public ResponseEntity<String> marketBoardLike(String artist, Long boardId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        ArtistMarketBoard artistMarketBoard = artistMarketBoardRepository.findById(boardId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));
        Boolean isCHeck = redisLikeService.marketBoardLike(boardId, joinIdol.getId());

        if (!isCHeck){
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests - try again later.");
        }else {
            Optional<ArtistMarketBoardLike> marketBoardLickCheck = artistMarketBoardLikeRepository.findArtistMarketBoardLikeByArtistMarketBoardIdAndMemberId(boardId, joinIdol.getId());
            if(marketBoardLickCheck.isEmpty()){
                ArtistMarketBoardLike boardLike = ArtistMarketBoardLike.createMarketBoardLike(artistMarketBoard, joinIdol);
                artistMarketBoardLikeRepository.save(boardLike);
                return ResponseEntity.ok("좋아요 성공");
            }else {
                artistMarketBoard.decreaseLikeCount();
                artistMarketBoardLikeRepository.delete(marketBoardLickCheck.get());
                return ResponseEntity.ok("좋아요 취소 성공");
            }
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> marketBoardBookmark(String artist, Long boardId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        ArtistMarketBoard artistMarketBoard = artistMarketBoardRepository.findById(boardId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));

        Optional<ArtistMarketBoardBookmark> artistMarketBoardBookmark = artistMarketBoardBookmarkRepository.findArtistMarketBoardBookmarkByArtistMarketBoardIdAndMemberId(boardId, joinIdol.getId());

        if(artistMarketBoardBookmark.isEmpty()){
            ArtistMarketBoardBookmark boardBookmark = ArtistMarketBoardBookmark.createMarketBoardBookmark(artistMarketBoard, joinIdol);
            artistMarketBoardBookmarkRepository.save(boardBookmark);
            return ResponseEntity.ok("북마크 성공");
        }else {
            artistMarketBoard.decreaseBookmarkCount();
            artistMarketBoardBookmarkRepository.delete(artistMarketBoardBookmark.get());
            return ResponseEntity.ok("북마크 취소");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> modifyMarketBoardStatus(String artist, Long boardId, String boardStatus, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        ArtistMarketBoard findArtistMarketBoard = artistMarketBoardRepository.findArtistMarketBoardByIdAndWriterId(boardId, joinIdol.getId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));

        MarketBoardStatus status = MarketBoardStatus.fromString(boardStatus);
        findArtistMarketBoard.updateBoardStatus(status);
        return ResponseEntity.ok("상태 변경 완료");
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteMarketBoard(String artist, Long boardId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        ArtistMarketBoard findArtistMarketBoard = artistMarketBoardRepository.findArtistMarketBoardByIdAndWriterId(boardId, joinIdol.getId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));

        Optional<List<ArtistMarketBoardLike>> artistMarketBoardLikesByArtistMarketBoardIdAndMemberId = artistMarketBoardLikeRepository.findArtistMarketBoardLikesByArtistMarketBoardIdAndArtistMarketBoardWriterId(boardId, joinIdol.getId());
        Optional<List<ArtistMarketBoardImage>> artistMarketBoardImagesByArtistMarketBoardIdAndArtistMarketBoardWriterId = artistMarketBoardImageRepository.findArtistMarketBoardImagesByArtistMarketBoardIdAndArtistMarketBoardWriterId(boardId, joinIdol.getId());
        Optional<List<ArtistMarketBoardBookmark>> artistMarketBoardBookmarksByArtistMarketBoardIdAndMemberId = artistMarketBoardBookmarkRepository.findArtistMarketBoardBookmarksByArtistMarketBoardIdAndArtistMarketBoardWriterId(boardId, joinIdol.getId());
        Optional<List<ArtistMarketBoardComment>> artistMarketBoardCommentsByArtistMarketBoardIdAndArtistMarketBoardWriterId = artistMarketBoardCommentRepository.findArtistMarketBoardCommentsByArtistMarketBoardIdAndArtistMarketBoardWriterId(boardId, joinIdol.getId());

        artistMarketBoardLikesByArtistMarketBoardIdAndMemberId.ifPresent(artistMarketBoardLikeRepository::deleteAll);
        artistMarketBoardImagesByArtistMarketBoardIdAndArtistMarketBoardWriterId.ifPresent(artistMarketBoardImageRepository::deleteAll);
        artistMarketBoardBookmarksByArtistMarketBoardIdAndMemberId.ifPresent(artistMarketBoardBookmarkRepository::deleteAll);
        artistMarketBoardCommentsByArtistMarketBoardIdAndArtistMarketBoardWriterId.ifPresent(artistMarketBoardCommentRepository::deleteAll);

        artistMarketBoardRepository.delete(findArtistMarketBoard);
        return ResponseEntity.ok("삭제 완료");
    }

    @Override
    public ResponseEntity<MarketBoardModifyResponseDTO> getModifyMarketBoard(String artist, Long boardId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        ArtistMarketBoard findArtistMarketBoard = artistMarketBoardRepository.findArtistMarketBoardByIdAndWriterId(boardId, joinIdol.getId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));

        List<MarketBoardModifyImageDTO> imageList = new ArrayList<>();
        for (ArtistMarketBoardImage artistMarketBoardImage : findArtistMarketBoard.getImageUrlList()) {
            MarketBoardModifyImageDTO marketBoardModifyImageDTO = new MarketBoardModifyImageDTO(artistMarketBoardImage.getId(), artistMarketBoardImage.getArtistMarketBoardImgURL());
            imageList.add(marketBoardModifyImageDTO);
        }

        return ResponseEntity.ok(new MarketBoardModifyResponseDTO(
                findArtistMarketBoard.getTitle(),
                findArtistMarketBoard.getPrice(),
                findArtistMarketBoard.getContent(),
                imageList
        ));
    }

    @Override
    @Transactional
    public ResponseEntity<String> modifyMarketBoard(String artist, Long sellNum, String username, String title, String price, String content, List<Long> deleteImgList, List<MultipartFile> addImgList) {
        ArtistMarketBoard findMarketBoard = artistMarketBoardRepository.findById(sellNum).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));
        findMarketBoard.modifyMarketBoard(title, price, content);

        // 이미지 삭제
        if (deleteImgList != null){ // 삭제할 이미지가 있을 경우
            for (Long index : deleteImgList) {
                ArtistMarketBoardImage artistMarketBoardImage = artistMarketBoardImageRepository.findById(index).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));
                findMarketBoard.getImageUrlList().remove(artistMarketBoardImage);
                s3FileUploadService.deleteMissingImages(artistMarketBoardImage.getArtistMarketBoardImgURL());
                artistMarketBoardImage.deleteMarketBoardImage();

                artistMarketBoardImageRepository.delete(artistMarketBoardImage);
            }
        }

        if(addImgList != null){ // 추가할 이미지가 있을 경우
            for (MultipartFile multipartFile : addImgList) {
                FileDetail fileDetail = FileDetail.multipartOf(multipartFile);
                String imgUrl = s3FileUploadService.marketBoardStore(artist, fileDetail.getPath(), multipartFile, username);

                // db에 이미지 이름 저장
                ArtistMarketBoardImage boardImg = ArtistMarketBoardImage.saveArtistBoardImg(imgUrl, findMarketBoard);
                artistMarketBoardImageRepository.save(boardImg);
            }
        }

        return ResponseEntity.ok("수정 완료");
    }

    @Override
    public ResponseEntity<List<PostListResponseDTO>> getPopularBoard(String artist, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);
        String nickname = joinIdol.getNickname();

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<Tuple> fetch = queryFactory
                .select(
                        artistBoard.boardType,
                        artistBoard
                )
                .from(artistBoard)
                .where(
                        (artistBoard.writer.idolCategory.artist.eq(artist))
                )
                .orderBy(artistBoard.likeCount.desc())
                .limit(20)
                .fetch();

       List<PostListResponseDTO> responseDTOs = new ArrayList<>();

        for (Tuple tuple : fetch) {
            BoardType boardType = tuple.get(artistBoard.boardType);
            ArtistBoard findArtistBoard = tuple.get(artistBoard);

            Optional<ArtistBoardLike> boardLickCheck = artistBoardLikeRepository.findByArtistBoardIdAndMemberNickname(findArtistBoard.getId(), nickname);
            Optional<ArtistBoardBookmark> boardBookmark = artistBoardBookmarkRepository.findByArtistBoardIdAndMemberNickname(findArtistBoard.getId(), nickname);

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
    @Transactional
    public ResponseEntity<String> saveCommentLike(String artist, Long boardId, Long commentId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        artistBoardRepository.findById(boardId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));
        ArtistBoardComment artistBoardComment = artistBoardCommentRepository.findById(commentId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));
        Optional<ArtistBoardCommentLike> boardLickCheck = artistBoardCommentLikeRepository.findByArtistBoardCommentIdAndMemberNickname(commentId, joinIdol.getNickname());

        if(boardLickCheck.isEmpty()){
            ArtistBoardCommentLike artistBoardCommentLike = ArtistBoardCommentLike.createBoardCommentLike(artistBoardComment, joinIdol);
            artistBoardCommentLikeRepository.save(artistBoardCommentLike);
            return ResponseEntity.ok("좋아요 성공");
        }else {
            artistBoardComment.decreaseLikeCount();
            artistBoardCommentLikeRepository.delete(boardLickCheck.get());
            return ResponseEntity.ok("좋아요 취소 성공");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveCommentBookmark(String artist, Long boardId, Long commentId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        artistBoardRepository.findById(boardId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));
        ArtistBoardComment artistBoardComment = artistBoardCommentRepository.findById(commentId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));
        Optional<ArtistBoardCommentBookmark> boardCommentCheck = artistBoardCommentBookmarkRepository.findByArtistBoardCommentIdAndMemberNickname(commentId, joinIdol.getNickname());

        if(boardCommentCheck.isEmpty()){
            ArtistBoardCommentBookmark boardCommentBookmark = ArtistBoardCommentBookmark.createBoardCommentBookmark(artistBoardComment, joinIdol);
            artistBoardCommentBookmarkRepository.save(boardCommentBookmark);
            return ResponseEntity.ok("북마크 성공");
        }else {
            artistBoardComment.decreaseBookmarkCount();
            artistBoardCommentBookmarkRepository.delete(boardCommentCheck.get());
            return ResponseEntity.ok("북마크 취소 성공");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveMarketComment(String artist, Long boardId, String username, String comment, Long boardCommentId) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        ArtistMarketBoard artistMarketBoard = artistMarketBoardRepository.findById(boardId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));

        ArtistMarketBoardComment artistMarketBoardComment = ArtistMarketBoardComment.createArtistMarketBoardComment(comment, joinIdol, artistMarketBoard);

        if(boardCommentId != null){
            ArtistMarketBoardComment artistMarketBoardCommentParent = artistMarketBoardCommentRepository.findById(boardCommentId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_COMMENT_ID, "not found comment id"));
            artistMarketBoardComment.updateParentComment(artistMarketBoardCommentParent);
        }

        artistMarketBoardCommentRepository.save(artistMarketBoardComment);
        return ResponseEntity.ok("댓글 작성 성공");
    }

    @Override
    public ResponseEntity<List<MarketCommentResponseDTO>> readMarketBoardComment(String artist, Long sellNum, String username) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<MarketCommentResponseDTO> commentResponseDTO = new ArrayList<>();
        Map<Long, MarketCommentResponseDTO> commentDTOHashMap = new HashMap<>();

        JoinIdol joinIdol = checkJoinMember(username, artist);

        List<ArtistMarketBoardComment> commentList = queryFactory.selectFrom(artistMarketBoardComment)
                .leftJoin(artistMarketBoardComment.parent).fetchJoin()
                .where(artistMarketBoardComment.artistMarketBoard.id.eq(sellNum))
                .orderBy(artistMarketBoardComment.parent.id.asc().nullsFirst(), artistMarketBoardComment.createdDate.asc())
                .fetch();

        commentList.forEach( c -> {
            Optional<ArtistMarketBoardCommentBookmark> isBookmark = marketCommentBookmarkRepository.findByArtistMarketBoardCommentIdAndArtistMarketBoardCommentWriterNickname(c.getId(), joinIdol.getNickname());
            Optional<ArtistMarketBoardCommentLike> isLike = marketCommentLikeRepository.findByArtistMarketBoardCommentIdAndArtistMarketBoardCommentWriterNickname(c.getId(), joinIdol.getNickname());
            MarketCommentResponseDTO comment = convertMarketCommentToDto(c, isLike.isPresent(), isBookmark.isPresent());
            commentDTOHashMap.put(comment.getId(), comment);

            if(c.getParent() != null) {
                commentDTOHashMap.get(c.getParent().getId()).getCommentResponseDTO().add(comment);
                commentDTOHashMap.get(c.getParent().getId()).updateCommentCount();
            }

            else commentResponseDTO.add(comment);
        });

        return ResponseEntity.ok(commentResponseDTO);
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveMarketCommentLike(String artist, Long sellNum, Long commentId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        artistMarketBoardRepository.findById(sellNum).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found market board"));
        ArtistMarketBoardComment comment = artistMarketBoardCommentRepository.findById(commentId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found comment"));
        Optional<ArtistMarketBoardCommentLike> commentLikeCheck = marketCommentLikeRepository.findByArtistMarketBoardCommentIdAndArtistMarketBoardCommentWriterNickname(commentId, joinIdol.getNickname());

        if(commentLikeCheck.isEmpty()){
            ArtistMarketBoardCommentLike artistMarketBoardCommentLike = ArtistMarketBoardCommentLike.createArtistMarketBoardCommentLike(comment, joinIdol);
            marketCommentLikeRepository.save(artistMarketBoardCommentLike);
            return ResponseEntity.ok("좋아요 성공");
        }else {
            comment.decreaseLikeCount();
            marketCommentLikeRepository.delete(commentLikeCheck.get());
            return ResponseEntity.ok("좋아요 취소 성공");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveMarketCommentBookmark(String artist, Long sellNum, Long commentId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        artistBoardRepository.findById(sellNum).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found market board"));
        ArtistMarketBoardComment comment = artistMarketBoardCommentRepository.findById(commentId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_BOARD, "not found board"));
        Optional<ArtistMarketBoardCommentBookmark> commentBookmarkCheck = marketCommentBookmarkRepository.findByArtistMarketBoardCommentIdAndArtistMarketBoardCommentWriterNickname(commentId, joinIdol.getNickname());

        if(commentBookmarkCheck.isEmpty()){
            ArtistMarketBoardCommentBookmark artistMarketBoardCommentBookmark = ArtistMarketBoardCommentBookmark.createArtistMarketBoardCommentBookmark(comment, joinIdol);
            marketCommentBookmarkRepository.save(artistMarketBoardCommentBookmark);
            return ResponseEntity.ok("북마크 성공");
        }else {
            comment.decreaseBookmarkCount();
            marketCommentBookmarkRepository.delete(commentBookmarkCheck.get());
            return ResponseEntity.ok("북마크 취소 성공");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteMarketComment(String artist, Long boardId, String username, Long boardCommentId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        checkJoinMember(username, artist);

        ArtistMarketBoardComment comment = queryFactory.selectFrom(artistMarketBoardComment)
                .where(artistMarketBoardComment.id.eq(boardCommentId))
                .fetchOne();

        List<ArtistMarketBoardCommentLike> likeComment = marketCommentLikeRepository.findByArtistMarketBoardCommentId(comment.getId());
        marketCommentLikeRepository.deleteAll(likeComment);

        List<ArtistMarketBoardCommentBookmark> bookmarkComment = marketCommentBookmarkRepository.findByArtistMarketBoardCommentId(comment.getId());
        marketCommentBookmarkRepository.deleteAll(bookmarkComment);

        if (comment.getChildren().size() > 0) {
            // 해당 댓글에 대댓글이 존재한다.
            comment.changeIsDeleted(true);
        } else {
            artistMarketBoardCommentRepository.delete(getDeletableAncestorMarketComment(comment));
        }

        return ResponseEntity.ok("댓글 삭제 성공");
    }

    @Override
    @Transactional
    public ResponseEntity<String> savePlace(String artist, String lat, String lng, String placeName, String addressName, String roadAddressName, String placeUrl, String memo, MultipartFile multipartFile, String username) {
        // 카테고리가 존재하는지 확인
        IdolCategory artistCategory = getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        FileDetail fileDetail = FileDetail.multipartOf(multipartFile);
        String imgUrl = s3FileUploadService.artistPlaceStore(artist, fileDetail.getPath(), multipartFile, username);

        // db에 이미지 이름 저장
        ArtistPlace artistPlace = ArtistPlace.createArtistPlace(lat, lng, placeName, addressName, roadAddressName, imgUrl, placeUrl, user, artistCategory, memo);
        artistPlaceRepository.save(artistPlace);

        return ResponseEntity.ok("장소 저장 완료");
    }

    @Override
    public ResponseEntity<List<ArtistPlaceResponse>> getArtistPlace(String artist, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        List<ArtistPlace> artistPlaces = artistPlaceRepository.findByCategoryArtist(artist);

        List<ArtistPlaceResponse> artistPlaceResponses = new ArrayList<>();

        for (ArtistPlace artistPlace : artistPlaces) {
            artistPlaceResponses.add(new ArtistPlaceResponse(artistPlace.getAddressName(), artistPlace.getPlaceImage(), artistPlace.getPlaceName(), artistPlace.getRoadAddressName(), artistPlace.getLat(), artistPlace.getLng(), artistPlace.getPlaceUrl(), artistPlace.getMemo()));
        }

        return ResponseEntity.ok(artistPlaceResponses);
    }

    @Override
    public ResponseEntity<List<PostListResponseDTO>> getSearchBoard(String artist, String keyword, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        BooleanExpression checkContent = artistBoard.content.contains(keyword);
        BooleanExpression checkWriter = artistBoard.writer.nickname.contains(keyword);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<Tuple> fetch = queryFactory
                .select(
                        artistBoard.boardType,
                        artistBoard
                )
                .from(artistBoard)
                .where(
                        checkContent.or(checkWriter)
                )
                .orderBy(artistBoard.createdDate.desc())
                //.offset(4L * (page -1))
                //.limit(4)
                .fetch();

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
    public ResponseEntity<List<MarketPostListResponseDTO>> getSearchMarketBoard(String artist, String keyword, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        BooleanExpression checkTitle = artistMarketBoard.title.contains(keyword);
        BooleanExpression checkContent = artistMarketBoard.content.contains(keyword);
        BooleanExpression checkWriter = artistMarketBoard.writer.nickname.contains(keyword);

        List<ArtistMarketBoard> marketBoardList = queryFactory
                .selectFrom(artistMarketBoard)
                .where(
                        checkTitle.or(checkContent).or(checkWriter)
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
    public ResponseEntity<List<SearchUserNicknameResponseDTO>> getSearchKeyword(String artist, String keyword, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        BooleanExpression checkUserNickname = joinIdol.nickname.contains(keyword);
        List<SearchUserNicknameResponseDTO> responseDTOS = queryFactory.select(Projections.constructor(SearchUserNicknameResponseDTO.class,
                        joinIdol.nickname,
                        joinIdol.joinIdolMemberProfile.imgUrl,
                        joinIdol.userInfo))
                .from(joinIdol)
                .where(checkUserNickname)
                .fetch();

        return ResponseEntity.ok(responseDTOS);
    }

    private ArtistBoardComment getDeletableAncestorComment(ArtistBoardComment comment) {
        ArtistBoardComment parent = comment.getParent();

        if (parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted()){
            return getDeletableAncestorComment(parent);
        }

        return comment;
    }

    private ArtistMarketBoardComment getDeletableAncestorMarketComment(ArtistMarketBoardComment comment) {
        ArtistMarketBoardComment parent = comment.getParent();

        if (parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted()){
            return getDeletableAncestorMarketComment(parent);
        }

        return comment;
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

    private Optional<PostVoteResponseDTO> createPostVoteResponseDTO(ArtistBoard findArtistBoard, String username) {
        Boolean hasVoted = false;
        String userVoteChoice = null;
        Map<String, String> voteResults = new HashMap<>();

        ArtistBoardVote artistBoardVote = findArtistBoard.getArtistBoardVote();

        List<String> choice = new ArrayList<>();
        choice.add(artistBoardVote.getChoice1());
        choice.add(artistBoardVote.getChoice2());
        choice.add(artistBoardVote.getChoice3());
        choice.add(artistBoardVote.getChoice4());

        boolean expireVote = false;
        LocalDateTime currentTime = LocalDateTime.now();
        if(currentTime.isAfter(artistBoardVote.getVoteExpireTime())){
            expireVote = true;
        }

        // 투표가 만료되었는지 확인
        // 투표가 만료되었을 경우 Db에서 가져오기
        if (expireVote && findArtistBoard.getArtistBoardVote().getSaveVote()){ // 투표 종료 후 db에 저장했으면
            voteResults = getVoteResults(findArtistBoard.getId());
        }else { // 투표 진행중이거나 db에 저장되지 않았을때
            // 이미 투표한 회원인지 확인
            voteResults = redisVoteService.getVoteResults(findArtistBoard.getId());
        }

        if(redisVoteService.hasVoted(username, String.valueOf(findArtistBoard.getId()))){ // 투표한적이 있음
            System.out.println("이미 투표한적이 있습니다.");
            userVoteChoice = redisVoteService.getUserVoteChoice(username, String.valueOf(findArtistBoard.getId()));
            hasVoted = true;
        }else { // 투표한적이 없거나 만료되어서 데이터가 없을경우.
            System.out.println("이미 투표한 회원이 아닙니다.");
        }

        return Optional.of(new PostVoteResponseDTO(
                choice,
                artistBoardVote.getChoiceTotalCount(),
                hasVoted,
                expireVote,
                userVoteChoice,
                voteResults,
                artistBoardVote.getVoteExpireTime()));
    }

    private Optional<List<String>> createImageList(ArtistBoard findArtistBoard) {
        List<String> imageList = new ArrayList<>();
        for (ArtistBoardImage boardImage : findArtistBoard.getImageUrlList()) {
            imageList.add(boardImage.getArtistBoardImgURL());
        }
        return Optional.of(imageList);
    }

    private Optional<List<String>> createMarketImageList(ArtistMarketBoard findArtistMarketBoard) {
        List<String> imageList = new ArrayList<>();
        for (ArtistMarketBoardImage boardImage : findArtistMarketBoard.getImageUrlList()) {
            imageList.add(boardImage.getArtistMarketBoardImgURL());
        }

        return Optional.of(imageList);
    }

    public Map<String, String> getVoteResults(Long postId) {
        ArtistBoardVote findArtistBoardVote = artistBoardVoteRepository.findByArtistBoardId(postId);

        Map<String, String> voteResult = new HashMap<>();
        int totalVotes = findArtistBoardVote.getResultChoiceCount();

        double percentage1 = (double) findArtistBoardVote.getChoiceCount1() / totalVotes * 100.0;
        double percentage2 = (double) findArtistBoardVote.getChoiceCount2() / totalVotes * 100.0;
        double percentage3 = (double) findArtistBoardVote.getChoiceCount3() / totalVotes * 100.0;
        double percentage4 = (double) findArtistBoardVote.getChoiceCount4() / totalVotes * 100.0;

        if (findArtistBoardVote.getChoice1() != null) {
            voteResult.put(findArtistBoardVote.getChoice1(), String.valueOf(percentage1));
        }

        if (findArtistBoardVote.getChoice2() != null) {
            voteResult.put(findArtistBoardVote.getChoice2(), String.valueOf(percentage2));
        }

        if (findArtistBoardVote.getChoice3() != null) {
            voteResult.put(findArtistBoardVote.getChoice3(), String.valueOf(percentage3));
        }

        if (findArtistBoardVote.getChoice4() != null) {
            voteResult.put(findArtistBoardVote.getChoice4(), String.valueOf(percentage4));
        }

        return voteResult;
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 12시간(12 * 60 * 60 * 1000 milliseconds)
    public void processExpiredVotes() {
        List<ArtistBoardVote> expiredVotes = artistBoardVoteRepository.findByVoteExpireTimeBeforeAndSaveVote(LocalDateTime.now(), false);
        for (ArtistBoardVote vote : expiredVotes) {
            redisVoteService.saveArtistBoardVoteResult(vote.getArtistBoard().getId(), vote.getChoiceTotalCount());
        }
    }
}
