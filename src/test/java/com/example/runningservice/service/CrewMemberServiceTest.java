package com.example.runningservice.service;

import static com.example.runningservice.entity.QCrewMemberEntity.crewMemberEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.crewMember.ChangeCrewRoleRequestDto;
import com.example.runningservice.dto.crewMember.ChangedLeaderResponseDto;
import com.example.runningservice.dto.crewMember.GetCrewMemberRequestDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberBlackListEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.QCrewMemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewMemberBlackListRepository;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.repository.chat.ChatJoinRepository;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.PageUtil;
import com.example.runningservice.util.QueryDslUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class CrewMemberServiceTest {

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @Mock
    private JoinApplicationRepository joinApplicationRepository;

    @Mock
    private CrewMemberBlackListRepository crewMemberBlackListRepository;

    @Mock
    private ChatJoinRepository chatJoinRepository;

    @Mock
    private JPAQueryFactory queryFactory;

    @Mock
    private PageUtil pageUtil;

    @Mock
    private AESUtil aesUtil;

    @InjectMocks
    private CrewMemberService crewMemberService;

    private BooleanExpression crewIdEq(Long crewId) {
        return crewId != null ? crewMemberEntity.crew.id.eq(crewId) : null;
    }

    private BooleanExpression genderEq(Gender gender) {
        return gender != null ? crewMemberEntity.member.gender.eq(gender) : null;
    }

    private BooleanExpression roleEq(CrewRole crewRole) {
        return crewRole != null ? crewMemberEntity.role.eq(crewRole) : null;
    }

    private BooleanExpression birthYearGoe(Integer minAge) {
        if (minAge == null) {
            return null;
        }
        int minBirthYear = LocalDate.now().getYear() - minAge + 1; //한국나이
        return crewMemberEntity.member.birthYear.loe(minBirthYear);
    }

    private BooleanExpression birthYearLoe(Integer maxAge) {
        if (maxAge == null) {
            return null;
        }
        int maxBirthYear = LocalDate.now().getYear() - maxAge + 1; //한국나이
        return crewMemberEntity.member.birthYear.goe(maxBirthYear);
    }

    @Test
    void testGetCrewMembers() {
        // Given
        Long crewId = 1L;
        GetCrewMemberRequestDto.Filter filter = GetCrewMemberRequestDto.Filter.builder()
            .crewRole(CrewRole.MEMBER)
            .build();

        Pageable pageable = PageRequest.of(0, 10);

        CrewMemberEntity crewMember1 = CrewMemberEntity.builder()
            .id(1L)
            .member(MemberEntity.builder().nickName("nick1").build())
            .crew(CrewEntity.builder().id(1L).crewName("crew1").build())
            .role(CrewRole.MEMBER)
            .joinedAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .build();

        CrewMemberEntity crewMember2 = CrewMemberEntity.builder()
            .id(1L)
            .member(MemberEntity.builder().nickName("nick2").build())
            .crew(CrewEntity.builder().id(1L).crewName("crew1").build())
            .role(CrewRole.LEADER)
            .joinedAt(LocalDateTime.of(2024, 1, 1, 1, 2))
            .build();

        CrewMemberEntity crewMember3 = CrewMemberEntity.builder()
            .id(1L)
            .member(MemberEntity.builder().nickName("nick3").build())
            .crew(CrewEntity.builder().id(1L).crewName("crew1").build())
            .role(CrewRole.STAFF)
            .joinedAt(LocalDateTime.of(2024, 1, 1, 1, 3))
            .build();

        List<CrewMemberEntity> crewMembers = List.of(crewMember1, crewMember2, crewMember3);

        Page<CrewMemberEntity> expectedPage = new PageImpl<>(crewMembers, pageable, 3);

        String defaultSortBy = "joinedAt";
        int defaultPageNumber = 0;
        int defaultPageSize = 10;
        Direction defaultSortDirection = Direction.ASC;
        Pageable sortedPageable = PageUtil.getSortedPageable(pageable, defaultSortBy,
            defaultSortDirection,
            defaultPageNumber, defaultPageSize);
        QCrewMemberEntity crewMemberEntity = QCrewMemberEntity.crewMemberEntity;

        // Mock the JPAQuery object
        JPAQuery<CrewMemberEntity> jpaQuery = (JPAQuery<CrewMemberEntity>) mock(JPAQuery.class);

        when(queryFactory.selectFrom(crewMemberEntity)).thenReturn(jpaQuery);

        when(jpaQuery.where(
            crewIdEq(crewId),
            genderEq(filter.getGender()),
            roleEq(filter.getCrewRole()),
            birthYearGoe(filter.getMinAge()),
            birthYearLoe(filter.getMaxAge()))
        ).thenReturn(jpaQuery);

        when(jpaQuery.orderBy(QueryDslUtil.getAllOrderSpecifiers(sortedPageable, "crewMemberEntity")
            .toArray(new OrderSpecifier[0]))).thenReturn(jpaQuery);

        when(jpaQuery.offset(sortedPageable.getOffset())).thenReturn(jpaQuery);
        when(jpaQuery.limit(sortedPageable.getPageSize())).thenReturn(jpaQuery);

        when(queryFactory.selectFrom(crewMemberEntity)
            .where(
                crewIdEq(crewId),
                genderEq(filter.getGender()),
                roleEq(filter.getCrewRole()),
                birthYearGoe(filter.getMinAge()),
                birthYearLoe(filter.getMaxAge())
            )
            .orderBy(QueryDslUtil.getAllOrderSpecifiers(sortedPageable, "crewMemberEntity")
                .toArray(new OrderSpecifier[0]))
            .offset(sortedPageable.getOffset())
            .limit(sortedPageable.getPageSize())
            .fetch()).thenReturn(crewMembers);

        JPAQuery<Long> countQuery = mock(JPAQuery.class);

        when(queryFactory.select(crewMemberEntity.count())).thenReturn(countQuery);
        when(countQuery.from(crewMemberEntity)).thenReturn(countQuery);
        when(countQuery.where(
            crewIdEq(crewId),
            genderEq(filter.getGender()),
            roleEq(filter.getCrewRole()),
            birthYearGoe(filter.getMinAge()),
            birthYearLoe(filter.getMaxAge())
        )).thenReturn(countQuery);

        when(queryFactory.select(crewMemberEntity.count())
            .from(crewMemberEntity)
            .where(crewIdEq(crewId),
                genderEq(filter.getGender()),
                roleEq(filter.getCrewRole()),
                birthYearGoe(filter.getMinAge()),
                birthYearLoe(filter.getMaxAge()))
            .fetchOne()).thenReturn(3L);

        //when
        Page<CrewMemberEntity> result = crewMemberService.getCrewMembers(crewId, filter, pageable);

        //then
        assertEquals(3, result.getTotalElements());
        assertEquals(crewMembers, result.getContent());
        assertEquals("nick1", result.getContent().get(0).getMember().getNickName());
    }

    @Test
    void testGetCrewMember_Success() {
        // given
        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .crew(CrewEntity.builder().id(1L).crewName("crew1").build())
            .member(MemberEntity.builder().nickName("nick1").name("name1").nameVisibility(
                Visibility.PRIVATE).build())
            .role(CrewRole.MEMBER)
            .build();

        when(crewMemberRepository.findById(crewMember.getId())).thenReturn(Optional.of(crewMember));

        // when
        CrewMemberEntity result = crewMemberService.getCrewMember(crewMember.getId());

        // then
        assertNotNull(result);
        assertEquals(crewMember.getId(), result.getId());
        assertEquals("nick1", result.getMember().getNickName());
    }

    @Test
    void testGetCrewMember_NotFound() {
        // given
        Long invalidId = 2L;
        when(crewMemberRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            crewMemberService.getCrewMember(invalidId);
        });

        assertEquals(ErrorCode.NOT_FOUND_CREW_MEMBER, exception.getErrorCode());
    }

    @Test
    void testLeaveCrew_Success() {
        // given
        Long crewId = 1L;
        Long memberId = 2L;
        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .crew(CrewEntity.builder().id(1L).crewName("crew1").build())
            .member(
                MemberEntity.builder().id(memberId).nickName("nick1").name("name1").nameVisibility(
                    Visibility.PRIVATE).build())
            .role(CrewRole.MEMBER)
            .build();

        JoinApplyEntity joinEntity = JoinApplyEntity.builder()
            .crew(CrewEntity.builder().id(1L).crewName("crew1").build())
            .member(MemberEntity.builder().id(2L).nickName("nick1").email("email1").build())
            .status(JoinStatus.APPROVED)
            .build();

        when(crewMemberRepository.findByCrew_IdAndMember_Id(crewId, memberId))
            .thenReturn(Optional.of(crewMember));
        doNothing().when(crewMemberRepository).delete(crewMember);
        when(
            joinApplicationRepository.findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(memberId,
                crewId))
            .thenReturn(Optional.of(joinEntity));

        // when
        crewMemberService.leaveCrew(crewId, memberId);

        // Assert
        assertEquals(JoinStatus.WITHDRAWN, joinEntity.getStatus());
        verify(crewMemberRepository, times(1)).findByCrew_IdAndMember_Id(crewId, memberId);
        verify(crewMemberRepository, times(1)).delete(crewMember);
        verify(joinApplicationRepository,
            times(1)).findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(memberId, crewId);
        verify(chatJoinRepository, times(1)).deleteAllByMemberIdAndCrewId(memberId, crewId);
    }

    @Test
    @DisplayName("잘못된 crewId & memberId")
    void testLeaveCrew_CrewMemberNotFound() {
        // given
        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .crew(CrewEntity.builder().id(1L).crewName("crew1").build())
            .member(MemberEntity.builder().nickName("nick1").name("name1").nameVisibility(
                Visibility.PRIVATE).build())
            .role(CrewRole.MEMBER)
            .build();

        JoinApplyEntity joinEntity = JoinApplyEntity.builder()
            .crew(CrewEntity.builder().id(1L).crewName("crew1").build())
            .member(MemberEntity.builder().id(2L).nickName("nick1").email("email1").build())
            .status(JoinStatus.APPROVED)
            .build();

        when(crewMemberRepository.findByCrew_IdAndMember_Id(2L, 1L))
            .thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            crewMemberService.leaveCrew(2L, 1L);
        });

        assertEquals(ErrorCode.NOT_FOUND_CREW_MEMBER, exception.getErrorCode());
        verify(crewMemberRepository, times(1)).findByCrew_IdAndMember_Id(2L, 1L);
        verify(crewMemberRepository, never()).delete(crewMember);
        verify(joinApplicationRepository,
            never()).findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(2L, 1L);
    }

    @Test
    void testLeaveCrew_JoinApplyNotFound() {
        // given
        Long crewId = 1L;
        Long memberId = 2L;
        Long JoinID = 3L;

        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .crew(CrewEntity.builder().id(crewId).crewName("crew1").build())
            .member(
                MemberEntity.builder().id(memberId).nickName("nick1").name("name1").nameVisibility(
                    Visibility.PRIVATE).build())
            .role(CrewRole.MEMBER)
            .build();

        when(crewMemberRepository.findByCrew_IdAndMember_Id(crewId, memberId))
            .thenReturn(Optional.of(crewMember));
        when(
            joinApplicationRepository.findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(memberId,
                crewId))
            .thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            crewMemberService.leaveCrew(crewId, memberId);
        });

        assertEquals(ErrorCode.NOT_FOUND_APPLY, exception.getErrorCode());
        verify(crewMemberRepository, times(1)).findByCrew_IdAndMember_Id(crewId, memberId);
        verify(crewMemberRepository, never()).delete(crewMember);
        verify(joinApplicationRepository,
            times(1)).findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(memberId, crewId);
    }

    @Test
    @DisplayName("권한 변경 성공")
    void testChangeRole_Success() {
        // given
        Long crewMemberId = 1L;
        CrewRole newRole = CrewRole.STAFF;
        Long crewId = 2L;
        Long memberId = 3L;
        ChangeCrewRoleRequestDto requestDto = ChangeCrewRoleRequestDto.builder()
            .crewMemberId(crewMemberId)
            .newRole(newRole)
            .build();

        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .crew(CrewEntity.builder().id(crewId).crewName("crew1").build())
            .member(
                MemberEntity.builder().id(memberId).nickName("nick1").name("name1").build())
            .role(CrewRole.MEMBER)
            .build();

        when(crewMemberRepository.findById(crewMemberId)).thenReturn(Optional.of(crewMember));

        // when
        CrewMemberEntity result = crewMemberService.changeRole(requestDto);

        // then
        assertNotNull(result);
        assertEquals(newRole, result.getRole());

        verify(crewMemberRepository, times(1)).findById(crewMemberId);
    }

    @Test
    @DisplayName("권한변경 실패_크루멤버를 찾을 수 없음")
    void testChangeRole_CrewMemberNotFound() {
        // given
        Long crewMemberId = 1L;
        CrewRole newRole = CrewRole.STAFF;
        Long crewId = 2L;
        Long memberId = 3L;
        ChangeCrewRoleRequestDto requestDto = ChangeCrewRoleRequestDto.builder()
            .crewMemberId(crewMemberId)
            .newRole(newRole)
            .build();

        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .crew(CrewEntity.builder().id(crewId).crewName("crew1").build())
            .member(
                MemberEntity.builder().id(memberId).nickName("nick1").name("name1").build())
            .role(CrewRole.MEMBER)
            .build();

        when(crewMemberRepository.findById(crewMemberId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> crewMemberService.changeRole(requestDto));

        // then
        assertEquals(ErrorCode.NOT_FOUND_CREW_MEMBER, exception.getErrorCode());
        assertEquals(null, crewMember.getStatus());
    }


    @Test
    @DisplayName("리더권한 위임 성공")
    void testTransferLeaderRole_Success() {
        // given
        CrewMemberEntity oldLeader = CrewMemberEntity.builder()
            .id(1L)
            .role(CrewRole.LEADER)
            .member(MemberEntity.builder().id(1L).nickName("OldLeader").build())
            .build();

        CrewMemberEntity newLeader = CrewMemberEntity.builder()
            .id(2L)
            .role(CrewRole.MEMBER)
            .member(MemberEntity.builder().id(2L).nickName("NewLeader").build())
            .build();

        Long userId = 1L;
        Long crewId = 1L;
        Long crewMemberId = 2L;

        when(crewMemberRepository.findById(crewMemberId)).thenReturn(Optional.of(newLeader));
        when(crewMemberRepository.findByMember_IdAndCrew_Id(userId, crewId)).thenReturn(
            Optional.of(oldLeader));

        // when
        ChangedLeaderResponseDto result = crewMemberService.transferLeaderRole(userId, crewId,
            crewMemberId);

        // then
        assertNotNull(result);
        assertEquals("OldLeader", result.getOldLeaderNickName());
        assertEquals(CrewRole.MEMBER, result.getOldLeaderRole());
        assertEquals("NewLeader", result.getNewLeaderNickName());
        assertEquals(CrewRole.LEADER, result.getNewLeaderRole());
        verify(crewMemberRepository, times(1)).findById(crewMemberId);
        verify(crewMemberRepository, times(1)).findByMember_IdAndCrew_Id(userId, crewId);

        // Verify that roles were changed
        assertEquals(CrewRole.MEMBER, oldLeader.getRole());
        assertEquals(CrewRole.LEADER, newLeader.getRole());
    }

    @Test
    @DisplayName("newLeader로 지정할 크루원을 찾을 수 없음")
    void testTransferLeaderRole_NewLeaderNotFound() {
        // given
        CrewMemberEntity oldLeader = CrewMemberEntity.builder()
            .id(1L)
            .role(CrewRole.LEADER)
            .member(MemberEntity.builder().id(1L).nickName("OldLeader").build())
            .build();

        CrewMemberEntity newLeader = CrewMemberEntity.builder()
            .id(2L)
            .role(CrewRole.MEMBER)
            .member(MemberEntity.builder().id(2L).nickName("NewLeader").build())
            .build();

        Long userId = 1L;
        Long crewId = 1L;
        Long crewMemberId = 2L;

        when(crewMemberRepository.findById(crewMemberId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            crewMemberService.transferLeaderRole(userId, crewId, crewMemberId);
        });

        // then
        assertEquals(ErrorCode.NOT_FOUND_CREW_MEMBER, exception.getErrorCode());
        verify(crewMemberRepository, times(1)).findById(crewMemberId);
        verify(crewMemberRepository, never()).findByMember_IdAndCrew_Id(anyLong(), anyLong());
    }

    @Test
    @DisplayName("oldLeader를 찾을 수 없음")
    void testTransferLeaderRole_OldLeaderNotFound() {
        // given
        CrewMemberEntity oldLeader = CrewMemberEntity.builder()
            .id(1L)
            .role(CrewRole.LEADER)
            .member(MemberEntity.builder().id(1L).nickName("OldLeader").build())
            .build();

        CrewMemberEntity newLeader = CrewMemberEntity.builder()
            .id(2L)
            .role(CrewRole.MEMBER)
            .member(MemberEntity.builder().id(2L).nickName("NewLeader").build())
            .build();

        Long userId = 1L;
        Long crewId = 1L;
        Long crewMemberId = 2L;

        when(crewMemberRepository.findById(crewMemberId)).thenReturn(Optional.of(newLeader));
        when(crewMemberRepository.findByMember_IdAndCrew_Id(userId, crewId)).thenReturn(
            Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            crewMemberService.transferLeaderRole(userId, crewId, crewMemberId);
        });

        //then
        assertEquals(ErrorCode.NOT_FOUND_CREW_MEMBER, exception.getErrorCode());

        verify(crewMemberRepository, times(1)).findById(crewMemberId);
        verify(crewMemberRepository, times(1)).findByMember_IdAndCrew_Id(userId, crewId);

        // not changed
        assertEquals(CrewRole.LEADER, oldLeader.getRole());
        assertEquals(CrewRole.MEMBER, newLeader.getRole());
    }

    @Test
    void testRemoveCrewMember_Success() {
        // given
        MemberEntity member = MemberEntity.builder()
            .id(2L)
            .email("test@example.com")
            .nickName("testNick")
            .build();

        CrewEntity crew = CrewEntity.builder()
            .id(1L)
            .crewName("TestCrew")
            .build();

        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .id(3L)
            .member(member)
            .crew(crew)
            .role(CrewRole.MEMBER)
            .build();

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(4L)
            .member(member)
            .crew(crew)
            .status(JoinStatus.APPROVED)
            .build();
        Long crewId = 1L;
        Long crewMemberId = 3L;

        CrewMemberBlackListEntity blackList = CrewMemberBlackListEntity.builder()
            .id(1L)
            .crew(crew)
            .member(member)
            .build();

        when(crewMemberRepository.findById(crewMemberId)).thenReturn(Optional.of(crewMember));
        when(joinApplicationRepository.findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(
            crewMember.getMember().getId(), crewId))
            .thenReturn(Optional.of(joinApplyEntity));
        when(crewMemberBlackListRepository.save(any(CrewMemberBlackListEntity.class))).thenReturn(
            blackList);

        // when
        CrewMemberBlackListEntity result = crewMemberService.removeCrewMember(crewId,
            crewMemberId);

        // then
        assertEquals(JoinStatus.FORCE_WITHDRAWN, joinApplyEntity.getStatus());
        assertEquals(1L, result.getId());
        verify(crewMemberRepository, times(1)).findById(crewMemberId);
        verify(joinApplicationRepository, times(1))
            .findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(crewMember.getMember().getId(),
                crewId);
        verify(crewMemberRepository, times(1)).delete(crewMember);
        verify(chatJoinRepository, times(1)).deleteAllByMemberIdAndCrewId(
            crewMember.getMember().getId(), crewId);
        verify(crewMemberBlackListRepository, times(1)).save(any(CrewMemberBlackListEntity.class));
    }

    @Test
    @DisplayName("퇴장시킬 크루원을 찾을 수 없음")
    void testRemoveCrewMember_CrewMemberNotFound() {
        // given
        Long crewId = 1L;
        Long crewMemberId = 3L;

        when(crewMemberRepository.findById(crewMemberId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            crewMemberService.removeCrewMember(crewId, crewMemberId);
        });

        // then
        assertEquals(ErrorCode.NOT_FOUND_CREW_MEMBER, exception.getErrorCode());

        verify(crewMemberRepository, times(1)).findById(crewMemberId);
        verify(joinApplicationRepository, never())
            .findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(anyLong(), anyLong());
        verify(crewMemberRepository, never()).delete(any(CrewMemberEntity.class));
    }

    @Test
    @DisplayName("퇴장시킬 크루원의 가입신청내역을 찾을 수 없음")
    void testRemoveCrewMember_JoinApplyNotFound() {
        // given
        MemberEntity member = MemberEntity.builder()
            .id(2L)
            .email("test@example.com")
            .nickName("testNick")
            .build();

        CrewEntity crew = CrewEntity.builder()
            .id(1L)
            .crewName("TestCrew")
            .build();

        CrewMemberEntity crewMember = CrewMemberEntity.builder()
            .id(3L)
            .member(member)
            .crew(crew)
            .role(CrewRole.MEMBER)
            .build();

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(4L)
            .member(member)
            .crew(crew)
            .status(JoinStatus.APPROVED)
            .build();
        Long crewId = 1L;
        Long crewMemberId = 3L;

        when(crewMemberRepository.findById(crewMemberId)).thenReturn(Optional.of(crewMember));
        when(joinApplicationRepository.findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(
            crewMember.getMember().getId(), crewId))
            .thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            crewMemberService.removeCrewMember(crewId, crewMemberId);
        });

        // then
        assertEquals(ErrorCode.NOT_FOUND_APPLY, exception.getErrorCode());
        assertEquals(JoinStatus.APPROVED, joinApplyEntity.getStatus());
        verify(crewMemberRepository, times(1)).findById(crewMemberId);
        verify(joinApplicationRepository, times(1))
            .findTopByMember_IdAndCrew_IdOrderByCreatedAtDesc(crewMember.getMember().getId(),
                crewId);
        verify(crewMemberRepository, never()).delete(any(CrewMemberEntity.class));
    }
}