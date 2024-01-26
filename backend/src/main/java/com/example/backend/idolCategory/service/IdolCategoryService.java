package com.example.backend.idolCategory.service;

import com.example.backend.idolCategory.dto.IdolCategoryJoinResponseDTO;
import com.example.backend.idolCategory.dto.IdolCategoryResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IdolCategoryService {
    ResponseEntity<String> addIdolCategory(String artist, String artistImg, String artistGenre, String artistType, String username);

    ResponseEntity<List<IdolCategoryResponseDTO>> getIdolCategory(Integer page);

    ResponseEntity<List<IdolCategoryResponseDTO>> getUserIdolCategory(String username, String username1, Integer page);

    ResponseEntity<Integer> getTotalPage();

    ResponseEntity<IdolCategoryJoinResponseDTO> joinIdolCategory(Long id, String username);
}
