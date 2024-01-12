package com.example.backend.idolCategory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdolCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idol_category_id")
    private Long id;

    private String idolName;

    private String question;

    private String hint;

    private String answer;
}
