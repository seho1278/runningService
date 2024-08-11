package com.example.runningservice.member;

import com.example.runningservice.repository.MemberRepository;
import com.example.runningservice.service.MemberService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class TestGetMemberProfile {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void testGetMemberProfile() {

    }

}
