package com.example.runningservice.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.runningservice.controller.UserJoinController;
import com.example.runningservice.service.LogoutService;
import com.example.runningservice.service.UserJoinService;
import com.example.runningservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserJoinController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class JwtAuthenticationFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    LogoutService logoutService;

    @MockBean
    private UserJoinService userJoinService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExpiredToken() throws Exception {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwidXNlcklkIjoxMiwic3ViIjoiZmxvd2Vyb253YWxsMzFAZ21haWwuY29tIiwiaWF0IjoxNzI1MTgwMzYwLCJleHAiOjE3MjUxODIxNjB9.NqbiJ2tHC_n0wLE9mwCdQHp2kHNE34M_1L4sBL0saKY";

        // Mocking JWT utility to simulate an expired token
        when(jwtUtil.isTokenExpired(expiredToken)).thenReturn(true);

        mockMvc.perform(post("/crew/1/join/apply")
                .header("Authorization", expiredToken))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value("TOKEN_EXPIRED"))
            .andExpect(jsonPath("$.message").exists());
    }
}