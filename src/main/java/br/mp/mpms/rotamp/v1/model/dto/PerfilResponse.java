package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Representação detalhada de um perfil institucional de acesso")
public record PerfilResponse(
        @Schema(description = "Identificador único (UUID) do perfil", example = "a1000001-0000-0000-0000-000000000001")
        UUID id,

        @Schema(description = "Nome descritivo do perfil", example = "ADMINISTRADOR")
        String nome,

        @Schema(description = "Descrição detalhada das responsabilidades e permissões", example = "Administrador geral do sistema com controle total sobre acessos")
        String descricao,

        @Schema(description = "Indicador de perfil ativo no sistema", example = "true")
        boolean ativo,

        @Schema(description = "Lista de telas e permissões associadas ao perfil")
        List<TelaResponse> telas
) {
    public PerfilResponse(UUID id, String nome, String descricao, boolean ativo) {
        this(id, nome, descricao, ativo, List.of());
    }
}
