package com.example.backend.idolCategory.entity;

import com.example.backend.common.TimeStamped;
import com.example.backend.member.entity.JoinIdol;
import com.example.backend.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdolCategory extends TimeStamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idol_category_id")
    private Long id;

    @Column(unique = true)
    private String artist;

    private String artistImg;

    private String artistGenre;

    private String artistType;

    /*private String question;

    private String hint;

    private String answer;*/

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static IdolCategory createIdolCategory(String artist, String artistImg, String artistGenre, String artistType, Member member){
        return IdolCategory.builder()
                .artist(artist)
                .artistImg(artistImg)
                .artistGenre(artistGenre)
                .artistType(artistType)
                .member(member)
                .build();
    }
}
