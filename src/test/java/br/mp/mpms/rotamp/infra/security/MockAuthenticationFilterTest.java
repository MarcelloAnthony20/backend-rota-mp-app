package br.mp.mpms.rotamp.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MockAuthenticationFilterTest {

    private MockAuthenticationFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new MockAuthenticationFilter();
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("deve autenticar como ADMIN quando token for mock-admin-token")
    void deve_autenticarComoAdmin_quando_tokenMockAdmin() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer mock-admin-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("admin.dev@mpms.mp.br");
        assertThat(auth.getAuthorities())
                .extracting(a -> a.getAuthority())
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    @DisplayName("deve autenticar como USER quando token for mock-user-token")
    void deve_autenticarComoUser_quando_tokenMockUser() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer mock-user-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("user.dev@mpms.mp.br");
        assertThat(auth.getAuthorities())
                .extracting(a -> a.getAuthority())
                .containsExactly("ROLE_USER");
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    @DisplayName("não deve autenticar quando cabeçalho Authorization estiver ausente")
    void naoDeve_autenticar_quando_headerAusente() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(filterChain).doFilter(any(), any());
    }
}

