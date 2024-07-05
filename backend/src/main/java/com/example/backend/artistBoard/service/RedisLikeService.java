package com.example.backend.artistBoard.service;

import com.querydsl.core.types.dsl.StringTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisLikeService {

    private static final String ARTIST_BOARD = "boardLike:";
    private static final String ARTIST_MARKET_BOARD = "marketBoardLike:";
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean marketBoardLike(Long marketBoardId, Long joinIdolId){
        String key = ARTIST_MARKET_BOARD+marketBoardId+":"+joinIdolId;
        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();

        if (stringStringValueOperations.get(key) != null){
            return false;
        }

        stringStringValueOperations.set(key, "1", Duration.ofSeconds(10)); // 10초동안 저장

        return true;
    }
}
