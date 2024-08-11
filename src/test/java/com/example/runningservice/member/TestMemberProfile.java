package com.example.runningservice.member;

import com.example.runningservice.dto.MemberResponseDto;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Role;
import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestMemberProfile {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void testGetMemberProfile() {
        // given
        Long userId = 1L;

        MemberEntity mockMemberEntity = MemberEntity.builder()
            .id(userId)
            .email("test@naver.com")
            .phoneNumber("01012341234")
            .name("TestMember")
            .nickName("TestMemberNickName")
            .birthYear(1996)
            .gender(Gender.MALE)
            .roles(List.of(Role.ROLE_USER))
            .build();

        // when
        when(memberRepository.findById(userId)).thenReturn(Optional.of(mockMemberEntity));

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
    public void updateMemberProfile() {
        // given
    }

}
