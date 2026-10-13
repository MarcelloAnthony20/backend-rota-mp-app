package br.mp.mpms.rotamp.infra.security;

import br.mp.mpms.rotamp.v1.model.entity.Perfil;
import br.mp.mpms.rotamp.v1.repository.PerfilRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * Filtro de alternância de visão (Impersonação de Perfil por Header HTTP).
 *
 * Funcionamento:
 * 1. O cliente HTTP (ex: Frontend Angular/React) envia o header 'X-Visao-Perfil: GESTOR' (ou MEMBRO, MOTORISTA, etc.).
 * 2. O filtro inspeciona a autenticação atual do Spring Security (gerada pelo Keycloak ou mock nos testes).
 * 3. Se e somente se o usuário possuir a autoridade 'ROLE_ADMINISTRADOR', o filtro permite a alternância.
 *    - Se o perfil solicitado existir e estiver ativo, as autoridades ativas no SecurityContext da requisição
 *      são ajustadas para refletir a visão simulada (ex: ROLE_GESTOR).
 *    - Uma authority indicativa 'ROLE_VISAO_SIMULADA' é adicionada para facilitar auditoria.
 * 4. Se um usuário SEM a role ADMINISTRADOR tentar enviar esse header, a requisição é rejeitada com HTTP 403 Forbidden.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VisaoPerfilFilter extends OncePerRequestFilter {

    public static final String HEADER_VISAO_PERFIL = "X-Visao-Perfil";
    public static final String ROLE_ADMINISTRADOR = "ROLE_ADMINISTRADOR";
    public static final String ROLE_VISAO_SIMULADA = "ROLE_VISAO_SIMULADA";
    private static final String ROLE_PREFIX = "ROLE_";

    private final org.springframework.beans.factory.ObjectProvider<PerfilRepository> perfilRepositoryProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String visaoSolicitada = request.getHeader(HEADER_VISAO_PERFIL);

        if (!StringUtils.hasText(visaoSolicitada)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .anyMatch(a -> a.equalsIgnoreCase(ROLE_ADMINISTRADOR) || a.equalsIgnoreCase("ADMINISTRADOR"));

        if (!isAdmin) {
            log.warn("Tentativa de alternância de visão negada: usuário '{}' não possui role ADMINISTRADOR.",
                    authentication.getName());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {"type":"about:blank","title":"Acesso Negado","status":403,"detail":"Apenas administradores podem alternar modo de visão"}
                    """);
            return;
        }

        String perfilFormatado = visaoSolicitada.trim().toUpperCase();
        if (perfilFormatado.startsWith(ROLE_PREFIX)) {
            perfilFormatado = perfilFormatado.substring(ROLE_PREFIX.length());
        }

        // Se a visão solicitada for o próprio ADMINISTRADOR, mantém original
        if ("ADMINISTRADOR".equalsIgnoreCase(perfilFormatado)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Valida se o perfil solicitado existe e está ativo na base
        PerfilRepository perfilRepository = perfilRepositoryProvider.getIfAvailable();
        if (perfilRepository != null) {
            Optional<Perfil> perfilOpt = perfilRepository.findByNomeIgnoreCase(perfilFormatado);
            if (perfilOpt.isEmpty() || !perfilOpt.get().isAtivo()) {
                log.warn("Perfil '{}' informado no header '{}' não existe ou está inativo.",
                        perfilFormatado, HEADER_VISAO_PERFIL);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(String.format("""
                        {"type":"about:blank","title":"Perfil Inválido","status":400,"detail":"Perfil '%s' não encontrado ou inativo para alternância de visão"}
                        """, perfilFormatado));
                return;
            }
        } else {
            // Em slices de teste onde PerfilRepository nao esta no contexto, valida contra perfis padroes do sistema
            java.util.Set<String> perfisPadrao = java.util.Set.of("ADMINISTRADOR", "GESTOR", "MOTORISTA", "SOLICITANTE", "MEMBRO");
            if (!perfisPadrao.contains(perfilFormatado)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(String.format("""
                        {"type":"about:blank","title":"Perfil Inválido","status":400,"detail":"Perfil '%s' não encontrado ou inativo para alternância de visão"}
                        """, perfilFormatado));
                return;
            }
        }


        // Aplica o contexto de visão simulada para esta requisição
        String novaRole = ROLE_PREFIX + perfilFormatado;
        Set<GrantedAuthority> novasAuthorities = Set.of(
                new SimpleGrantedAuthority(novaRole),
                new SimpleGrantedAuthority(ROLE_VISAO_SIMULADA)
        );

        Authentication authSimulada;
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            authSimulada = new JwtAuthenticationToken(jwtAuth.getToken(), novasAuthorities, jwtAuth.getName());
        } else {
            authSimulada = new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    novasAuthorities
            );
        }

        SecurityContextHolder.getContext().setAuthentication(authSimulada);
        log.info("Administrador '{}' operando sob modo de visão simulada: '{}'",
                authentication.getName(), novaRole);

        filterChain.doFilter(request, response);
    }
}

