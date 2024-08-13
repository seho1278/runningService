package com.example.runningservice.member;

import com.example.runningservice.dto.*;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Role;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.service.MemberService;
import com.example.runningservice.util.AESUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestMemberProfile {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AESUtil aesUtil;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void testGetMemberProfile() throws Exception {
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

        // when
        when(memberRepository.findById(userId)).thenReturn(Optional.of(mockMemberEntity));
        when(aesUtil.decrypt(encryptedPhoneNumber)).thenReturn("01012341234");

        MemberResponseDto memberResponseDto = memberService.getMemberProfile(userId);

        // then
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
    public void testUpdateMemberProfile() throws Exception{
        // given
        Long userId = 1L;

        MemberEntity mockMemberEntity = MemberEntity.builder()
            .id(userId)
            .nickName("test11")
            .birthYear(1998)
            .gender(Gender.MALE)
            .activityRegion(Region.BUSAN)
            .build();

        UpdateMemberRequestDto updateMemberRequestDto = UpdateMemberRequestDto.builder()
            .nickName("test22")
            .birthYear(1996)
            .gender(Gender.FEMALE)
            .activityRegion(Region.CHUNGBUK)
            .build();

        // when
        when(memberRepository.findById(userId)).thenReturn(Optional.of(mockMemberEntity));
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(mockMemberEntity);

        MemberResponseDto memberResponseDto = memberService.updateMemberProfile(userId, updateMemberRequestDto);

        // then
        assertNotNull(updateMemberRequestDto);
        assertEquals("test22", updateMemberRequestDto.getNickName());
        assertEquals(1996, updateMemberRequestDto.getBirthYear());
        assertEquals(Gender.FEMALE, updateMemberRequestDto.getGender());
        assertEquals(Region.CHUNGBUK, updateMemberRequestDto.getActivityRegion());

        verify(memberRepository, times(1)).findById(userId);
        verify(memberRepository, times(1)).save(mockMemberEntity);
    }

    @Test
    public void testUpdatePassword() throws Exception {
        // given
        Long userId = 1L;

        MemberEntity mockMemberEntity = MemberEntity.builder()
            .id(userId)
            .password("encryptedOldPassword")
            .build();

        PasswordRequestDto passwordRequestDto = new PasswordRequestDto(
            "oldPassword", "newPassword", "newPassword");

        // when
        when(memberRepository.findById(userId)).thenReturn(Optional.of(mockMemberEntity));
        when(passwordEncoder.matches(passwordRequestDto.getOldPassword(), mockMemberEntity.getPassword()))
            .thenReturn(true);
        when(aesUtil.encrypt(passwordRequestDto.getNewPassword())).thenReturn("encryptedNewPassword");

        memberService.updateMemberPassword(userId, passwordRequestDto);

        // then
        verify(memberRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(passwordRequestDto.getOldPassword(), "encryptedOldPassword");
        verify(aesUtil, times(1)).encrypt(passwordRequestDto.getNewPassword());
        verify(memberRepository, times(1)).save(mockMemberEntity);
        assertEquals("encryptedNewPassword", mockMemberEntity.getPassword());
    }

    @Test
    public void testDeleteMemberProfile() {
        // given
        Long userId = 1L;

        MemberEntity mockMemberEntity = MemberEntity.builder()
            .id(userId)
            .password("encryptedOldPassword")
            .build();

        DeleteRequestDto deleteRequestDto = new DeleteRequestDto("oldPassword");

        // when
        when(memberRepository.findById(userId)).thenReturn(Optional.of(mockMemberEntity));
        when(passwordEncoder.matches(deleteRequestDto.getPassword(), mockMemberEntity.getPassword()))
            .thenReturn(true);

        memberService.deleteMember(userId, deleteRequestDto);

        // then
        verify(memberRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(deleteRequestDto.getPassword(), mockMemberEntity.getPassword());
        verify(memberRepository, times(1)).delete(mockMemberEntity);
    }

    @Test
    public void testUpdateVisibility() {
        // given
        Long userId = 1L;

        MemberEntity mockMemberEntity = MemberEntity.builder()
            .id(userId)
            .nameVisibility(Visibility.PUBLIC)
            .phoneNumberVisibility(Visibility.PUBLIC)
            .genderVisibility(Visibility.PUBLIC)
            .birthYearVisibility(Visibility.PUBLIC)
            .build();

        ProfileVisibilityRequestDto profileVisibilityRequestDto = new ProfileVisibilityRequestDto(0, 0, 1, 1);

        // when
        when(memberRepository.findById(userId)).thenReturn(Optional.of(mockMemberEntity));
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(mockMemberEntity);

        ProfileVisibilityResponseDto profileVisibilityResponseDto = memberService.updateProfileVisibility(userId, profileVisibilityRequestDto);

        // then
        verify(memberRepository, times(1)).findById(userId);
        verify(memberRepository, times(1)).save(mockMemberEntity);

        assertEquals(Visibility.PUBLIC, profileVisibilityResponseDto.getUserName());
        assertEquals(Visibility.PUBLIC, profileVisibilityResponseDto.getPhoneNumber());
        assertEquals(Visibility.PRIVATE, profileVisibilityResponseDto.getGender());
        assertEquals(Visibility.PRIVATE, profileVisibilityResponseDto.getBirthYear());
    }
}
