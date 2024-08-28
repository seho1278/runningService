package com.example.runningservice.service;

import com.example.runningservice.dto.member.DeleteRequestDto;
import com.example.runningservice.dto.member.MemberResponseDto;
import com.example.runningservice.dto.member.PasswordRequestDto;
import com.example.runningservice.dto.member.UpdateMemberRequestDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Role;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.S3FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AESUtil aesUtil;

    @Mock
    private S3FileUtil s3FileUtil;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void testGetMemberProfile_success() throws Exception {
        // given
        Long userId = 1L;
        String encryptedPhoneNumber = aesUtil.encrypt("01012341234");

        MemberEntity mockMemberEntity = MemberEntity.builder()
            .id(userId)
            .email("test@naver.com")
            .phoneNumber(encryptedPhoneNumber)
            .name("TestMember")
            .nickName("TestMemberNickName")
            .birthYear(1996)
            .gender(Gender.MALE)
            .roles(List.of(Role.ROLE_USER))
            .build();

        when(memberRepository.findMemberById(userId)).thenReturn(mockMemberEntity);
        when(aesUtil.decrypt(encryptedPhoneNumber)).thenReturn("01012341234");

        // when
        MemberResponseDto memberResponseDto = memberService.getMemberProfile(userId);

        // then
        verify(memberRepository, times(1)).findMemberById(userId);

        assertNotNull(memberResponseDto);
        assertEquals(userId, memberResponseDto.getId());
        assertEquals("test@naver.com", memberResponseDto.getEmail());
        assertEquals("01012341234", memberResponseDto.getPhoneNumber());
        assertEquals("TestMember", memberResponseDto.getName());
        assertEquals("TestMemberNickName", memberResponseDto.getNickName());
        assertEquals(1996, memberResponseDto.getBirthYear());
        assertEquals(Gender.MALE, memberResponseDto.getGender());
        assertEquals(List.of(Role.ROLE_USER), memberResponseDto.getRoles());
    }

    @Test
    public void testUpdateMemberProfile_success() throws Exception {
        // given
        Long userId = 1L;

        String fileName = "user-" + userId;
        String oldImageUrl = "http://example.s3.region.amazonaws.com/oldImage";
        String imageUrl = s3FileUtil.getImgUrl(fileName);

        MemberEntity mockMemberEntity = MemberEntity.builder()
            .id(userId)
            .nickName("test11")
            .birthYear(1998)
            .gender(Gender.MALE)
            .activityRegion(Region.BUSAN)
            .profileImageUrl(oldImageUrl)
            .nameVisibility(Visibility.PRIVATE)
            .phoneNumberVisibility(Visibility.PRIVATE)
            .genderVisibility(Visibility.PRIVATE)
            .birthYearVisibility(Visibility.PRIVATE)
            .build();

        when(memberRepository.findMemberById(userId)).thenReturn(mockMemberEntity);
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(mockMemberEntity);

        MultipartFile profileImage = mock(MultipartFile.class);

        UpdateMemberRequestDto updateMemberRequestDto = UpdateMemberRequestDto.builder()
            .nickName("test22")
            .birthYear(1996)
            .gender(Gender.FEMALE)
            .activityRegion(Region.INCHEON)
            .profileImage(profileImage)
            .nameVisibility(Visibility.PRIVATE)
            .phoneNumberVisibility(Visibility.PRIVATE)
            .genderVisibility(Visibility.PRIVATE)
            .birthYearVisibility(Visibility.PRIVATE)
            .build();

        when(s3FileUtil.getImgUrl(fileName)).thenReturn(imageUrl);

        // when
        MemberResponseDto memberResponseDto = memberService.updateMemberProfile(userId, updateMemberRequestDto);

        // then
        verify(memberRepository, times(1)).findMemberById(userId);
        verify(memberRepository, times(1)).save(mockMemberEntity);

        assertEquals("test22", memberResponseDto.getNickName());
        assertEquals(1996, memberResponseDto.getBirthYear());
        assertEquals(Gender.FEMALE, memberResponseDto.getGender());
        assertEquals(Region.INCHEON, memberResponseDto.getActivityRegion());
        assertEquals(imageUrl, memberResponseDto.getImageUrl());
    }

    @Test
    public void testUpdatePassword_success() throws Exception {
        // given
        Long userId = 1L;

        MemberEntity mockMemberEntity = MemberEntity.builder()
            .id(userId)
            .password("encryptedOldPassword")
            .build();

        PasswordRequestDto passwordRequestDto = new PasswordRequestDto(
            "oldPassword", "newPassword", "newPassword");

        when(memberRepository.findMemberById(userId)).thenReturn(mockMemberEntity);
        when(passwordEncoder.matches(passwordRequestDto.getOldPassword(), mockMemberEntity.getPassword()))
            .thenReturn(true);
        when(passwordEncoder.encode(passwordRequestDto.getNewPassword())).thenReturn("encryptedNewPassword");

        // when
        memberService.updateMemberPassword(userId, passwordRequestDto);

        // then
        verify(memberRepository, times(1)).findMemberById(userId);
        verify(passwordEncoder, times(1)).matches(passwordRequestDto.getOldPassword(), "encryptedOldPassword");
        verify(passwordEncoder, times(1)).encode(passwordRequestDto.getNewPassword());
        verify(memberRepository, times(1)).save(mockMemberEntity);
        assertEquals("encryptedNewPassword", mockMemberEntity.getPassword());
    }

    @Test
    public void testDeleteMemberProfile_success() {
        // given
        Long userId = 1L;

        MemberEntity mockMemberEntity = MemberEntity.builder()
            .id(userId)
            .password("encryptedOldPassword")
            .build();

        DeleteRequestDto deleteRequestDto = new DeleteRequestDto("oldPassword");

        when(memberRepository.findMemberById(userId)).thenReturn(mockMemberEntity);
        when(passwordEncoder.matches(deleteRequestDto.getPassword(), mockMemberEntity.getPassword()))
            .thenReturn(true);

        // when
        memberService.deleteMember(userId, deleteRequestDto);

        // then
        verify(memberRepository, times(1)).findMemberById(userId);
        verify(passwordEncoder, times(1)).matches(deleteRequestDto.getPassword(), mockMemberEntity.getPassword());
        verify(memberRepository, times(1)).delete(mockMemberEntity);
    }
}
