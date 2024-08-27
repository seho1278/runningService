package com.example.runningservice.controller;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.runningservice.dto.join.CrewApplicantDetailResponseDto;
import com.example.runningservice.dto.join.CrewApplicantResponseDto;
import com.example.runningservice.dto.join.GetApplicantsRequestDto;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.service.CrewApplicantService;
import com.example.runningservice.util.JwtUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(CrewApplicantController.class)
@MockBean(JpaMetamodelMappingContext.class)
//@Import({CrewRoleCheckAspect.class})  // AOP 설정을 테스트에 포함시킵니다.
class CrewApplicantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrewApplicantService crewApplicantService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("모든 가입신청자 리스트 조회")
    @WithMockUser("USER")
    void getAllApplicants_success() throws Exception {
        //given
        Long userId = 1L;
        Long crewId = 2L;

        GetApplicantsRequestDto requestDto = GetApplicantsRequestDto.builder()
            .status(JoinStatus.PENDING)
            .pageable(PageRequest.of(0, 5))
            .build();

        CrewApplicantResponseDto crewApplicantResponseDto1 = CrewApplicantDetailResponseDto
            .builder()
            .nickName("testNick1")
            .profileImage("testImage1")
            .message("testMessage1")
            .appliedAt(LocalDateTime.of(2024, 1, 1, 1, 1))
            .build();

        CrewApplicantResponseDto crewApplicantResponseDto2 = CrewApplicantDetailResponseDto
            .builder()
            .nickName("testNick2")
            .profileImage("testImage2")
            .message("testMessage2")
            .appliedAt(LocalDateTime.of(2024, 1, 1, 1, 2))
            .build();

        Page<CrewApplicantResponseDto> responsePage = new PageImpl<>(
            List.of(crewApplicantResponseDto1, crewApplicantResponseDto2));

        when(jwtUtil.validateToken(1L, "mockToken")).thenReturn(true);
        when(jwtUtil.getAuthentication("mockToken")).thenReturn(
            new UsernamePasswordAuthenticationToken("user", null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        when(crewApplicantService.getAllJoinApplications(eq(crewId),
            argThat(it -> it.getStatus().equals(JoinStatus.PENDING) &&
                it.getPageable().getPageNumber() == 0 &&
                it.getPageable().getPageSize() == 5))).thenReturn(
            responsePage);
        //when
        //then
        mockMvc.perform(get("/crew/2/join/list")
                .param("status", JoinStatus.PENDING.name())
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON)
                .with(request -> {
                    request.addHeader("Authorization", "Bearer mockToken");
                    request.setAttribute("loginId", 1L);
                    return request;
                }))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].nickName").value("testNick1"))
            .andExpect(jsonPath("$.content[1].nickName").value("testNick2"));
    }

    private RequestPostProcessor mockJwtToken() {
        return request -> {
            request.addHeader("Authorization", "Bearer mockToken");
            request.setAttribute("loginId", 1L);  // Manually setting loginId
            return request;
        };
    }
}