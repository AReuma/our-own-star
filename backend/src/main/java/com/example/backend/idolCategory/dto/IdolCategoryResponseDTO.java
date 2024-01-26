package com.example.backend.idolCategory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdolCategoryResponseDTO {

    @NotBlank(message = "artistId not blank")
    @Schema(name = "아티스트 아이디", example = "1")
    private Long id;

    @NotBlank(message = "artist not blank")
    @Schema(name = "아티스트 이름", example = "EXO")
    private String artist;

    @NotBlank(message = "artistImg not blank")
    @Schema(name = "아티스트 이미지", example = "https://image.bugsm.co.kr/artist/images/130/801559/80155940.jpg?version=20230708002114.0")
    private String artistImg;

    @NotBlank(message = "artist not blank")
    @Schema(name = "아티스트 장르", example = "댄스/팝")
    private String artistGenre;

    @NotBlank(message = "artist not blank")
    @Schema(name = "아티스트 타입", example = "그룹 (남성)")
    private String artistType;

    @NotBlank(message = "isJoin not blank")
    @Schema(name = "가입여부", example = "TRUE")
    private Boolean isJoin;
}
