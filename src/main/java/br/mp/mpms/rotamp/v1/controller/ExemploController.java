package br.mp.mpms.rotamp.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * Controller de exemplo para validação e testes de proteção de endpoints (RBAC).
 * Utiliza @PreAuthorize para garantir que apenas os papéis adequados tenham acesso.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Exemplo RBAC", description = "Endpoints de exemplo demonstrando controle de acesso baseado em roles")
public class ExemploController {

    @Operation(summary = "Obtém dados administrativos restritos", description = "Requer autorização com a role ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados obtidos com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (não possui ROLE_ADMIN)")
    })
    @GetMapping("/admin/dados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosAdminResponse> obterDadosAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        var response = new DadosAdminResponse(
                "Painel Administrativo Consolidado",
                auth.getName(),
                auth.getAuthorities().stream().map(a -> a.getAuthority()).toList(),
                Instant.now()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtém perfil do usuário", description = "Requer autorização com a role USER")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil obtido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (não possui ROLE_USER)")
    })
    @GetMapping("/user/perfil")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PerfilUsuarioResponse> obterPerfilUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        var response = new PerfilUsuarioResponse(
                auth.getName(),
                "Ativo",
                auth.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        );

        return ResponseEntity.ok(response);
    }

    public record DadosAdminResponse(
            String titulo,
            String usuarioSolicitante,
            List<String> papeis,
            Instant timestamp
    ) {}

    public record PerfilUsuarioResponse(
            String email,
            String status,
            List<String> papeis
    ) {}
}

