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
import java.lang.reflect.Field;
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
    void createCrew_WithImage() throws Exception {
        // given
        Long leaderId = 1L;

        Create create = new Create(null, null, null, null, null, null,
            null, null, null, null);
        Field f1 = Create.class.getDeclaredField("leaderId");
        f1.setAccessible(true);
        f1.set(create, leaderId);

        Field f2 = Create.class.getSuperclass().getDeclaredField("crewImage");
        f2.setAccessible(true);
        f2.set(create, new MockMultipartFile("file", new byte[]{1, 2, 3}));

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
    public void createCrew_WithoutImage() throws Exception {
        // given
        Long leaderId = 1L;

        Create newCrew = new Create(null, null, null, null, null, null,
            null, null, null, null);
        Field f1 = Create.class.getDeclaredField("leaderId");
        f1.setAccessible(true);
        f1.set(newCrew, leaderId);

        Field f2 = Create.class.getSuperclass().getDeclaredField("crewImage");
        f2.setAccessible(true);
        f2.set(newCrew, new MockMultipartFile("file", new byte[0]));

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
    public void createCrew_UserNotFound() throws Exception {
        // given
        Long leaderId = 1L;

        Create newCrew = new Create(null, null, null, null, null, null,
            null, null, null, null);
        Field f1 = Create.class.getDeclaredField("leaderId");
        f1.setAccessible(true);
        f1.set(newCrew, leaderId);

        given(memberRepository.findById(leaderId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () -> crewService.createCrew(newCrew));
    }

    @Test
    @DisplayName("크루 수정 - 이미지 O")
    public void updateCrew_WithImage() throws Exception {
        // given
        Long crewId = 1L;

        Update update = new Update(null, null, null, null, null
            , null, null, null, null);
        Field f1 = Update.class.getDeclaredField("crewId");
        f1.setAccessible(true);
        f1.set(update, crewId);

        Field f2 = Update.class.getSuperclass().getDeclaredField("crewImage");
        f2.setAccessible(true);
        f2.set(update, new MockMultipartFile("file", new byte[]{1, 2, 3}));

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
    public void updateCrew_WithoutImage() throws Exception {
        // given
        Long crewId = 1L;

        Update update = new Update(null, null, null, null, null
            , null, null, null, null);
        Field f1 = Update.class.getDeclaredField("crewId");
        f1.setAccessible(true);
        f1.set(update, crewId);

        Field f2 = Update.class.getSuperclass().getDeclaredField("crewImage");
        f2.setAccessible(true);
        f2.set(update, new MockMultipartFile("file", new byte[0]));

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

    @Test
    @DisplayName("크루 삭제 - 사용자 이미지")
    public void deleteCrew_WithImage() {
        // given
        Long crewId = 1L;

        MemberEntity memberEntity = MemberEntity.builder().nickName("hi").build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(crewId)
            .member(memberEntity)
            .crewImage("a/b/crew-1")
            .build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crewEntity));

        // when
        CrewData response = crewService.deleteCrew(crewId);

        // then
        assertEquals(crewEntity.getCrewId(), response.getCrewId());
        verify(s3FileUtil, times(1)).deleteObject("crew-1");
    }

    @Test
    @DisplayName("크루 삭제 - 기본 이미지")
    public void deleteCrew_WithoutImage() {
        // given
        Long crewId = 1L;

        MemberEntity memberEntity = MemberEntity.builder().nickName("hi").build();
        CrewEntity crewEntity = CrewEntity.builder()
            .crewId(crewId)
            .member(memberEntity)
            .crewImage("a/b/crew-default")
            .build();

        given(crewRepository.findById(crewId)).willReturn(Optional.of(crewEntity));

        // when
        CrewData response = crewService.deleteCrew(crewId);

        // then
        assertEquals(crewEntity.getCrewId(), response.getCrewId());
        verify(s3FileUtil, times(0)).deleteObject(anyString());
    }
}