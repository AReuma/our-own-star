package com.example.backend.member.repository;

import com.example.backend.member.entity.JoinIdol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JoinIdolRepository extends JpaRepository<JoinIdol, Long> {
    Optional<JoinIdol> findJoinIdolByMemberIdAndIdolCategoryId(Long memberId, Long IdolCategoryId);
}
