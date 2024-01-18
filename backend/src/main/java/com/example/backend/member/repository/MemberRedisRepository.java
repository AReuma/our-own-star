package com.example.backend.member.repository;

import com.example.backend.member.dto.EmailCertificationResponseDTO;
import org.springframework.data.repository.CrudRepository;

public interface MemberRedisRepository extends CrudRepository<EmailCertificationResponseDTO, String> {

}
