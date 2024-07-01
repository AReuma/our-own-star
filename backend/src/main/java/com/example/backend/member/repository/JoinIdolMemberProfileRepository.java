package com.example.backend.member.repository;

import com.example.backend.member.entity.JoinIdolMemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JoinIdolMemberProfileRepository extends JpaRepository<JoinIdolMemberProfile, Long> {
}
