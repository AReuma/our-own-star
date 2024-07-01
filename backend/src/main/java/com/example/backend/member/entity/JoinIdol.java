package com.example.backend.member.entity;

import com.example.backend.idolCategory.entity.IdolCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinIdol {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_idol_id")
    private Long id;

    @Column(unique = true)
    private String nickname;

    private Boolean isFirst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idol_category_id")
    private IdolCategory idolCategory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_member_profile_id")
    private JoinIdolMemberProfile joinIdolMemberProfile;

    @Lob
    private String userInfo;

    @Transient
    private final Set<String> usedNicknames = Collections.synchronizedSet(new HashSet<>());

    public String generateRandomNickname() {
        Random random = new Random();

        while (true) {
            String randomNickname = "ourownstar" + (random.nextInt(1000) + 1);

            if (!usedNicknames.contains(randomNickname)) {
                usedNicknames.add(randomNickname);
                return randomNickname;
            }
        }
    }

    public void addNickname(String nickname){
        this.nickname = nickname;
        usedNicknames.add(nickname);
    }

    public static JoinIdol createJoinIdol(Member member, IdolCategory idolCategory, JoinIdolMemberProfile joinIdolMemberProfile){
        JoinIdol joinIdol = JoinIdol.builder()
                .member(member)
                .idolCategory(idolCategory)
                .joinIdolMemberProfile(joinIdolMemberProfile)
                .isFirst(Boolean.TRUE)
                .build();

        joinIdol.addNickname(joinIdol.generateRandomNickname());

        return joinIdol;
    }

    public void updateUserInfo(String nickname, String userInfo){
        this.nickname = nickname;
        this.userInfo = userInfo;
    }

    public void changeIsFirst() {
        this.isFirst = false;
    }
}
