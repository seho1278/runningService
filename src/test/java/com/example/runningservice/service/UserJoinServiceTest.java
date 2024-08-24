package com.example.runningservice.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.dto.join.JoinApplyDto;
import com.example.runningservice.dto.join.JoinApplyDto.DetailResponse;
import com.example.runningservice.dto.join.JoinApplyDto.Request;
import com.example.runningservice.dto.join.JoinApplyDto.SimpleResponse;
import com.example.runningservice.dto.UpdateJoinApplyDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.JwtUtil;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class UserJoinServiceTest {

    @InjectMocks
    private UserJoinService userJoinService;

    @Mock
    private JoinApplicationRepository joinApplicationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CrewRepository crewRepository;

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("승인없이 자동 가입")
    void saveJoinApply_whenJoinPossible_thenSuccess() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1996).gender(Gender.MALE).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName")
            .leaderRequired(false).build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
            joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder().userId(1L).message("test").build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());
        // then
        assertEquals(JoinStatus.APPROVED, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
        verify(crewMemberRepository, times(1)).save(any(CrewMemberEntity.class));
    }

    @Test
    @DisplayName("승인 필요 시 승인대기상태로 저장")
    void saveJoinApply_whenJoinPossible_thenSuccess_withPending() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1996).gender(Gender.MALE).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName")
            .leaderRequired(true).build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
            joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder().userId(1L).message("test").build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());

        // then
        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
    }

    @Test
    @DisplayName("나이제한 없음(성공)")
    void saveJoinApply_whenJoinPossible_NoAgeLimit_thenSuccess() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1996).gender(Gender.MALE)
            .genderVisibility(Visibility.PUBLIC).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName")
            .gender(Gender.MALE).leaderRequired(true).build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
            joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder().userId(1L).message("test").build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());

        // then
        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
    }

    @Test
    @DisplayName("나이제한 하한만 있음(성공)")
    void saveJoinApply_whenJoinPossible_OnlyMinAgeLimit_thenSuccess() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(2005).birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.MALE).genderVisibility(Visibility.PUBLIC).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName").minAge(20)
            .gender(Gender.MALE).leaderRequired(true).build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
            joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder().userId(1L).message("test").build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());

        // then
        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
    }

    @Test
    @DisplayName("나이제한 상한만 있음")
    void saveJoinApply_whenJoinPossible_OnlyMaxAgeLimit_thenSuccess() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1995).birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.MALE).genderVisibility(Visibility.PUBLIC).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName").maxAge(30)
            .gender(Gender.MALE).leaderRequired(true).build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
            joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder().userId(1L).message("test").build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());

        // then
        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
    }

    @Test
    @DisplayName("성별제한 없음")
    void saveJoinApply_whenJoinPossible_NoGenderLimit_thenSuccess() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1995).birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.FEMALE).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName").maxAge(30)
            .leaderRequired(true).build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
            joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder().userId(1L).message("test").build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());

        // then
        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
    }

    @Test
    @DisplayName("어떠한 제한도 없음")
    void saveJoinApply_whenJoinPossible_NoLimit_thenSuccess() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName")
            .leaderRequired(true).build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
            joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder().userId(1L).message("test").build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());

        // then
        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
    }

    @Test
    @DisplayName("성별 불일치(실패)")
    void saveJoinApply_whenJoinPossible_GenderLimit_Fail() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1995).birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.MALE).genderVisibility(Visibility.PUBLIC).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName").maxAge(30)
            .gender(Gender.FEMALE).leaderRequired(true).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(1L,
                Request.builder().userId(1L).message("test").build()));

        // then
        assertEquals(ErrorCode.GENDER_RESTRICTION_NOT_MET, exception.getErrorCode());
    }

    @Test
    @DisplayName("나이제한 미충족(실패)")
    void saveJoinApply_whenJoinPossible_AgeLimit_Fail() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1994).birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.FEMALE).genderVisibility(Visibility.PUBLIC).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName").maxAge(30)
            .gender(Gender.FEMALE).leaderRequired(true).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(1L,
                Request.builder().userId(1L).message("test").build()));

        // then
        assertEquals(ErrorCode.AGE_RESTRICTION_NOT_MET, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원나이 null(실패)")
    void saveJoinApply_whenJoinPossible_MemberAgeNull_Fail() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(null).birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.FEMALE).genderVisibility(Visibility.PUBLIC).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName").maxAge(30)
            .gender(Gender.FEMALE).leaderRequired(true).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(1L,
                Request.builder().userId(1L).message("test").build()));

        // then
        assertEquals(ErrorCode.AGE_REQUIRED, exception.getErrorCode());
    }

//    @Test
//    @DisplayName("회원나이 비공개(실패)")
//    void saveJoinApply_whenJoinPossible_MemberPrivate_Fail() {
//        // given
//        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
//            .nickName("testNickName").birthYear(1994).birthYearVisibility(Visibility.PRIVATE)
//            .gender(Gender.FEMALE).genderVisibility(Visibility.PUBLIC).build();
//        CrewEntity crewEntity = CrewEntity.builder().crewId(1L).crewName("testCrewName").maxAge(30)
//            .gender(Gender.FEMALE).leaderRequired(true).build(); // 필드들 초기화
//
//        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
//        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
//
//        // when
//        CustomException exception = assertThrows(CustomException.class,
//            () -> userJoinService.saveJoinApply(1L,
//                Request.builder().userId(1L).message("test").build()));
//
//        // then
//        assertEquals(ErrorCode.AGE_REQUIRED, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("회원나이 비공개 & 나이제한 없음(성공)")
//    void saveJoinApply_whenJoinPossible_MemberAgePrivate_Success() {
//        // given
//        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
//            .nickName("testNickName").birthYear(1994).birthYearVisibility(Visibility.PRIVATE)
//            .gender(Gender.FEMALE).build();
//        CrewEntity crewEntity = CrewEntity.builder().crewId(1L).crewName("testCrewName")
//            .leaderRequired(true).build(); // 필드들 초기화
//
//        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
//            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화
//
//        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
//        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
//        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
//            joinApplyEntity);
//
//        // when
//        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
//            Request.builder().userId(1L).message("test").build());
//
//        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
//        verify(joinApplicationRepository).save(captor.capture());
//
//        // then
//        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
//        assertEquals("testNickName", response.getNickname());
//        assertEquals("testCrewName", response.getCrewName());
//        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
//    }
    @Test
    @DisplayName("회원나이 비공개(실패)")
    void saveJoinApply_whenJoinPossible_MemberPrivate_Fail() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1994).birthYearVisibility(Visibility.PRIVATE)
            .gender(Gender.FEMALE).genderVisibility(Visibility.PUBLIC).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName").maxAge(30)
            .gender(Gender.FEMALE).leaderRequired(true).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(1L,
                Request.builder().userId(1L).message("test").build()));

        // then
        assertEquals(ErrorCode.AGE_REQUIRED, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원나이 비공개 & 나이제한 없음(성공)")
    void saveJoinApply_whenJoinPossible_MemberAgePrivate_Success() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1994).birthYearVisibility(Visibility.PRIVATE)
            .gender(Gender.FEMALE).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName")
            .leaderRequired(true).build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
            joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            Request.builder().userId(1L).message("test").build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());

        // then
        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
    }

    @Test
    @DisplayName("회원나이 null 성공(나이제한 없음)")
    void saveJoinApply_whenJoinPossible_MemberAgeNull_Success() {
        // given
        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(null).birthYearVisibility(Visibility.PRIVATE)
            .gender(Gender.FEMALE).genderVisibility(Visibility.PUBLIC).build();
        CrewEntity crewEntity = CrewEntity.builder().id(1L).crewName("testCrewName")
            .gender(Gender.FEMALE).leaderRequired(true).build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L).member(memberEntity)
            .crew(crewEntity).status(JoinStatus.PENDING).build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(
            joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            Request.builder().userId(1L).message("test").build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());

        // then
        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
    }

    @Test
    @DisplayName("신청리스트 조회 (성공)")
    void testGetJoinApplications_ValidToken_Success() {
        String token = "Bearer validToken";
        Long memberId = 1L;
        GetApplicantsRequestDto request = GetApplicantsRequestDto.builder().status(null)
            .pageable(PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")))).build();
        JoinApplyEntity entity = JoinApplyEntity.builder()
            .member(MemberEntity.builder().id(memberId).nickName("testNick").build())
            .crew(CrewEntity.builder().id(1L).crewName("testCrew").build()).build();

        when(jwtUtil.validateToken(memberId, "validToken")).thenReturn(true);
        Page<JoinApplyEntity> page = new PageImpl<>(Collections.singletonList(entity));

        when(joinApplicationRepository.findAllByMember_Id(1L, request.getPageable())).thenReturn(
            page);

        //when
        Page<SimpleResponse> result = userJoinService.getJoinApplications(token, memberId, request);

        //then
        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("testNick", result.getContent().get(0).getNickname());
        assertEquals("testCrew", result.getContent().get(0).getCrewName());
    }

    @Test
    @DisplayName("신청리스트 조회_기본 정렬 기준으로 조회 (성공)")
    void testGetJoinApplications_ValidToken_DefaultSorting_Success() {
        String token = "Bearer validToken";
        Long memberId = 1L;
        GetApplicantsRequestDto request = GetApplicantsRequestDto.builder().status(null)
            .pageable(PageRequest.of(0, 10)).build();
        JoinApplyEntity entity = JoinApplyEntity.builder()
            .member(MemberEntity.builder().id(memberId).nickName("testNick").build())
            .crew(CrewEntity.builder().id(1L).crewName("testCrew").build()).build();

        when(jwtUtil.validateToken(memberId, "validToken")).thenReturn(true);
        Page<JoinApplyEntity> page = new PageImpl<>(Collections.singletonList(entity));
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(joinApplicationRepository.findAllByMember_Id(eq(memberId),
            pageableCaptor.capture())).thenReturn(page);

        //when
        Page<SimpleResponse> result = userJoinService.getJoinApplications(token, memberId, request);

        //then
        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("testNick", result.getContent().get(0).getNickname());
        assertEquals("testCrew", result.getContent().get(0).getCrewName());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(10, capturedPageable.getPageSize());
        assertEquals(Sort.by(Sort.Order.desc("createdAt")), capturedPageable.getSort());
    }

    @Test
    @DisplayName("유효하지 않은 토큰(실패)")
    void testGetJoinApplications_InvalidToken_ThrowsException() {
        String token = "Bearer invalidToken";
        Long memberId = 1L;
        GetApplicantsRequestDto request = GetApplicantsRequestDto.builder().status(null)
            .pageable(null).build();

        when(jwtUtil.validateToken(memberId, "invalidToken")).thenCallRealMethod();
        when(jwtUtil.extractUserId("invalidToken")).thenReturn(2L);

        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.getJoinApplications(token, memberId, request));

        assertEquals(ErrorCode.UNAUTHORIZED_MY_APPLY_ACCESS, exception.getErrorCode());
    }

    @Test
    @DisplayName("status == null 일 때 모든 값 불러옴(성공)")
    void testGetJoinApplications_NoStatus_FetchAll() {
        String token = "Bearer validToken";
        Long memberId = 1L;
        GetApplicantsRequestDto request = GetApplicantsRequestDto.builder().status(null)
            .pageable(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("createdAt"))))
            .build();

        JoinApplyEntity entity1 = JoinApplyEntity.builder()
            .member(MemberEntity.builder().id(1L).build())
            .crew(CrewEntity.builder().id(1L).build()).status(JoinStatus.PENDING).build();

        JoinApplyEntity entity2 = JoinApplyEntity.builder()
            .member(MemberEntity.builder().id(1L).build())
            .crew(CrewEntity.builder().id(1L).build()).status(JoinStatus.APPROVED).build();

        when(jwtUtil.validateToken(memberId, "validToken")).thenReturn(true);
        Page<JoinApplyEntity> page = new PageImpl<>(List.of(entity1, entity2));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(joinApplicationRepository.findAllByMember_Id(eq(memberId),
            pageableCaptor.capture())).thenReturn(page);

        //when
        Page<SimpleResponse> result = userJoinService.getJoinApplications(token, memberId, request);

        //then
        assertNotNull(result);
        assertEquals(2, result.getSize());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(10, capturedPageable.getPageSize());
        assertEquals(Sort.by(Sort.Order.asc("createdAt")), capturedPageable.getSort());
    }

    @Test
    @DisplayName("status == PENDING 일 때 모든 값 불러옴(성공)")
    void testGetJoinApplications_PENDINGStatus_FetchAll() {
        // Given
        String token = "Bearer validToken";
        Long memberId = 1L;
        GetApplicantsRequestDto request = GetApplicantsRequestDto.builder().status(JoinStatus.PENDING)
            .pageable(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("createdAt")))).build();

        JoinApplyEntity entity1 = JoinApplyEntity.builder()
            .member(MemberEntity.builder().id(1L).build())
            .crew(CrewEntity.builder().id(1L).crewName("crew1").build())
            .status(JoinStatus.APPROVED)  // 이 데이터는 결과에 포함되지 않아야 함
            .build();

        JoinApplyEntity entity2 = JoinApplyEntity.builder()
            .member(MemberEntity.builder().id(1L).build())
            .crew(CrewEntity.builder().id(2L).crewName("crew2").build())
            .status(JoinStatus.PENDING)  // 이 데이터는 결과에 포함되어야 함
            .build();

        Page<JoinApplyEntity> page = new PageImpl<>(List.of(entity2));

        when(jwtUtil.validateToken(memberId, "validToken")).thenReturn(true);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(joinApplicationRepository.findAllByMember_IdAndStatus(eq(memberId),
            eq(JoinStatus.PENDING), pageableCaptor.capture())).thenReturn(page);

        // When
        Page<SimpleResponse> result = userJoinService.getJoinApplications(token, memberId, request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("crew2", result.getContent().get(0).getCrewName());

        // Pageable 검증
        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(10, capturedPageable.getPageSize());
        assertEquals(Sort.by(Sort.Order.asc("createdAt")), capturedPageable.getSort());
    }

    @Test
    @DisplayName("신청내역 상세조회 (성공)")
    void testGetJoinApplicationDetail_Success() {
        // Given
        String token = "Bearer test-token";
        Long userId = 1L;
        Long joinApplyId = 1L;

        when(jwtUtil.validateToken(userId, "test-token")).thenCallRealMethod();
        when(jwtUtil.extractUserId("test-token")).thenReturn(1L);

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(joinApplyId)
            .member(MemberEntity.builder().id(1L).nickName("testNick").build())
            .crew(CrewEntity.builder().id(1L).crewName("testCrew").build()).build();
        when(joinApplicationRepository.findByIdAndMember_Id(1L, 1L)).thenReturn(
            Optional.of(joinApplyEntity));

        // When
        DetailResponse result = userJoinService.getJoinApplicationDetail(token, userId,
            joinApplyId);

        // Then
        assertNotNull(result);
        assertEquals("testCrew", result.getCrewName());
        assertEquals("testNick", result.getNickname());
        verify(jwtUtil, times(1)).validateToken(userId, "test-token");
        verify(joinApplicationRepository, times(1)).findByIdAndMember_Id(joinApplyId, userId);
    }

    @Test
    @DisplayName("신청 - 이미 신청 내역 있음(실패)")
    void testGetJoinApplicationDetail_AlreadyApplied() {
        // Given
        Long userId = 1L;
        Long crewId = 2L;

        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1995).birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.FEMALE).build();
        CrewEntity crewEntity = CrewEntity.builder().id(2L).crewName("testCrewName").maxAge(30)
            .leaderRequired(true).build(); // 필드들 초기화

        JoinApplyDto.Request joinApplyDto = JoinApplyDto.Request.builder().userId(userId)
            .message("testMessage").build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(2L)).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.existsByMember_IdAndCrew_Id(userId, crewId)).thenReturn(
            true);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(crewId, joinApplyDto));

        //then
        assertEquals(ErrorCode.ALREADY_EXIST_JOIN_APPLY, exception.getErrorCode());
        verify(joinApplicationRepository, times(1)).existsByMember_IdAndCrew_Id(userId, crewId);
    }

    @Test
    @DisplayName("신청 - 이미 가입된 회원(실패)")
    void testGetJoinApplicationDetail_AlreadyCrewMember() {
        // Given
        Long userId = 1L;
        Long crewId = 2L;

        MemberEntity memberEntity = MemberEntity.builder().id(1L).email("testEmail")
            .nickName("testNickName").birthYear(1995).birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.FEMALE).build();
        CrewEntity crewEntity = CrewEntity.builder().id(2L).crewName("testCrewName").maxAge(30)
            .leaderRequired(true).build(); // 필드들 초기화

        JoinApplyDto.Request joinApplyDto = JoinApplyDto.Request.builder().userId(userId)
            .message("testMessage").build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(2L)).thenReturn(Optional.of(crewEntity));
        when(crewMemberRepository.existsByMember_Id(userId)).thenReturn(true);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(crewId, joinApplyDto));

        //then
        assertEquals(ErrorCode.ALREADY_CREWMEMBER, exception.getErrorCode());
        verify(crewMemberRepository, times(1)).existsByMember_Id(userId);
    }

    @Test
    @DisplayName("가입 신청 수정 - 성공 케이스")
    void updateJoinApply_Success() {
        // Given
        String token = "Bearer valid-token";
        Long crewId = 1L;
        Long userId = 1L;
        Long joinApplyId = 1L;
        String newMessage = "Updated message";

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(joinApplyId)
            .member(MemberEntity.builder().id(userId).build())
            .crew(CrewEntity.builder().id(crewId).build()).status(JoinStatus.PENDING)
            .message("Old message").build();

        UpdateJoinApplyDto updateJoinApplyDto = UpdateJoinApplyDto.builder()
            .joinApplyId(joinApplyId).message(newMessage).build();

        when(jwtUtil.extractUserId(token.substring("Bearer ".length()))).thenReturn(userId);
        when(joinApplicationRepository.findByIdAndStatus(joinApplyId,
            JoinStatus.PENDING)).thenReturn(Optional.ofNullable(joinApplyEntity));

        // When
        JoinApplyDto.DetailResponse response = userJoinService.updateJoinApply(token,
            updateJoinApplyDto);

        // Then
        assertEquals(newMessage, response.getApplyMessage());
    }

    @Test
    @DisplayName("신청 취소_유효한 토큰과 대기 상태의 신청이 존재 (성공)")
    void testRemoveJoinApply_ValidToken_ValidJoinApply_Success() {
        String token = "Bearer validToken";
        Long memberId = 1L;
        Long joinApplyId = 1L;

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(joinApplyId)
            .member(MemberEntity.builder().id(memberId).nickName("testNIck").build())
            .crew(CrewEntity.builder().id(1L).crewName("testCrew").build())
            .status(JoinStatus.PENDING).build();

        when(jwtUtil.extractUserId(token.substring("Bearer ".length()))).thenReturn(memberId);
        when(joinApplicationRepository.findByIdAndStatus(joinApplyId,
            JoinStatus.PENDING)).thenReturn(Optional.of(joinApplyEntity));

        //when
        userJoinService.removeJoinApply(token, joinApplyId);

        //then
        verify(joinApplicationRepository).delete(joinApplyEntity);
    }

    @Test
    @DisplayName("해당 신청이 대기 상태가 아닐 때")
    void testRemoveJoinApply_InvalidStatus_ExceptionThrown() {
        String token = "Bearer validToken";
        Long memberId = 1L;
        Long joinApplyId = 1L;

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(joinApplyId)
            .member(MemberEntity.builder().id(memberId).build())
            .crew(CrewEntity.builder().id(1L).crewName("testCrew").build())
            .status(JoinStatus.APPROVED) // 상태가 대기(PENDING)가 아님
            .build();

        when(joinApplicationRepository.findByIdAndStatus(joinApplyId,
            JoinStatus.PENDING)).thenReturn(Optional.empty());
        //when
        CustomException thrownException = assertThrows(CustomException.class,
            () -> userJoinService.removeJoinApply(token, joinApplyId));
        assertEquals(ErrorCode.NOT_FOUND_APPLY, thrownException.getErrorCode());
    }

    @Test
    @DisplayName("존재하지 않는 신청이 있을 때 예외가 발생한다")
    void testRemoveJoinApply_JoinApplyNotFound_ExceptionThrown() {
        String token = "Bearer validToken";
        Long memberId = 1L;
        Long joinApplyId = 1L;

        when(joinApplicationRepository.findByIdAndStatus(joinApplyId,
            JoinStatus.PENDING)).thenReturn(Optional.empty());

        //when
        CustomException thrownException = assertThrows(CustomException.class,
            () -> userJoinService.removeJoinApply(token, joinApplyId));
        //
        assertEquals(ErrorCode.NOT_FOUND_APPLY, thrownException.getErrorCode());
    }

    @Test
    @DisplayName("유효하지 않은 토큰일 때 예외가 발생한다")
    void testRemoveJoinApply_InvalidToken_ExceptionThrown() {
        String token = "Bearer invalidToken";
        Long memberId = 1L;
        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder().id(1L)
            .member(MemberEntity.builder().id(2L).build())
            .crew(CrewEntity.builder().id(1L).crewName("testCrew").build())
            .status(JoinStatus.APPROVED) // 상태가 대기(PENDING)가 아님
            .build();

        when(joinApplicationRepository.findByIdAndStatus(1L, JoinStatus.PENDING)).thenReturn(
            Optional.of(joinApplyEntity));

        when(jwtUtil.extractUserId(token.substring("Bearer ".length()))).thenReturn(memberId);

        CustomException thrownException = assertThrows(CustomException.class,
            () -> userJoinService.removeJoinApply(token, 1L));
        assertEquals(ErrorCode.UNAUTHORIZED_MY_APPLY_ACCESS, thrownException.getErrorCode());
    }
}