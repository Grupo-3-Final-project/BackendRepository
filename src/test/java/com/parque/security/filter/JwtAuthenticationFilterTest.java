package com.parque.security.filter;

import com.parque.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_shouldIgnoreRequestWithoutAuthorizationHeader() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isSameAs(request);
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void doFilter_shouldIgnoreNonBearerAuthorizationHeader() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Token raw-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isSameAs(request);
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void doFilter_shouldAuthenticateWhenTokenIsValid() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtProvider.validateToken("valid-token")).thenReturn(true);
        when(jwtProvider.getUsernameFromToken("valid-token")).thenReturn("admin");
        when(jwtProvider.getCredentialIdFromToken("valid-token")).thenReturn(7L);
        when(jwtProvider.getRoleFromToken("valid-token")).thenReturn("ADMIN");

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("admin");
        assertThat(authentication.getDetails()).isEqualTo(7L);
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
        assertThat(filterChain.getRequest()).isSameAs(request);
    }

    @Test
    void doFilter_shouldLeaveContextEmptyWhenTokenIsInvalid() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtProvider.validateToken("invalid-token")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isSameAs(request);
    }

    @Test
    void doFilter_shouldContinueWhenJwtProviderThrowsException() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer exploding-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtProvider.validateToken("exploding-token")).thenThrow(new RuntimeException("boom"));

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isSameAs(request);
    }
}
