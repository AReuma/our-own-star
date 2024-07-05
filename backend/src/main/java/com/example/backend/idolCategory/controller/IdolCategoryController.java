package com.example.backend.idolCategory.controller;

import com.example.backend.common.exception.dto.ErrorDTO;
import com.example.backend.idolCategory.dto.*;
import com.example.backend.idolCategory.service.GetIdolInfoCrawlingService;
import com.example.backend.idolCategory.service.IdolCategoryService;
import com.example.backend.member.dto.LoginInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/idol")
@Tag(name = "[아티스트 정보]", description = "아티스트 검색 API")
public class IdolCategoryController {

    private final GetIdolInfoCrawlingService getIdolInfoCrawlingService;
    private final IdolCategoryService idolCategoryService;

    @Operation(summary = "아티스트 찾기", description = "아티스트 찾기 API, 벅스에서 아티스트의 정보를 가지고와 전달하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아티스트 정보 전달 성공", content = @Content(schema = @Schema(implementation = IdolCategoryInfoDTO.class))),
            @ApiResponse(responseCode = "404", description = "아티스트 정보를 가져오기 실패", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @GetMapping("/search/{artist}")
    public ResponseEntity<List<IdolCategoryInfoDTO>> getIdolInfo(Authentication authentication, @PathVariable @Valid @NotBlank String artist){
        log.info("artist: {}", artist);

        return getIdolInfoCrawlingService.getIdolInfo(artist);
    }

    @Operation(summary = "아티스트 추가", description = "아티스트 추가 API, 서버에서 전달 된 아티스트의 정보를 가지고 아티스트의 카테고리를 생성하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아티스트 정보 전달 성공"),
            @ApiResponse(responseCode = "404", description = "저장하려는 회원의 정보가 없음", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "이미 생성된 아티스트", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/addCategory")
    public ResponseEntity<String> addIdolCategory(@RequestBody IdolCategoryInfoDTO idolCategoryInfoDto, Authentication authentication){
        log.info("addIdolCategory: {}", authentication.getName());

        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        log.info(loginInfoDto.getNickname());
        log.info(loginInfoDto.getUsername());

        return idolCategoryService.addIdolCategory(
                idolCategoryInfoDto.getArtist(), idolCategoryInfoDto.getArtistImg(), idolCategoryInfoDto.getArtistGenre(), idolCategoryInfoDto.getArtistType(), loginInfoDto.getUsername());
    }

    @Operation(summary = "인증 되지 않은 회원 아티스트 정보 전달", description = "인증되진 않은 회원에게 아티스트 정보를 전달하는 API, 페이지별로 아티스트의 정보 전달")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아티스트 정보 전달 성공", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = IdolCategoryResponseDTO.class))
            )),
    })
    @GetMapping("/page/{page}")
    public ResponseEntity<List<IdolCategoryResponseDTO>> getIdolCategory(@PathVariable Integer page){
        log.info("getIdolCategory:{}", page);
        return idolCategoryService.getIdolCategory(page);
    }

    @Operation(summary = "인증 된 회원에게 아티스트 정보 전달", description = "인증 된 회원에게 아티스트 정보를 전달하는 API, 페이지별로 아티스트의 정보 전달")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아티스트 정보 전달 성공", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = IdolCategoryResponseDTO.class))
            )),
            @ApiResponse(responseCode = "404", description = "회원의 정보가 없음", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
    })
    @GetMapping("/page/{page}/username")
    public ResponseEntity<List<IdolCategoryResponseDTO>> getUserIdolCategory(@PathVariable Integer page, Authentication authentication){
        log.info("getUserIdolCategory");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return idolCategoryService.getUserIdolCategory(loginInfoDto.getUsername(), page);
    }

    @Operation(summary = "아티스트 정보 마지막 페이지 요청", description = "아티스트 마지막 페이지 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아티스트 페이지 크기 전달 성공")
    })
    @GetMapping("/getTotalPage")
    public ResponseEntity<Integer> getTotalPage(){
        log.info("getTotalPage");
        return idolCategoryService.getTotalPage();
    }

    @Operation(summary = "아티스트 페이지 가입", description = "아티스트 페이지 가입 API, 아티스트의 페이지를 이용하기 위해서 가입 요청하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아티스트 페이지 가입 성공"),
            @ApiResponse(responseCode = "404", description = "저장하려는 회원의 정보가 없거나 찾는 회원이 존재하지않음", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "이미 가입 된 회원", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/join")
    public ResponseEntity<IdolCategoryJoinResponseDTO> join(@RequestBody IdolCategoryJoinRequestDTO idolCategoryJoinRequestDTO, Authentication authentication){
        log.info("join: {}", idolCategoryJoinRequestDTO.getId());
        LoginInfoDto loginInfo = (LoginInfoDto) authentication.getPrincipal();
        return idolCategoryService.joinIdolCategory(idolCategoryJoinRequestDTO.getId(), loginInfo.getUsername());
    }

    @GetMapping("/join/{artist}/userInfo")
    public ResponseEntity<JoinIdolCategoryUserInfoDTO> joinIdolUserInfo(@PathVariable String artist, Authentication authentication){
        log.info("id: {}", artist);
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return idolCategoryService.joinIdolUserInfo(artist, loginInfoDto.getUsername());
    }

    @GetMapping("/find/{artist}")
    public ResponseEntity<List<IdolCategoryResponseDTO>> searchArtistName(@PathVariable String artist, Authentication authentication){
        log.info("searchArtistName");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return idolCategoryService.searchArtistName(artist, loginInfoDto.getUsername());
    }

    @GetMapping("/find/beforeLogin/{artist}")
    public ResponseEntity<List<IdolCategoryResponseDTO>> searchBeforeLoginArtistName(@PathVariable String artist){
        log.info("searchBeforeLoginArtistName");
        return idolCategoryService.searchBeforeLoginArtistName(artist);
    }

    @PostMapping("/follow/user")
    public ResponseEntity<String> followUser(@RequestBody @Valid FollowUser followUser, Authentication authentication){
        log.info("followUser");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return idolCategoryService.followUser(followUser.getArtist(), followUser.getFollowUser(), loginInfoDto.getUsername());
    }
}
