package com.example.backend.idolCategory.controller;

import com.example.backend.idolCategory.dto.IdolCategoryInfoDTO;
import com.example.backend.idolCategory.dto.IdolCategoryJoinRequestDTO;
import com.example.backend.idolCategory.dto.IdolCategoryResponseDTO;
import com.example.backend.idolCategory.dto.IdolTestCategoryInfoDTO;
import com.example.backend.idolCategory.service.GetIdolInfoCrawlingService;
import com.example.backend.idolCategory.service.IdolCategoryService;
import com.example.backend.member.dto.LoginInfoDto;
import com.querydsl.core.Tuple;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "IdolCategoryController", description = "아이돌 검색 API")
public class IdolCategoryController {

    private final GetIdolInfoCrawlingService getIdolInfoCrawlingService;
    private final IdolCategoryService idolCategoryService;

    @GetMapping("/search/{artist}")
    public ResponseEntity<List<IdolCategoryInfoDTO>> getIdolInfo(Authentication authentication, @PathVariable String artist){
        log.info("artist: {}", authentication.getPrincipal());

        return getIdolInfoCrawlingService.getIdolInfo(artist);
    }

    @PostMapping("/addCategory")
    public ResponseEntity<String> addIdolCategory(@RequestBody IdolCategoryInfoDTO idolCategoryInfoDto, Authentication authentication){
        log.info("addIdolCategory: {}", authentication.getName());

        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        log.info(loginInfoDto.getNickname());
        log.info(loginInfoDto.getUsername());

        return idolCategoryService.addIdolCategory(
                idolCategoryInfoDto.getArtist(), idolCategoryInfoDto.getArtistImg(), idolCategoryInfoDto.getArtistGenre(), idolCategoryInfoDto.getArtistType(), loginInfoDto.getUsername());
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<List<IdolCategoryResponseDTO>> getIdolCategory(@PathVariable Integer page){
        log.info("getIdolCategory:{}", page);
        return idolCategoryService.getIdolCategory(page);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<IdolCategoryResponseDTO>> getUserIdolCategory(@PathVariable String username, Authentication authentication){
        log.info("getUserIdolCategory");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return idolCategoryService.getUserIdolCategory(username, loginInfoDto.getUsername());
    }

    @GetMapping("/getTotalPage")
    public ResponseEntity<Integer> getTotalPage(){
        log.info("getTotalPage");
        return idolCategoryService.getTotalPage();
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody IdolCategoryJoinRequestDTO idolCategoryJoinRequestDTO, Authentication authentication){
        log.info("join: {}", idolCategoryJoinRequestDTO.getId());
        LoginInfoDto loginInfo = (LoginInfoDto) authentication.getPrincipal();
        return idolCategoryService.joinIdolCategory(idolCategoryJoinRequestDTO.getId(), loginInfo.getUsername());
    }
}
