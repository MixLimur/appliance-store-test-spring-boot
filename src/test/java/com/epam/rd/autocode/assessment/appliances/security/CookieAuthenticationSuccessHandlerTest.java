package com.epam.rd.autocode.assessment.appliances.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieAuthenticationSuccessHandlerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CookieAuthenticationSuccessHandler successHandler;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Should generate token, add HTTP-only cookie and redirect to root")
    void onAuthenticationSuccess_ShouldAddCookieAndRedirect() throws IOException, ServletException {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        String generatedToken = "mock.jwt.token";

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(generatedToken);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        Cookie cookie = response.getCookie("jwt");
        assertNotNull(cookie);
        assertEquals(generatedToken, cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertFalse(cookie.getSecure());
        assertEquals(24 * 60 * 60, cookie.getMaxAge());
        assertEquals("/", response.getRedirectedUrl());

        verify(jwtService).generateToken(userDetails);
    }

}