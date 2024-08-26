package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.dto.crew.CrewBaseResponseDto;
import com.example.runningservice.dto.crew.CrewDetailResponseDto;
import com.example.runningservice.dto.crew.CrewFilterDto.CrewInfo;
import com.example.runningservice.dto.crew.CrewJoinStatusResponseDto;
import com.example.runningservice.dto.crew.CrewRequestDto.Create;
import com.example.runningservice.dto.crew.CrewRequestDto.Update;
import com.example.runningservice.dto.crew.CrewRoleResponseDto;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.CrewMemberEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.enums.OccupancyStatus;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.repository.ActivityRepository;
import com.example.runningservice.repository.CrewMemberBlackListRepository;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.JoinApplicationRepository;
import com.example.runningservice.repository.RegularRunMeetingRepository;
import com.example.runningservice.repository.chat.ChatRoomRepository;
import com.example.runningservice.repository.crew.CrewRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.service.chat.ChatRoomService;
import com.example.runningservice.util.S3FileUtil;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@ExtendWith(MockitoExtension.class)
class CrewServiceTest {

    @Mock
    private CrewRepository crewRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CrewMemberRepository crewMemberRepository;
    @Mock
    private S3FileUtil s3FileUtil;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private RegularRunMeetingRepository regularRunMeetingRepository;
    @Mock
    private CrewMemberBlackListRepository crewMemberBlackListRepository;
    @Mock
    private JoinApplicationRepository joinApplicationRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ActivityRepository activityRepository;
    @InjectMocks
    private CrewService crewService;

    @Test
    @DisplayName("크루 생성 - 이미지 O")
    void createCrew_WithImage() {
        // given
        Long leaderId = 1L;

        Create create = mock(Create.class);
        when(create.getLeaderId()).thenReturn(leaderId);
        when(create.getCrewImage()).thenReturn(new MockMultipartFile("file", new byte[]{1, 2, 3}));

        MemberEntity memberEntity = MemberEntity.builder().id(leaderId).build();
        CrewEntity crewEntity = CrewEntity.toEntity(create, memberEntity);

        given(memberRepository.findById(leaderId)).willReturn(Optional.of(memberEntity));
        given(crewRepository.save(any(CrewEntity.class))).willReturn(crewEntity);
        given(s3FileUtil.getImgUrl(anyString())).willReturn("http://example.com/image");
        doNothing().when(s3FileUtil).putObject(anyString(), any(MultipartFile.class));

        // when
        CrewBaseResponseDto response = crewService.createCrew(create);

        // then
        assertEquals(crewEntity.getId(), response.getCrewId());
        verify(s3FileUtil, times(1)).putObject("crew-" + crewEntity.getId(),
            create.getCrewImage());
        verify(s3FileUtil, times(1)).getImgUrl("crew-" + crewEntity.getId());
        verify(crewRepository, times(1)).save(any(CrewEntity.class));
    }

    @Test
    @DisplayName("크루 생성 - 이미지 X")
    public void createCrew_WithoutImage() {
        // given
        Long leaderId = 1L;

        Create create = mock(Create.class);
        when(create.getLeaderId()).thenReturn(leaderId);
        when(create.getCrewImage()).thenReturn(new MockMultipartFile("file", new byte[0]));

        MemberEntity memberEntity = new MemberEntity();
        CrewEntity crewEntity = CrewEntity.toEntity(create, memberEntity);

        given(memberRepository.findById(leaderId)).willReturn(Optional.of(memberEntity));
        given(crewRepository.save(any(CrewEntity.class))).willReturn(crewEntity);
        given(s3FileUtil.getImgUrl(anyString())).willReturn("http://example.com/default");

        // when
        CrewBaseResponseDto response = crewService.createCrew(create);

        // then
        assertEquals(crewEntity.getId(), response.getCrewId());
        // 이미지가 없는 경우 putObject는 호출되지 않아야 함
        verify(s3FileUtil, never()).putObject(anyString(), any(MultipartFile.class));
        verify(s3FileUtil, times(1)).getImgUrl("crew-default");
        verify(crewRepository, times(1)).save(any(CrewEntity.class));
    }

    @Test
    @DisplayName("크루 생성 (실패) - 사용자 없음")
    public void createCrew_UserNotFound() {
        // given
        Long leaderId = 1L;

        Create create = mock(Create.class);
        when(create.getLeaderId()).thenReturn(leaderId);

        given(memberRepository.findById(leaderId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () -> crewService.createCrew(create));
    }

    @Test
    @DisplayName("크루 수정 - 이미지 O")
    public void updateCrew_WithImage() {
        // given
        Long crewId = 1L;

        Update update = mock(Update.class);
        when(update.getCrewId()).thenReturn(crewId);
        when(update.getCrewImage()).thenReturn(new MockMultipartFile("file", new byte[]{1, 2, 3}));

        MemberEntity memberEntity = MemberEntity.builder().nickName("hi").build();
        CrewEntity crewEntity = CrewEntity.builder().id(crewId).leader(memberEntity).build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crewEntity));
        given(s3FileUtil.getImgUrl(anyString())).willReturn("http://example.com/image");
        doNothing().when(s3FileUtil).putObject(anyString(), any(MultipartFile.class));

        // when
        CrewBaseResponseDto response = crewService.updateCrew(update);

        // then
        assertEquals(crewEntity.getId(), response.getCrewId());
        assertEquals(crewEntity.getCrewImage(), "http://example.com/image");
        verify(s3FileUtil, times(1)).putObject("crew-" + crewEntity.getId(),
            update.getCrewImage());
        verify(s3FileUtil, times(1)).getImgUrl("crew-" + crewEntity.getId());
    }

    @Test
    @DisplayName("크루 수정 - 이미지 X")
    public void updateCrew_WithoutImage() {
        // given
        Long crewId = 1L;

        Update update = mock(Update.class);
        when(update.getCrewId()).thenReturn(crewId);
        when(update.getCrewImage()).thenReturn(new MockMultipartFile("file", new byte[0]));

        MemberEntity memberEntity = MemberEntity.builder().nickName("hi").build();
        CrewEntity crewEntity = CrewEntity.builder().id(crewId).leader(memberEntity).build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crewEntity));
        given(s3FileUtil.getImgUrl(anyString())).willReturn("http://example.com/default");

        // when
        CrewBaseResponseDto response = crewService.updateCrew(update);

        // then
        assertEquals(crewEntity.getId(), response.getCrewId());
        assertEquals(crewEntity.getCrewImage(), "http://example.com/default");
        verify(s3FileUtil, times(0)).putObject(any(), any());
        verify(s3FileUtil, times(1)).getImgUrl("crew-default");
    }

    @Test
    @DisplayName("크루 삭제 - 사용자 이미지")
    public void deleteCrew_WithImage() {
        // given
        Long crewId = 1L;

        MemberEntity memberEntity = MemberEntity.builder().nickName("hi").build();
        CrewEntity crewEntity = CrewEntity.builder()
            .id(crewId)
            .leader(memberEntity)
            .crewImage("a/b/crew-1")
            .build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crewEntity));

        // when
        CrewBaseResponseDto response = crewService.deleteCrew(crewId);

        // then
        assertEquals(crewEntity.getId(), response.getCrewId());
        verify(s3FileUtil, times(1)).deleteObject("crew-1");
    }

    @Test
    @DisplayName("크루 삭제 - 기본 이미지")
    public void deleteCrew_WithoutImage() {
        // given
        Long crewId = 1L;

        MemberEntity memberEntity = MemberEntity.builder().nickName("hi").build();
        CrewEntity crewEntity = CrewEntity.builder()
            .id(crewId)
            .leader(memberEntity)
            .crewImage("a/b/crew-default")
            .build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crewEntity));

        // when
        CrewBaseResponseDto response = crewService.deleteCrew(crewId);

        // then
        assertEquals(crewEntity.getId(), response.getCrewId());
        verify(s3FileUtil, times(0)).deleteObject(anyString());
    }

    @Test
    @DisplayName("크루 상세 조회_성공")
    public void getCrew() {
        Long crewId = 1L;
        int runningCount = 20;

        MemberEntity memberEntity = MemberEntity.builder().nickName("hi").build();
        CrewEntity crewEntity = CrewEntity.builder()
            .id(crewId)
            .leader(memberEntity)
            .leaderRequired(true)
            .runRecordOpen(true)
            .crewMember(List.of(CrewMemberEntity.builder().build()))
            .build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crewEntity));
        given(activityRepository.countByCrew_Id(crewId)).willReturn(runningCount);

        CrewDetailResponseDto detail = crewService.getCrew(crewId);

        assertEquals(detail.getLeader(), "hi");
        assertEquals(detail.getCrewOccupancy(), 1);
        assertEquals(detail.getRunningCount(), runningCount);
    }

    @Test
    @DisplayName("참가 중인 크루 리스트 조회")
    public void getParticipateCrewList() {
        Long loginId = 5L;
        MemberEntity member = MemberEntity.builder().id(1L).nickName("nick").build();
        CrewEntity crew1 = CrewEntity.builder().leader(member).id(loginId).build();
        CrewEntity crew2 = CrewEntity.builder().leader(member).id(10L).build();

        Pageable pageable = PageRequest.of(1, 2);
        List<CrewMemberEntity> crewMembers = List.of(CrewMemberEntity.builder()
                .crew(crew1)
                .member(member)
                .role(CrewRole.LEADER)
                .build(),
            CrewMemberEntity.builder()
                .crew(crew2)
                .member(member)
                .role(CrewRole.MEMBER)
                .build());
        Page<CrewMemberEntity> result = new PageImpl<>(crewMembers, pageable, crewMembers.size());

        given(crewMemberRepository.findByMember_IdOrderByJoinedAt(loginId, pageable))
            .willReturn(result);

        List<CrewRoleResponseDto> crewList = crewService.getParticipateCrewList(loginId, pageable);

        assertEquals(crewList.size(), 2);
        assertEquals(crewList.get(0).getCrewId(), 5);
        assertEquals(crewList.get(0).getRole(), CrewRole.LEADER);
        assertEquals(crewList.get(1).getCrewId(), 10);
        assertEquals(crewList.get(1).getRole(), CrewRole.MEMBER);
    }

    @Test
    @DisplayName("전체 크루 조회_로그인 사용자")
    public void getCrewList_Login() {
        Long loginId = 1L;
        CrewInfo crewInfo = CrewInfo.builder().occupancyStatus(OccupancyStatus.FULL).build();
        Pageable pageable = PageRequest.of(0, 2);
        MemberEntity member = MemberEntity.builder().id(loginId).nickName("nick").build();
        List<CrewMemberEntity> crewMemberList = List.of(
            CrewMemberEntity.builder().member(member).build());
        CrewEntity crew1 = CrewEntity.builder().id(5L).leader(member).crewMember(crewMemberList)
            .crewCapacity(1).build();
        CrewEntity crew2 = CrewEntity.builder().id(10L).leader(member).crewMember(crewMemberList)
            .crewCapacity(10).build();

        List<CrewEntity> crewMembers = List.of(crew1, crew2);
        Page<CrewEntity> result = new PageImpl<>(crewMembers, pageable, crewMembers.size());

        given(crewRepository.findFullCrewList(any(), any(), any(), any(), any(), any(), any()))
            .willReturn(result);

        List<CrewJoinStatusResponseDto> response = crewService.getCrewList(loginId, crewInfo,
            pageable);

        verify(crewRepository, times(1)).findFullCrewList(any(), any(), any(),
            any(), any(), any(), any());
        assertEquals(response.size(), 2);
        assertTrue(response.get(0).isJoined());
    }

    @Test
    @DisplayName("전체 크루 조회_비로그인 사용자")
    public void getCrewList_NoUser() {
        CrewInfo crewInfo = CrewInfo.builder().occupancyStatus(OccupancyStatus.FULL).build();
        Pageable pageable = PageRequest.of(0, 2);
        MemberEntity member = MemberEntity.builder().nickName("nick").build();
        List<CrewMemberEntity> crewMemberList = List.of(
            CrewMemberEntity.builder().member(member).build());
        CrewEntity crew1 = CrewEntity.builder().id(5L).leader(member).crewMember(crewMemberList)
            .crewCapacity(1).build();
        CrewEntity crew2 = CrewEntity.builder().id(10L).leader(member).crewMember(crewMemberList)
            .crewCapacity(10).build();

        List<CrewEntity> crewMembers = List.of(crew1, crew2);
        Page<CrewEntity> result = new PageImpl<>(crewMembers, pageable, crewMembers.size());

        given(crewRepository.findFullCrewList(any(), any(), any(), any(), any(), any(), any()))
            .willReturn(result);

        List<CrewJoinStatusResponseDto> response = crewService.getCrewList(null, crewInfo,
            pageable);

        verify(crewRepository, times(1)).findFullCrewList(any(), any(), any(),
            any(), any(), any(), any());
        assertEquals(response.size(), 2);
        assertFalse(response.get(0).isJoined());
    }
}