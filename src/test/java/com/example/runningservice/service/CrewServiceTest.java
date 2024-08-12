package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.runningservice.dto.crew.CrewRequestDto;
import com.example.runningservice.dto.crew.CrewRequestDto.Create;
import com.example.runningservice.dto.crew.CrewRequestDto.Update;
import com.example.runningservice.dto.crew.CrewResponseDto.CrewData;
import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.repository.CrewMemberRepository;
import com.example.runningservice.repository.CrewRepository;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.S3FileUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @InjectMocks
    private CrewService crewService;

    @Test
    @DisplayName("크루 생성 - 이미지 O")
    void createCrew_WithImage() {
        // given
        Long leaderId = 1L;
        Create create = Create.builder()
            .leaderId((leaderId))
            .crewImage(new MockMultipartFile("file", new byte[]{1, 2, 3}))
            .build();

        MemberEntity memberEntity = MemberEntity.builder().id(leaderId).build();
        CrewEntity crewEntity = CrewEntity.toEntity(create, memberEntity);

        given(memberRepository.findById(leaderId)).willReturn(Optional.of(memberEntity));
        given(crewRepository.save(any(CrewEntity.class))).willReturn(crewEntity);
        given(s3FileUtil.getImgUrl(anyString())).willReturn("http://example.com/image");
        doNothing().when(s3FileUtil).putObject(anyString(), any(MultipartFile.class));

        // when
        CrewData response = crewService.createCrew(create);

        // then
        assertEquals(crewEntity.getCrewId(), response.getCrewId());
        verify(s3FileUtil, times(1)).putObject("crew-" + crewEntity.getCrewId(),
            create.getCrewImage());
        verify(s3FileUtil, times(1)).getImgUrl("crew-" + crewEntity.getCrewId());
        verify(crewRepository, times(1)).save(any(CrewEntity.class));
    }

    @Test
    @DisplayName("크루 생성 - 이미지 X")
    public void createCrew_WithoutImage() {
        // given
        Long leaderId = 1L;
        Create newCrew = Create.builder()
            .leaderId(leaderId)
            .build();

        MemberEntity memberEntity = new MemberEntity();
        CrewEntity crewEntity = CrewEntity.toEntity(newCrew, memberEntity);

        given(memberRepository.findById(leaderId)).willReturn(Optional.of(memberEntity));
        given(crewRepository.save(any(CrewEntity.class))).willReturn(crewEntity);
        given(s3FileUtil.getImgUrl(anyString())).willReturn("http://example.com/default");

        // when
        CrewData response = crewService.createCrew(newCrew);

        // then
        assertEquals(crewEntity.getCrewId(), response.getCrewId());
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
        Create newCrew = Create.builder()
            .leaderId(leaderId)
            .build();

        given(memberRepository.findById(leaderId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () -> crewService.createCrew(newCrew));
    }

    @Test
    @DisplayName("크루 수정 - 이미지 O")
    public void updateCrew_WithImage() {
        // given
        Long crewId = 1L;
        Update update = CrewRequestDto.Update.builder()
            .crewId(crewId)
            .crewImage(new MockMultipartFile("file", new byte[]{1, 2, 3}))
            .build();

        MemberEntity memberEntity = MemberEntity.builder().nickName("hi").build();
        CrewEntity crewEntity = CrewEntity.builder().crewId(crewId).member(memberEntity).build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crewEntity));
        given(s3FileUtil.getImgUrl(anyString())).willReturn("http://example.com/image");
        doNothing().when(s3FileUtil).putObject(anyString(), any(MultipartFile.class));

        // when
        CrewData response = crewService.updateCrew(update);

        // then
        assertEquals(crewEntity.getCrewId(), response.getCrewId());
        assertEquals(crewEntity.getCrewImage(), "http://example.com/image");
        verify(s3FileUtil, times(1)).putObject("crew-" + crewEntity.getCrewId(),
            update.getCrewImage());
        verify(s3FileUtil, times(1)).getImgUrl("crew-" + crewEntity.getCrewId());
    }

    @Test
    @DisplayName("크루 수정 - 이미지 X")
    public void updateCrew_WithoutImage() {
        // given
        Long crewId = 1L;
        Update update = CrewRequestDto.Update.builder()
            .crewId(crewId)
            .build();

        MemberEntity memberEntity = MemberEntity.builder().nickName("hi").build();
        CrewEntity crewEntity = CrewEntity.builder().crewId(crewId).member(memberEntity).build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crewEntity));
        given(s3FileUtil.getImgUrl(anyString())).willReturn("http://example.com/default");

        // when
        CrewData response = crewService.updateCrew(update);

        // then
        assertEquals(crewEntity.getCrewId(), response.getCrewId());
        assertEquals(crewEntity.getCrewImage(), "http://example.com/default");
        verify(s3FileUtil, times(0)).putObject(any(), any());
        verify(s3FileUtil, times(1)).getImgUrl("crew-default");
    }
}