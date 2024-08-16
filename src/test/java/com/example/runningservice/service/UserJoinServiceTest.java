package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.JoinApplyDto;
import com.example.runningservice.dto.JoinApplyDto.Request;
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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(1996)
            .gender(Gender.MALE)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .leaderRequired(false)
            .build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(joinApplyEntity);


        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder()
                .userId(1L)
                .message("test")
                .build());

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
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(1996)
            .gender(Gender.MALE)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .leaderRequired(true)
            .build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder()
                .userId(1L)
                .message("test")
                .build());

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
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(1996)
            .gender(Gender.MALE)
            .genderVisibility(Visibility.PUBLIC)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .gender(Gender.MALE)
            .leaderRequired(true)
            .build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder()
                .userId(1L)
                .message("test")
                .build());

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
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(2005)
            .birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.MALE)
            .genderVisibility(Visibility.PUBLIC)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .minAge(20)
            .gender(Gender.MALE)
            .leaderRequired(true)
            .build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder()
                .userId(1L)
                .message("test")
                .build());

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
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(1995)
            .birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.MALE)
            .genderVisibility(Visibility.PUBLIC)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .maxAge(30)
            .gender(Gender.MALE)
            .leaderRequired(true)
            .build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder()
                .userId(1L)
                .message("test")
                .build());

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
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(1995)
            .birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.FEMALE)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .maxAge(30)
            .leaderRequired(true)
            .build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder()
                .userId(1L)
                .message("test")
                .build());

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
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .leaderRequired(true)
            .build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            JoinApplyDto.Request.builder()
                .userId(1L)
                .message("test")
                .build());

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
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(1995)
            .birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.MALE)
            .genderVisibility(Visibility.PUBLIC)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .maxAge(30)
            .gender(Gender.FEMALE)
            .leaderRequired(true)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(1L,
                Request.builder()
                    .userId(1L)
                    .message("test")
                    .build()));

        // then
        assertEquals(ErrorCode.GENDER_RESTRICTION_NOT_MET, exception.getErrorCode());
    }

    @Test
    @DisplayName("나이제한 미충족(실패)")
    void saveJoinApply_whenJoinPossible_AgeLimit_Fail() {
        // given
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(1994)
            .birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.FEMALE)
            .genderVisibility(Visibility.PUBLIC)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .maxAge(30)
            .gender(Gender.FEMALE)
            .leaderRequired(true)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(1L,
                Request.builder()
                    .userId(1L)
                    .message("test")
                    .build()));

        // then
        assertEquals(ErrorCode.AGE_RESTRICTION_NOT_MET, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원나이 null(실패)")
    void saveJoinApply_whenJoinPossible_MemberAgeNull_Fail() {
        // given
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(null)
            .birthYearVisibility(Visibility.PUBLIC)
            .gender(Gender.FEMALE)
            .genderVisibility(Visibility.PUBLIC)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .maxAge(30)
            .gender(Gender.FEMALE)
            .leaderRequired(true)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(1L,
                Request.builder()
                    .userId(1L)
                    .message("test")
                    .build()));

        // then
        assertEquals(ErrorCode.AGE_REQUIRED, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원나이 비공개(실패)")
    void saveJoinApply_whenJoinPossible_MemberPrivate_Fail() {
        // given
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(1994)
            .birthYearVisibility(Visibility.PRIVATE)
            .gender(Gender.FEMALE)
            .genderVisibility(Visibility.PUBLIC)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .maxAge(30)
            .gender(Gender.FEMALE)
            .leaderRequired(true)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> userJoinService.saveJoinApply(1L,
                Request.builder()
                    .userId(1L)
                    .message("test")
                    .build()));

        // then
        assertEquals(ErrorCode.AGE_REQUIRED, exception.getErrorCode());
    }
    @Test
    @DisplayName("회원나이 비공개 & 나이제한 없음(성공)")
    void saveJoinApply_whenJoinPossible_MemberAgePrivate_Success() {
        // given
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(1994)
            .birthYearVisibility(Visibility.PRIVATE)
            .gender(Gender.FEMALE)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .leaderRequired(true)
            .build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            Request.builder()
                .userId(1L)
                .message("test")
                .build());

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
        MemberEntity memberEntity = MemberEntity.builder()
            .id(1L)
            .email("testEmail")
            .nickName("testNickName")
            .birthYear(null)
            .birthYearVisibility(Visibility.PRIVATE)
            .gender(Gender.FEMALE)
            .genderVisibility(Visibility.PUBLIC)
            .build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(1L)
            .crewName("testCrewName")
            .gender(Gender.FEMALE)
            .leaderRequired(true)
            .build(); // 필드들 초기화

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(1L)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build(); // 필드들 초기화

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));
        when(crewRepository.findById(anyLong())).thenReturn(Optional.of(crewEntity));
        when(joinApplicationRepository.save(any(JoinApplyEntity.class))).thenReturn(joinApplyEntity);

        // when
        JoinApplyDto.DetailResponse response = userJoinService.saveJoinApply(1L,
            Request.builder()
                .userId(1L)
                .message("test")
                .build());

        ArgumentCaptor<JoinApplyEntity> captor = forClass(JoinApplyEntity.class);
        verify(joinApplicationRepository).save(captor.capture());

        // then
        assertEquals(JoinStatus.PENDING, captor.getValue().getStatus());
        assertEquals("testNickName", response.getNickname());
        assertEquals("testCrewName", response.getCrewName());
        verify(joinApplicationRepository, times(1)).save(any(JoinApplyEntity.class));
    }
}