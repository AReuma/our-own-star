package com.example.backend.artistBoard.service;

import com.example.backend.artistBoard.dto.PostResponseDTO;
import com.example.backend.artistBoard.dto.PostVoteResponseDTO;
import com.example.backend.artistBoard.entity.*;
import com.example.backend.artistBoard.repository.ArtistBoardImageRepository;
import com.example.backend.artistBoard.repository.ArtistBoardRepository;
import com.example.backend.artistBoard.repository.ArtistBoardVoteRepository;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.common.s3.FileDetail;
import com.example.backend.common.s3.S3FileUploadService;
import com.example.backend.idolCategory.repository.IdolCategoryRepository;
import com.example.backend.member.entity.JoinIdol;
import com.example.backend.member.repository.JoinIdolRepository;
import com.example.backend.member.repository.MemberRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.backend.artistBoard.entity.QArtistBoard.artistBoard;
import static com.example.backend.artistBoard.entity.QArtistFollow.artistFollow;

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
    private final S3FileUploadService s3FileUploadService;
    private final EntityManager em;

    private void getIdolCategoryByArtist(String artist){
        idolCategoryRepository.findByArtist(artist).orElseThrow(() -> new AppException(ErrorCode.IDOL_CATEGORY_NOT_FOUND, "아티스트가 존재하지않습니다"));
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
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        // 내용 저장
        ArtistBoard artistBoard = ArtistBoard.createArtistBoard(content, joinIdol, BoardType.VOTE);
        artistBoardRepository.save(artistBoard);

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiryTime = currentTime.plusDays(voteDay).plusHours(voteHour);

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
                .where(artistFollow.follower.nickname.eq(nickname).and(artistBoard.writer.idolCategory.artist.eq(artist)))
                .fetchCount());

        Integer totalPages = (int) Math.ceil((double) totalResults / 4);

        System.out.println(totalPages);
        return ResponseEntity.ok(totalPages);
    }

    @Override
    public ResponseEntity<List<PostResponseDTO>> getUserFollowPost(String nickname, String artist, Integer page) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<Tuple> fetch = queryFactory
                .select(
                        artistBoard.boardType,
                        artistBoard
                )
                .from(artistBoard)
                .leftJoin(artistFollow).on(artistFollow.following.nickname.eq(artistBoard.writer.nickname))
                .where(artistFollow.follower.nickname.eq(nickname).and(artistBoard.writer.idolCategory.artist.eq(artist)))
                .orderBy(artistBoard.createdDate.desc())
                .offset(4L * (page -1))
                .limit(4)
                .fetch();

        List<PostResponseDTO> responseDTOs = new ArrayList<>();

        for (Tuple tuple : fetch) {
            BoardType boardType = tuple.get(artistBoard.boardType);
            ArtistBoard findArtistBoard = tuple.get(artistBoard);

            Optional<PostVoteResponseDTO> postVoteResponseDTO = Optional.empty();
            Optional<List<String>> image = Optional.empty();

            switch (boardType){
                case VOTE -> postVoteResponseDTO = createPostVoteResponseDTO(findArtistBoard);
                case IMAGE -> image = createImageList(findArtistBoard);
            }

            assert findArtistBoard != null;
            responseDTOs.add(new PostResponseDTO(
                    findArtistBoard.getId(),
                    findArtistBoard.getContent(),
                    findArtistBoard.getWriter().getNickname(),
                    findArtistBoard.getCreatedDate(),
                    boardType,
                    findArtistBoard.getWriter().getJoinIdolMemberProfile().getImgUrl(),
                    postVoteResponseDTO,
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
        checkJoinMember(username, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        ArtistBoard findArtistBoard = queryFactory
                .selectFrom(artistBoard)
                .where(artistBoard.id.eq(boardId))
                .fetchOne();

        Optional<PostVoteResponseDTO> postVoteResponseDTO = Optional.empty();
        Optional<List<String>> image = Optional.empty();

        switch (findArtistBoard.getBoardType()){
            case VOTE -> postVoteResponseDTO = createPostVoteResponseDTO(findArtistBoard);
            case IMAGE -> image = createImageList(findArtistBoard);

        }

        return ResponseEntity.ok().body( new PostResponseDTO(
                findArtistBoard.getId(),
                findArtistBoard.getContent(),
                findArtistBoard.getWriter().getNickname(),
                findArtistBoard.getCreatedDate(),
                findArtistBoard.getBoardType(),
                findArtistBoard.getWriter().getJoinIdolMemberProfile().getImgUrl(),
                postVoteResponseDTO,
                image));
    }

    private Optional<PostVoteResponseDTO> createPostVoteResponseDTO(ArtistBoard findArtistBoard) {
        ArtistBoardVote artistBoardVote = findArtistBoard.getArtistBoardVote();

        List<String> choice = new ArrayList<>();
        choice.add(artistBoardVote.getChoice1());
        choice.add(artistBoardVote.getChoice2());
        choice.add(artistBoardVote.getChoice3());
        choice.add(artistBoardVote.getChoice4());

        return Optional.of(new PostVoteResponseDTO(
                choice,
                artistBoardVote.getChoiceCount(),
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
