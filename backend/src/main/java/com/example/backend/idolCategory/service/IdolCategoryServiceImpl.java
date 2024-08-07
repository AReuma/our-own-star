package com.example.backend.idolCategory.service;

import com.example.backend.artistBoard.entity.ArtistFollow;
import com.example.backend.artistBoard.repository.ArtistFollowRepository;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.idolCategory.dto.IdolCategoryJoinResponseDTO;
import com.example.backend.idolCategory.dto.IdolCategoryResponseDTO;
import com.example.backend.idolCategory.dto.JoinIdolCategoryUserInfoDTO;
import com.example.backend.idolCategory.entity.IdolCategory;
import com.example.backend.idolCategory.repository.IdolCategoryRepository;
import com.example.backend.member.entity.JoinIdol;
import com.example.backend.member.entity.JoinIdolMemberProfile;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.JoinIdolMemberProfileRepository;
import com.example.backend.member.repository.JoinIdolRepository;
import com.example.backend.member.repository.MemberRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.backend.idolCategory.entity.QIdolCategory.idolCategory;
import static com.example.backend.member.entity.QJoinIdol.joinIdol;
import static com.example.backend.member.entity.QMember.member;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IdolCategoryServiceImpl implements IdolCategoryService{

    private final IdolCategoryRepository idolCategoryRepository;
    private final JoinIdolRepository joinIdolRepository;
    private final MemberRepository memberRepository;
    private final JoinIdolMemberProfileRepository joinIdolMemberProfileRepository;
    private final ArtistFollowRepository artistFollowRepository;
    private final EntityManager em;

    @Override
    @Transactional
    public ResponseEntity<String> addIdolCategory(String artist, String artistImg, String artistGenre, String artistType, String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER_ID, "사용자를 찾을 수 없습니다."));

        idolCategoryRepository.findByArtist(artist).ifPresent((idolCategory -> {
            throw new AppException(ErrorCode.IDOL_CATEGORY_DUPLICATED, "이미 생성된 아이돌 입니다.");
        }));

        IdolCategory idolCategory = IdolCategory.createIdolCategory(artist, artistImg, artistGenre, artistType, member);

        idolCategoryRepository.save(idolCategory);

        return ResponseEntity.ok().body("저장 완료");
    }

    /**
     * 카테고리 정렬 순서
     * 1. 아티스티 이름 오름차순
     */
    @Override
    public ResponseEntity<List<IdolCategoryResponseDTO>> getIdolCategory(Integer page) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<IdolCategoryResponseDTO> response = queryFactory
                .select(Projections.constructor(IdolCategoryResponseDTO.class,
                        idolCategory.id,
                        idolCategory.artist,
                        idolCategory.artistImg,
                        idolCategory.artistGenre,
                        idolCategory.artistType,
                        Expressions.constant(false)))
                .from(idolCategory)
                .orderBy(idolCategory.artist.asc())
                .offset(4L * (page -1))
                .limit(4)
                .fetch();

        return ResponseEntity.ok().body(response);
    }

    /**
     * 카테고리 정렬 순서
     * 1. 내가 가입한 카테고리
     * 2. 아티스트 오름차순
     */
    @Override
    public ResponseEntity<List<IdolCategoryResponseDTO>> getUserIdolCategory(String username, Integer page) {
        //if (!username.equals(username1)) throw new AppException(ErrorCode.NOT_FOUND_USER_ID, "회원을 찾을 수 없습니다.");

        //Member member = memberRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER_ID, "회원을 찾을 수 없습니다."));
        // select * from idol_category
        // LEFT JOIN join_idol ON idol_category.idol_category_id = join_idol.idol_category_id and join_idol.member_id=1
        // LEFT OUTER JOIN member ON member.member_id=join_idol.member_id;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Tuple> fetch = queryFactory
                .selectDistinct(
                        idolCategory.id,
                        idolCategory.artist,
                        idolCategory.artistImg,
                        idolCategory.artistGenre,
                        idolCategory.artistType,
                       // member.username,
                        joinIdol.id
                )
                .from(idolCategory)
                .leftJoin(joinIdol)
                .on(joinIdol.idolCategory.eq(idolCategory)
                        .and(joinIdol.member.username.eq(username).or(joinIdol.member.isNull())))
                //.leftJoin(member).on(joinIdol.member.eq(member).and(joinIdol.member.username.eq(username)))
                .orderBy(
                        new CaseBuilder()
                                .when(joinIdol.id.isNotNull()).then(0) // 가입한 카테고리
                                .otherwise(1) // 가입하지 않은 카테고리
                                .asc(),
                        idolCategory.artist.asc()
                )
                .offset(4L * (page -1))
                .limit(4)
                .fetch();

        List<IdolCategoryResponseDTO> resultList = fetch.stream()
                .map(tuple -> new IdolCategoryResponseDTO(
                        tuple.get(idolCategory.id),
                        tuple.get(idolCategory.artist),
                        tuple.get(idolCategory.artistImg),
                        tuple.get(idolCategory.artistGenre),
                        tuple.get(idolCategory.artistType),
                        //tuple.get(member.username) != null
                        tuple.get(joinIdol.id) != null
                ))
                .toList();

        return ResponseEntity.ok().body(resultList);
    }

    @Override
    public ResponseEntity<Integer> getTotalPage() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        int totalResults = Math.toIntExact(queryFactory
                .selectFrom(idolCategory)
                .fetchCount());

        Integer totalPages = (int) Math.ceil((double) totalResults / 4);

        return ResponseEntity.ok().body(totalPages);
    }

    @Override
    @Transactional
    public ResponseEntity<IdolCategoryJoinResponseDTO> joinIdolCategory(Long id, String username) {
        // 가입할 아티스트 찾기
        IdolCategory artist = idolCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.IDOL_CATEGORY_NOT_FOUND, "찾는 아티스트가 없습니다."));

        // 가입할 회원 찾기
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER_ID, "회원을 찾을 수 없습니다."));

        // 이미 가입한 회원인지 확인
        joinIdolRepository.findJoinIdolByMemberIdAndIdolCategoryId(member.getId(), artist.getId())
                .ifPresent((joinIdol -> {
                    throw new AppException(ErrorCode.ALREADY_JOIN_USER, "이미 가입한 회원입니다.");
                }));

        // 회원 프로필 이미지 저장
        JoinIdolMemberProfile joinIdolMemberProfile = JoinIdolMemberProfile.createJoinIdolMemberProfile();
        joinIdolMemberProfileRepository.save(joinIdolMemberProfile);

        // 회원 - 아이돌 카테고리
        JoinIdol joinIdol = JoinIdol.createJoinIdol(member, artist, joinIdolMemberProfile);
        joinIdolRepository.save(joinIdol);

        return ResponseEntity.ok().body(new IdolCategoryJoinResponseDTO(artist.getArtist()));
    }

    @Override
    @Transactional
    public ResponseEntity<JoinIdolCategoryUserInfoDTO> joinIdolUserInfo(String artist, String username) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        JoinIdol findJoinIdol = queryFactory
                .selectFrom(joinIdol)
                .where(joinIdol.member.username.eq(username).and(joinIdol.idolCategory.artist.eq(artist)))
                .fetchOne();

        assert findJoinIdol != null;
        findJoinIdol.changeIsFirst();

        return ResponseEntity.ok().body(new JoinIdolCategoryUserInfoDTO(findJoinIdol.getNickname(), findJoinIdol.getIsFirst(), findJoinIdol.getJoinIdolMemberProfile().getImgUrl()));
    }

    @Override
    public ResponseEntity<List<IdolCategoryResponseDTO>> searchArtistName(String artist, String username) {
        if (!username.equals(username)) throw new AppException(ErrorCode.NOT_FOUND_USER_ID, "회원을 찾을 수 없습니다.");

        BooleanExpression predicate = idolCategory.artist.containsIgnoreCase(artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Tuple> fetch = queryFactory
                .selectDistinct(
                        idolCategory.id,
                        idolCategory.artist,
                        idolCategory.artistImg,
                        idolCategory.artistGenre,
                        idolCategory.artistType,
                        member.username,
                        joinIdol.id
                )
                .from(idolCategory)
                .leftJoin(joinIdol)
                .on(joinIdol.idolCategory.eq(idolCategory)
                        .and(joinIdol.member.username.eq(username).or(joinIdol.member.isNull())))
                .leftJoin(member).on(joinIdol.member.eq(member).and(joinIdol.member.username.eq(username)))
                .where(predicate)
                .orderBy(
                        new CaseBuilder()
                                .when(joinIdol.id.isNotNull()).then(0) // 가입한 카테고리
                                .otherwise(1) // 가입하지 않은 카테고리
                                .asc(),
                        idolCategory.artist.asc()
                )
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("tuple = "+tuple);
        }

        List<IdolCategoryResponseDTO> resultList = fetch.stream()
                .map(tuple -> new IdolCategoryResponseDTO(
                        tuple.get(idolCategory.id),
                        tuple.get(idolCategory.artist),
                        tuple.get(idolCategory.artistImg),
                        tuple.get(idolCategory.artistGenre),
                        tuple.get(idolCategory.artistType),
                        tuple.get(member.username) != null
                ))
                .toList();

        return ResponseEntity.ok().body(resultList);
    }

    @Override
    public ResponseEntity<List<IdolCategoryResponseDTO>> searchBeforeLoginArtistName(String artist) {
        BooleanExpression predicate = idolCategory.artist.containsIgnoreCase(artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Tuple> fetch = queryFactory
                .selectDistinct(
                        idolCategory.id,
                        idolCategory.artist,
                        idolCategory.artistImg,
                        idolCategory.artistGenre,
                        idolCategory.artistType
                )
                .from(idolCategory)
                .where(predicate)
                .orderBy(
                        idolCategory.artist.asc()
                )
                .fetch();

        List<IdolCategoryResponseDTO> resultList = fetch.stream()
                .map(tuple -> new IdolCategoryResponseDTO(
                        tuple.get(idolCategory.id),
                        tuple.get(idolCategory.artist),
                        tuple.get(idolCategory.artistImg),
                        tuple.get(idolCategory.artistGenre),
                        tuple.get(idolCategory.artistType),
                       null
                ))
                .toList();

        return ResponseEntity.ok().body(resultList);
    }

    @Override
    @Transactional
    public ResponseEntity<String> followUser(String artist, String followUser, String username) {
        // 해당 카테고리 있는지 확인
        getIdolCategoryByArtist(artist);

        JoinIdol otherUser = checkJoinMemberByNickname(followUser, artist);
        JoinIdol user = checkJoinMemberByUsername(username, artist);

        Optional<ArtistFollow> isFollow = artistFollowRepository.findByFollowerNicknameAndFollowingNickname(user.getNickname(), otherUser.getNickname());

        if(isFollow.isPresent()){ // 이미 팔로우한 경우
            artistFollowRepository.delete(isFollow.get());

            return ResponseEntity.ok("팔로우 취소 성공");
        }else {
            ArtistFollow followMember = ArtistFollow.createFollowMember(user, otherUser);
            artistFollowRepository.save(followMember);

            return ResponseEntity.ok("팔로우 성공");
        }
    }

    private void getIdolCategoryByArtist(String artist){
        idolCategoryRepository.findByArtist(artist).orElseThrow(() -> new AppException(ErrorCode.IDOL_CATEGORY_NOT_FOUND, "아티스트가 존재하지않습니다"));
    }

    private JoinIdol checkJoinMemberByNickname(String nickname, String artist){
        return joinIdolRepository.findJoinIdolByNicknameAndIdolCategoryArtist(nickname, artist).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_JOIN_IDOL_CATEGORY, "찾는 회원은 존재하지않는 회원입니다."));
    }

    private JoinIdol checkJoinMemberByUsername(String username, String artist){
        return joinIdolRepository.findJoinIdolByMemberUsernameAndIdolCategoryArtist(username, artist).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_JOIN_IDOL_CATEGORY, "존재하지않는 회원입니다."));
    }
}
