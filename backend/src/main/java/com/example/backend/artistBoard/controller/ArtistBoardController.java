package com.example.backend.artistBoard.controller;

import com.example.backend.artistBoard.dto.*;
import com.example.backend.artistBoard.dto.market.*;
import com.example.backend.artistBoard.repository.ArtistPlaceResponse;
import com.example.backend.artistBoard.service.ArtistBoardImageService;
import com.example.backend.artistBoard.service.ArtistBoardService;
import com.example.backend.artistBoard.service.RedisVoteService;
import com.example.backend.common.exception.dto.ErrorDTO;
import com.example.backend.member.dto.LoginInfoDto;
import com.example.backend.member.dto.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/idol/board")
@Tag(name = "[아티스트 게시글]", description = "아티스트 검색 API")
public class ArtistBoardController {

    private final ArtistBoardService artistBoardService;
    private final RedisVoteService redisVoteService;
    private final ArtistBoardImageService artistBoardImageService;

    @Operation(summary = "게시글 저장", description = "게시글 작성 시 저장하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 저장 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 작성하려는 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/{artist}")
    public ResponseEntity<String> savePost(@PathVariable String artist,
                                           @RequestBody SavePostRequestDTO savePostRequestDTO,
                                           Authentication authentication) {
        log.info("savePost");

        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistBoardService.savePost(artist, savePostRequestDTO.getContent(), loginInfoDto.getUsername());
    }

    @Operation(summary = "이미지 포함 게시글 저장", description = "이미지 포함된 게시글 작성 시 저장하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 포함된 게시글 저장 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 작성하려는 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/{artist}/image")
    public ResponseEntity<String> saveImgPost(@PathVariable String artist,
                                              @RequestPart("content") String content,
                                              @RequestPart("postImg") List<MultipartFile> multipartFile,
                                              Authentication authentication) throws IOException {
        log.info("saveImgPost artis: {}", artist);

        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        //return artistBoardService.saveImgPost(artist, content, multipartFile, loginInfoDto.getUsername());
        return artistBoardImageService.saveImgPost(artist, content, multipartFile, loginInfoDto.getUsername());
    }

    @Operation(summary = "투표 포함 게시글 저장", description = "투표 포함된 게시글 작성 시 저장하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투표 포함된 게시글 저장 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 작성하려는 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/{artist}/vote")
    public ResponseEntity<String> saveVotePost(@PathVariable @Valid @NotBlank String artist, @RequestBody @Valid SaveVotePostRequestDTO saveVotePostRequestDTO, Authentication authentication) {
        log.info("saveVotePost");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.saveVotePost(artist, saveVotePostRequestDTO.getContent(), saveVotePostRequestDTO.getVoteChoice(), saveVotePostRequestDTO.getVoteDay(), saveVotePostRequestDTO.getVoteHour(), loginInfoDto.getUsername());
    }

    // vote 결과 저장
    // Redis 이용하기
    @Operation(summary = "투표 결과 Redis 저장", description = "투표 결과를 Redis에 저장하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투표 결과 저장 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "중복 투표 발생함", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
    })
    @PostMapping("/vote/{boardId}")
    public ResponseEntity<String> saveVoteResult(@PathVariable @Valid @Min(value = 0L) Long boardId, Authentication authentication, @RequestBody @Valid VoteRequestDTO voteRequestDTO) {
        log.info("saveVoteResult: 투표 결과 저장 ");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return redisVoteService.addVote(loginInfoDto.getUsername(), boardId, voteRequestDTO.getChoice());
    }

    @Operation(summary = "게시글 전체 페이지 요청", description = "게시글 전체 페이지 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 전체 페이지 요청 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
    })
    @GetMapping("/{artist}/{nickname}/getTotalPage")
    public ResponseEntity<Integer> getTotalPage(@PathVariable String artist, @PathVariable String nickname) {
        log.info("getTotalPage");

        return artistBoardService.getUserFollowPostTotalPage(nickname, artist);
    }
// ================
    @Operation(summary = "회원이 팔로우 한 게시글 리스트 요청", description = "회원이 팔로우 한 게시글 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원이 팔로우 한 게시글 요청 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
    })
    @GetMapping("/{artist}/{nickname}/{page}")
    public ResponseEntity<List<PostListResponseDTO>> getUserFollowPost(@PathVariable String artist, @PathVariable String nickname, @PathVariable Integer page, Authentication authentication) {
        log.info("getUserFollowPost");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.getUserFollowPost(nickname, artist, page);
    }

    @Operation(summary = "특정 게시글 요청", description = "회원이 팔로우 한 게시글 중 특정 게시글 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 결과 리턴 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
    })
    @GetMapping("/{artist}/{boardId}")
    public ResponseEntity<PostResponseDTO> readArtistBoard(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication) throws MalformedURLException {
        log.info("readArtistBoard: " + boardId);
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.readArtistBoard(artist, boardId, loginInfoDto.getUsername());
        //return artistBoardImageService.readArtistBoard(artist, boardId, loginInfoDto.getUsername());
    }

    // 댓글 저장
    @Operation(summary = "댓글 저장", description = "댓글 저장하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 저장 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "게시글이나 대댓글일 경우 부모 댓글이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/{artist}/{boardId}/comment")
    public ResponseEntity<String> saveComment(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication, @RequestBody CommentRequestDTO commentRequestDTO) {
        log.info("댓글 저장");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.saveComment(artist, boardId, loginInfoDto.getUsername(), commentRequestDTO.getComment(), commentRequestDTO.getBoardCommentId());
    }

    @Operation(summary = "댓글 리스트 출력", description = "특정 게시글의 댓글 리스트 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 출력 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
    })
    @GetMapping("/{artist}/{boardId}/comment")
    public ResponseEntity<List<CommentResponseDTO>> readComment(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication) {
        log.info("댓글 리스트 출력");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.readArtistBoardComment(artist, boardId, loginInfoDto.getUsername());
    }

    @Operation(summary = "댓글 삭제", description = "댓글 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 작성하려는 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @DeleteMapping("/{artist}/{boardId}/comment/{boardCommentId}")
    public ResponseEntity<String> deleteComment(@PathVariable String artist, @PathVariable Long boardId, @PathVariable Long boardCommentId, Authentication authentication) {
        log.info("댓글 삭제");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.deleteComment(artist, boardId, loginInfoDto.getUsername(), boardCommentId);
    }

    @Operation(summary = "게시글 좋아요 요청", description = "게시글 좋아요 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 요청 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원, 아티스트, 게시글이 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/{artist}/{boardId}/like")
    public ResponseEntity<String> boardLike(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication) {
        log.info("게시글 좋아요");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.boardLike(artist, boardId, loginInfoDto.getUsername());
    }

    @Operation(summary = "게시글 북마크 요청", description = "게시글 북마크 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 요청 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원, 아티스트, 게시글이 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/{artist}/{boardId}/bookmark")
    public ResponseEntity<String> boardBookmark(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication) {
        log.info("게시글 북마크");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.boardBookmark(artist, boardId, loginInfoDto.getUsername());
    }

    @Operation(summary = "판매 게시글 저장", description = "판매 게시글 작성 시 저장하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "판매 게시글 저장 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 작성하려는 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/{artist}/market")
    public ResponseEntity<String> saveMarketPost(@PathVariable String artist,
                                                 @RequestParam("title") @Valid @NotBlank String title,
                                                 @RequestParam("content") @Valid @NotBlank String content,
                                                 @RequestParam("price") @Valid @NotBlank String price,
                                                 @RequestPart("postImg") List<MultipartFile> multipartFile,
                                                 Authentication authentication) throws IOException {
        log.info("market board 저장"+ multipartFile);

        if(multipartFile.isEmpty() ||( multipartFile.size() == 1 && Objects.equals(multipartFile.get(0).getOriginalFilename(), ""))){
            return ResponseEntity.badRequest().body("postImg is required");
        }

        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardImageService.saveMarketBoard(title, artist, content, price, multipartFile, loginInfoDto.getUsername());
        //return artistBoardService.saveMarketBoard(title, artist, content, price, multipartFile, loginInfoDto.getUsername());
    }

    @Operation(summary = "판매 게시글 전체 페이지 요청", description = "판매 게시글 전체 페이지 수를 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 페이지 요청 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 해당 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @GetMapping("/{artist}/market/getTotalPage")
    public ResponseEntity<Integer> getMarketPostTotalPage(@PathVariable String artist, Authentication authentication) {
        log.info("getMarketPostTotalPage");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.getMarketPostTotalPage(artist, loginInfoDto.getUsername());
    }

    @Operation(summary = "아티스트 판매 게시글 리스트 요청", description = "특정한 아티스트의 판매 게시글 리스트 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "판매 게시글 리스트 요청 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 해당 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @GetMapping("/{artist}/market/{nickname}/{page}")
    public ResponseEntity<List<MarketPostListResponseDTO>> readMarketPostList(@PathVariable String artist, @PathVariable String nickname, @PathVariable Integer page, Authentication authentication) {
        log.info("readMarketPostList");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        //return artistBoardImageService.readMarketBoardList(artist, nickname, page, loginInfoDto.getUsername());
        return artistBoardService.readMarketBoardList(artist, nickname, page, loginInfoDto.getUsername());
    }

    @Operation(summary = "특정 판매 게시글 요청", description = "특정한 한 판매 페이지를 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 읽기 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 해당 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @GetMapping("/{artist}/market/board/{boardId}")
    public ResponseEntity<MarketPostResponseDTO> readMarketPost(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication) {
        log.info("readMarketPost");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.readMarketBoard(artist, boardId, loginInfoDto.getUsername());
    }

    @Operation(summary = "마켓 게시글 좋아요 요청", description = "특정한 마켓 게시글에 좋아요 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마켓 게시글에 좋아요 요청 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 해당 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/{artist}/market/{boardId}/like")
    public ResponseEntity<String> marketBoardLike(@PathVariable @Valid @NotBlank String artist, @PathVariable @Valid @Pattern(regexp = "\\d+") Long boardId, Authentication authentication) {
        log.info("게시글 좋아요");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.marketBoardLike(artist, boardId, loginInfoDto.getUsername());
    }

    @Operation(summary = "마켓 게시글 북마크 요청", description = "특정한 마켓 게시글에 북마크 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마켓 게시글에 북마크 요청 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 해당 아티스트가 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/{artist}/market/{boardId}/bookmark")
    public ResponseEntity<String> marketBoardBookmark(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication) {
        log.info("게시글 북마크");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.marketBoardBookmark(artist, boardId, loginInfoDto.getUsername());
    }

    @Operation(summary = "판매 게시글 상태 변경 요청", description = "판매 게시글 상태 변경 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투표 포함된 게시글 저장 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 해당 아티스트, 게시글이 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PatchMapping("/{artist}/market/{boardId}/modifyBoardStatus")
    public ResponseEntity<String> modifyMarketBoardStatus(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication, @RequestBody MarketBoardModifyBoardStatusRequestDTO marketBoardModifyBoardStatusRequestDTO) {
        log.info("상품 상태 변경");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.modifyMarketBoardStatus(artist, boardId, marketBoardModifyBoardStatusRequestDTO.getBoardStatus(), loginInfoDto.getUsername());
    }

    @Operation(summary = "마켓 게시글 삭제", description = "마켓 게시글 삭제 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마켓 게시글 삭제 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 해당 아티스트, 게시글이 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @DeleteMapping("/{artist}/market/{boardId}")
    public ResponseEntity<String> deleteMarketBoard(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication) {
        log.info("marketBoard 삭제");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.deleteMarketBoard(artist, boardId, loginInfoDto.getUsername());
    }

    @Operation(summary = "변경 가능한 게시글 내용 요청", description = "변경 가능한 게시글 내용 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 가져오기 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 해당 아티스트, 게시글이 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @GetMapping("/{artist}/market/{boardId}/modify")
    public ResponseEntity<MarketBoardModifyResponseDTO> getModifyMarketBoard(@PathVariable String artist, @PathVariable Long boardId, Authentication authentication) {
        log.info("marketBoard 변경할 값 가져오기");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.getModifyMarketBoard(artist, boardId, loginInfoDto.getUsername());
    }

    @Operation(summary = "마켓 게시글 변경", description = "마켓 게시글 변경 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 변경 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "아티스트에 가입된 회원이 아님", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이나 해당 아티스트, 게시글이 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PutMapping("/{artist}/market/{sellNum}/modify")
    public ResponseEntity<String> modifyMarketBoard(@PathVariable String artist, @PathVariable Long sellNum, Authentication authentication,
                                                    @RequestParam("title") String title,
                                                    @RequestParam("content") String content,
                                                    @RequestParam("price") String price,
                                                    @RequestParam(value = "deleteImgList", required = false) List<Long> deleteImgList,
                                                    @RequestPart(value = "addImgList", required = false) List<MultipartFile> addImgList) {
        log.info("marketBoard 변경하기");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.modifyMarketBoard(artist, sellNum, loginInfoDto.getUsername(),
                title, price, content, deleteImgList, addImgList);
    }

    @Operation(summary = "가장 인기있는 게시글 요청", description = "가장 인기있는 게시글 요청하는 API")
    @GetMapping("/{artist}/getPopularBoard")
    public ResponseEntity<List<PostListResponseDTO>> getPopularBoard(@PathVariable String artist, Authentication authentication){
        log.info("가장 인기있는 게시글 출력하기 ");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistBoardService.getPopularBoard(artist, loginInfoDto.getUsername());
    }

    @PostMapping("/{artist}/{boardNum}/comment/{commentId}/like")
    public ResponseEntity<String> boardCommentLike(@PathVariable String artist, @PathVariable Long boardNum, @PathVariable Long commentId, Authentication authentication){
        log.info("댓글 좋아요 누르기");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.saveCommentLike(artist, boardNum, commentId, loginInfoDto.getUsername());
    }

    @PostMapping("/{artist}/{boardNum}/comment/{commentId}/bookmark")
    public ResponseEntity<String> boardCommentBookmark(@PathVariable String artist, @PathVariable Long boardNum, @PathVariable Long commentId, Authentication authentication){
        log.info("댓글 북마크 누르기");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.saveCommentBookmark(artist, boardNum, commentId, loginInfoDto.getUsername());
    }

    // 마켓 댓글 저장
    @PostMapping("/{artist}/{sellNum}/marketComment")
    public ResponseEntity<String> saveMarketComment(@PathVariable String artist, @PathVariable Long sellNum, Authentication authentication, @RequestBody CommentRequestDTO commentRequestDTO) {
        log.info("마켓 댓글 저장");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.saveMarketComment(artist, sellNum, loginInfoDto.getUsername(), commentRequestDTO.getComment(), commentRequestDTO.getBoardCommentId());
    }

    @GetMapping("/{artist}/{sellNum}/marketComment")
    public ResponseEntity<List<MarketCommentResponseDTO>> getMarketComment(@PathVariable String artist, @PathVariable Long sellNum, Authentication authentication) {
        log.info("마켓 댓글 가져오기");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.readMarketBoardComment(artist, sellNum, loginInfoDto.getUsername());
    }

    @PostMapping("/{artist}/{sellNum}/MarketComment/{commentId}/like")
    public ResponseEntity<String> marketBoardCommentLike(@PathVariable String artist, @PathVariable Long sellNum, @PathVariable Long commentId, Authentication authentication){
        log.info("마켓 댓글 좋아요 누르기");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.saveMarketCommentLike(artist, sellNum, commentId, loginInfoDto.getUsername());
    }

    @PostMapping("/{artist}/{sellNum}/MarketComment/{commentId}/bookmark")
    public ResponseEntity<String> marketBoardCommentBookmark(@PathVariable String artist, @PathVariable Long sellNum, @PathVariable Long commentId, Authentication authentication){
        log.info("마켓 댓글 북마크 누르기");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.saveMarketCommentBookmark(artist, sellNum, commentId, loginInfoDto.getUsername());
    }

    @DeleteMapping("/{artist}/{boardId}/MarketComment/{boardCommentId}")
    public ResponseEntity<String> deleteMarketComment(@PathVariable String artist, @PathVariable Long boardId, @PathVariable Long boardCommentId, Authentication authentication) {
        log.info("댓글 삭제");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        return artistBoardService.deleteMarketComment(artist, boardId, loginInfoDto.getUsername(), boardCommentId);
    }

    @PostMapping("/{artist}/savePlace")
    public ResponseEntity<String> savePlace(@PathVariable String artist,
                                            @RequestParam("Lat") String lat,
                                            @RequestParam("Lng") String lng,
                                            @RequestParam("placeName") String placeName,
                                            @RequestParam("addressName") String addressName,
                                            @RequestParam("roadAddressName") String roadAddressName,
                                            @RequestParam("placeUrl") String placeUrl,
                                            @RequestParam("memo") String memo,
                                            @RequestPart("placeImage") MultipartFile multipartFile,
                                            Authentication authentication){
        log.info("savePlace");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistBoardService.savePlace(artist, lat, lng, placeName, addressName, roadAddressName, placeUrl, memo, multipartFile, loginInfoDto.getUsername());
    }

    @GetMapping("/{artist}/getPlace")
    public ResponseEntity<List<ArtistPlaceResponse>> getArtistPlace(@PathVariable String artist, Authentication authentication){
        log.info("getArtistPlace");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistBoardService.getArtistPlace(artist, loginInfoDto.getUsername());
    }

    @GetMapping("/{artist}/search/{keyword}")
    public ResponseEntity<List<PostListResponseDTO>> getSearchBoard(@PathVariable String artist, @PathVariable String keyword, Authentication authentication){
        log.info("getSearchBoard");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistBoardService.getSearchBoard(artist, keyword, loginInfoDto.getUsername());
    }

    @GetMapping("/{artist}/searchMarket/{keyword}")
    public ResponseEntity<List<MarketPostListResponseDTO>> getSearchMarketBoard(@PathVariable String artist, @PathVariable String keyword, Authentication authentication){
        log.info("getSearchMarketBoard");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistBoardService.getSearchMarketBoard(artist, keyword, loginInfoDto.getUsername());
    }

    @GetMapping("/{artist}/searchUser/{keyword}")
    public ResponseEntity<List<SearchUserNicknameResponseDTO>> getSearchKeyword(@PathVariable String artist, @PathVariable String keyword, Authentication authentication){
        log.info("getSearchMarketBoard");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistBoardService.getSearchKeyword(artist, keyword, loginInfoDto.getUsername());
    }
}