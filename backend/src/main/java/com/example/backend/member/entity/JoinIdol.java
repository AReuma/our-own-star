package com.example.backend.member.entity;

import com.example.backend.idolCategory.entity.IdolCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinIdol {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_idol_id")
    private Long id;

    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idol_category_id")
    private IdolCategory idolCategory;
}
