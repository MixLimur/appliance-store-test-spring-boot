package com.epam.rd.autocode.assessment.appliances.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CookieLogoutHandlerTest {

    private final CookieLogoutHandler logoutHandler = new CookieLogoutHandler();

    @Test
    @DisplayName("Should add an expired JWT cookie to response on logout")
    void logout_ShouldAddExpiredCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = mock(Authentication.class);

        logoutHandler.logout(request, response, authentication);

        Cookie cookie = response.getCookie("jwt");
        assertNotNull(cookie);
        assertNull(cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertEquals(0, cookie.getMaxAge());
    }
}