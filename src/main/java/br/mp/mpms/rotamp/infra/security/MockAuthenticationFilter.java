package br.mp.mpms.rotamp.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * =========================================================================================
 * ATENÇÃO - COMPONENTE TEMPORÁRIO DE MOCK DE AUTENTICAÇÃO (PROFILE DEV)
 * =========================================================================================
 * Este filtro intercepta tokens fictícios no cabeçalho Authorization para viabilizar
 * o desenvolvimento e testes integrados do Frontend enquanto o Keycloak não está plugado.
 *
 * Estratégia de Mock:
 * - "Bearer mock-admin-token" -> Usuário autenticado com roles ROLE_ADMIN e ROLE_USER
 * - "Bearer mock-user-token"  -> Usuário autenticado com role ROLE_USER
 *
 * TRANSIÇÃO FUTURA PARA O KEYCLOAK:
 * 1. Excluir este arquivo ou remover a configuração em SecurityConfig.
 * 2. No SecurityConfig, plugar o .oauth2ResourceServer(oauth2 -> oauth2.jwt(...)).
 * =========================================================================================
 */
@Slf4j
public class MockAuthenticationFilter extends OncePerRequestFilter {

    public static final String MOCK_ADMIN_TOKEN = "Bearer mock-admin-token";
    public static final String MOCK_USER_TOKEN = "Bearer mock-user-token";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("[MOCK AUTH] Interceptando URI: {}, Header: {}", request.getRequestURI(), authHeader);
        HttpServletRequest requestToUse = request;

        if (StringUtils.hasText(authHeader)) {
            if (MOCK_ADMIN_TOKEN.equalsIgnoreCase(authHeader)) {
                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")
                );
                var auth = new UsernamePasswordAuthenticationToken(
                        "admin.dev@mpms.mp.br",
                        null,
                        authorities
                );
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
                log.debug("[MOCK AUTH] Usuário autenticado com ROLE_ADMIN via mock-admin-token");
                requestToUse = wrapRequestSuppressingAuthHeader(request);

            } else if (MOCK_USER_TOKEN.equalsIgnoreCase(authHeader)) {
                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                );
                var auth = new UsernamePasswordAuthenticationToken(
                        "user.dev@mpms.mp.br",
                        null,
                        authorities
                );
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
                log.debug("[MOCK AUTH] Usuário autenticado com ROLE_USER via mock-user-token");
                requestToUse = wrapRequestSuppressingAuthHeader(request);

            } else {
                log.debug("[MOCK AUTH] Header Authorization presente, porém não corresponde aos tokens de mock.");
            }
        }

        filterChain.doFilter(requestToUse, response);
    }

    /**
     * Envolve a requisição ocultando o header Authorization mockado dos filtros
     * subsequentes (como o BearerTokenAuthenticationFilter do OAuth2 Resource Server).
     */
    private HttpServletRequest wrapRequestSuppressingAuthHeader(HttpServletRequest request) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
                    return null;
                }
                return super.getHeader(name);
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
                    return Collections.emptyEnumeration();
                }
                return super.getHeaders(name);
            }
        };
    }
}

