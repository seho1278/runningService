package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.crewMember.CrewMemberResponseDto;
import com.example.runningservice.dto.join.CrewApplicantDetailResponseDto;
import com.example.runningservice.dto.join.CrewApplicantResponseDto;
import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.PageUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
class CrewApplicantServiceTest {

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @Mock
    private JoinApplicationRepository joinApplicationRepository;

    @Mock
    private PageUtil pageUtil;

    @Mock
    private AESUtil aesUtil;

    @InjectMocks
    private CrewApplicantService crewApplicantService;

    @Test
    void getAllJoinApplications_Success() {
        // given
        Long crewId = 1L;
        GetApplicantsRequestDto request = GetApplicantsRequestDto.builder()
            .status(JoinStatus.PENDING)
            .pageable(PageRequest.of(0, 5))
            .build();

        JoinApplyEntity joinApplyEntity1 = JoinApplyEntity.builder()
            .member(MemberEntity.builder()
                .nickName("testNick1")
                .build())
            .crew(CrewEntity.builder()
                .crewName("testCrew1")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 0))
                .build())
            .build();
        JoinApplyEntity joinApplyEntity2 = JoinApplyEntity.builder()
            .member(MemberEntity.builder()
                .nickName("testNick2")
                .build())
            .crew(CrewEntity.builder()
                .crewName("testCrew2")
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
                .build())
            .build();
        Page<JoinApplyEntity> page = new PageImpl<>(List.of(joinApplyEntity1, joinApplyEntity2),
            request.getPageable(),
            2);

        Pageable sortedPageable = PageUtil.getSortedPageable(request.getPageable(), "createdAt", Direction.ASC, 0,
                10);
        when(joinApplicationRepository.findAllByCrew_IdAndStatus(eq(crewId),
            eq(JoinStatus.PENDING), eq(sortedPageable)))
            .thenReturn(page);

        // when
        Page<CrewApplicantResponseDto> result = crewApplicantService.getAllJoinApplications(crewId,
            request);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("testNick1", result.getContent().get(0).getNickName());
    }

    @Test
    void getJoinApplicationDetail_Success() {
        // given
        Long crewId = 1L;
        Long joinApplyId = 2L;

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(joinApplyId)
            .member(MemberEntity.builder().nickName("testNick").build())
            .crew(CrewEntity.builder().crewName("testCrew").build())
            .createdAt(LocalDateTime.now())
            .build();

        when(joinApplicationRepository.findByIdAndCrew_Id(joinApplyId, crewId))
            .thenReturn(Optional.of(joinApplyEntity));

        // when
        CrewApplicantDetailResponseDto result = crewApplicantService.getJoinApplicationDetail(
            crewId, joinApplyId);

        // then
        assertNotNull(result);
        assertEquals("testNick", result.getNickName());
    }

    @Test
    void approveJoinApplication_Success() {
        // given
        Long joinApplyId = 1L;

        MemberEntity memberEntity = MemberEntity.builder()
            .nickName("testNick")
            .profileImageUrl("testImageUrl")
            .nameVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
            .birthYearVisibility(Visibility.PUBLIC)
            .phoneNumberVisibility(Visibility.PUBLIC)
            .phoneNumber("encryptedNumber")
            .name("testName")
            .build();

        CrewEntity crewEntity = CrewEntity.builder()
            .crewName("testCrew")
            .build();

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(joinApplyId)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build();

        CrewMemberEntity newCrewMember = CrewMemberEntity.of(memberEntity, crewEntity);

        when(joinApplicationRepository.findByIdAndStatus(joinApplyId, JoinStatus.PENDING))
            .thenReturn(Optional.of(joinApplyEntity));

        when(crewMemberRepository.save(any(CrewMemberEntity.class)))
            .thenReturn(newCrewMember);

        when(aesUtil.decrypt(memberEntity.getPhoneNumber())).thenReturn("decryptedNumber");

        // when
        CrewMemberResponseDto result = crewApplicantService.approveJoinApplication(joinApplyId);

        // then
        assertNotNull(result);
        assertEquals(crewEntity.getCrewName(), result.getCrewName());
        assertEquals(CrewRole.MEMBER, result.getRole());
        assertEquals(memberEntity.getNickName(), result.getMemberNickName());
        assertEquals(memberEntity.getName(), result.getName());
        assertEquals(memberEntity.getProfileImageUrl(), result.getMemberProfileImage());
        assertEquals(null, result.getMemberGender());
        assertEquals("decryptedNumber", result.getPhoneNumber());
        assertEquals(JoinStatus.APPROVED, joinApplyEntity.getStatus());

        verify(joinApplicationRepository).findByIdAndStatus(joinApplyId, JoinStatus.PENDING);
        verify(crewMemberRepository).save(any(CrewMemberEntity.class));
    }

    @Test
    void rejectJoinApplication_Success() {
        // given
        Long joinApplyId = 1L;

        MemberEntity memberEntity = MemberEntity.builder()
            .nickName("testNick")
            .profileImageUrl("testImageUrl")
            .email("testEmail")
            .nameVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
            .birthYearVisibility(Visibility.PUBLIC)
            .phoneNumberVisibility(Visibility.PUBLIC)
            .phoneNumber("encryptedNumber")
            .name("testName")
            .build();

        CrewEntity crewEntity = CrewEntity.builder()
            .crewName("testCrew")
            .build();

        JoinApplyEntity joinApplyEntity = JoinApplyEntity.builder()
            .id(joinApplyId)
            .member(memberEntity)
            .crew(crewEntity)
            .status(JoinStatus.PENDING)
            .build();


        when(joinApplicationRepository.findByIdAndStatus(joinApplyId, JoinStatus.PENDING))
            .thenReturn(Optional.of(joinApplyEntity));

        // when
        String result = crewApplicantService.rejectJoinApplication(joinApplyId);

        // then
        assertNotNull(result);
        assertEquals("testEmail님의 가입신청이 거부되었습니다.", result);
        assertEquals(JoinStatus.REJECTED, joinApplyEntity.getStatus());

        verify(joinApplicationRepository).findByIdAndStatus(joinApplyId, JoinStatus.PENDING);
    }
}