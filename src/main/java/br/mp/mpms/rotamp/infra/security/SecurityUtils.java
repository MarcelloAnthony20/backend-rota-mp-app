package br.mp.mpms.rotamp.infra.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utilitário de consulta do contexto de segurança atual da requisição.
 */
@Component
public class SecurityUtils {

    /**
     * Retorna o identificador/username ou subject do usuário autenticado.
     */
    public Optional<String> getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.ofNullable(auth.getName());
    }

    /**
     * Verifica se a requisição atual está rodando sob visão simulada por um Administrador.
     */
    public boolean isVisaoSimulada() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(VisaoPerfilFilter.ROLE_VISAO_SIMULADA::equalsIgnoreCase);
    }
}

