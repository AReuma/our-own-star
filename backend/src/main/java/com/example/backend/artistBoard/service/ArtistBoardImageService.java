package com.example.backend.artistBoard.service;

import com.example.backend.artistBoard.dto.PostResponseDTO;
import com.example.backend.artistBoard.dto.PostVoteResponseDTO;
import com.example.backend.artistBoard.dto.market.MarketPostListResponseDTO;
import com.example.backend.artistBoard.entity.*;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoard;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardImage;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardLike;
import com.example.backend.artistBoard.repository.*;
import com.example.backend.artistBoard.repository.market.*;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.common.s3.FileDetail;
import com.querydsl.core.Tuple;
import org.springframework.core.io.Resource;
import com.example.backend.common.s3.S3FileUploadService;
import com.example.backend.idolCategory.entity.IdolCategory;
import com.example.backend.idolCategory.repository.IdolCategoryRepository;
import com.example.backend.member.entity.JoinIdol;
import com.example.backend.member.repository.JoinIdolRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.backend.artistBoard.entity.QArtistBoard.artistBoard;
import static com.example.backend.artistBoard.entity.market.QArtistMarketBoard.artistMarketBoard;
import static com.example.backend.artistBoard.entity.market.QArtistMarketBoardLike.artistMarketBoardLike;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtistBoardImageService {

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
    private final EntityManager em;

    private IdolCategory getIdolCategoryByArtist(String artist){
        return idolCategoryRepository.findByArtist(artist).orElseThrow(() -> new AppException(ErrorCode.IDOL_CATEGORY_NOT_FOUND, "아티스트가 존재하지않습니다"));
    }

    private JoinIdol checkJoinMember(String username, String artist){
        return joinIdolRepository.findJoinIdolByMemberUsernameAndIdolCategoryArtist(username, artist).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_JOIN_IDOL_CATEGORY, "권한이 없는 회원입니다."));
    }

    private static String UPLOADED_FOLDER = "/Users/reuma/Documents/our-own-star/backend/src/main/resources/static/image/";
    private final Path fileStorageLocation = Paths.get("/Users/reuma/Documents/our-own-star/frontend/src/assets/board/");

    @Transactional
    public ResponseEntity<String> saveImgPost(String artist, String content, List<MultipartFile> postImgs, String username) throws IOException {

        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        // 내용 저장
        ArtistBoard artistBoard = ArtistBoard.createArtistBoard(content, joinIdol, BoardType.IMAGE);
        artistBoardRepository.save(artistBoard);

        // 이미지 저장
        for (MultipartFile multipartFile : postImgs) {
            /*FileDetail fileDetail = FileDetail.multipartOf(multipartFile);
            String imgUrl = s3FileUploadService.store(artist, fileDetail.getPath(), multipartFile, username);

            // db에 이미지 이름 저장
            ArtistBoardImage boardImg = ArtistBoardImage.saveArtistBoardImg(imgUrl, artistBoard);
            artistBoardImageRepository.save(boardImg);*/

            try{
                byte[] bytes = multipartFile.getBytes();
                Path path = Paths.get(UPLOADED_FOLDER + multipartFile.getOriginalFilename());
                Files.write(path, bytes);

                // 파일 다운로드 URI 생성
                //String fileDownloadUri = "/files/" + multipartFile.getOriginalFilename();

                // 파일 정보를 데이터베이스에 저장
                ArtistBoardImage artistBoardImageTest = ArtistBoardImage.saveArtistBoardImg(multipartFile.getOriginalFilename(), artistBoard);
                artistBoardImageRepository.save(artistBoardImageTest);

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return ResponseEntity.ok().body("이미지 업로드 성공");
    }

    public ResponseEntity<PostResponseDTO> readArtistBoard(String artist, Long boardId, String username) throws MalformedURLException {
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

    @Transactional
    public ResponseEntity<String> saveMarketBoard(String title, String artist, String content, String price, List<MultipartFile> multipartFile, String username) throws IOException {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol joinIdol = checkJoinMember(username, artist);

        // 내용 저장
        ArtistMarketBoard artistMarketBoard = ArtistMarketBoard.createArtistMarketBoard(title, content, joinIdol, price);
        artistMarketBoardRepository.save(artistMarketBoard);

        // 이미지 저장
        for (MultipartFile image : multipartFile) {
            byte[] bytes = image.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER+"market/" + image.getOriginalFilename());
            Files.write(path, bytes);

            // 파일 다운로드 URI 생성
            //String fileDownloadUri = "/files/" + multipartFile.getOriginalFilename();

            // db에 이미지 이름 저장
            ArtistMarketBoardImage boardImg = ArtistMarketBoardImage.saveArtistBoardImg(image.getOriginalFilename(), artistMarketBoard);
            artistMarketBoardImageRepository.save(boardImg);
        }

        return ResponseEntity.ok().body("이미지 업로드 성공");
    }

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

            MarketPostListResponseDTO marketPostListResponseDTO = new MarketPostListResponseDTO(findArtistMarketBoard.getId(), findArtistMarketBoard.getTitle(), findArtistMarketBoard.getPrice(), isLike, "/image/market/"+findArtistMarketBoard.getImageUrlList().get(0).getArtistMarketBoardImgURL());
            responseDTOList.add(marketPostListResponseDTO);
        }

        return ResponseEntity.ok(responseDTOList);
    }

    private Optional<List<String>> createImageList(ArtistBoard findArtistBoard) {
        List<String> imageList = new ArrayList<>();
        for (ArtistBoardImage boardImage : findArtistBoard.getImageUrlList()) {
            imageList.add("/image/"+boardImage.getArtistBoardImgURL());
        }

        return Optional.of(imageList);
    }
}
