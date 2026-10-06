package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "Payload para cadastro de um novo perfil institucional de acesso")
public record CriarPerfilRequest(
        @Schema(description = "Nome do perfil institucional", example = "AUDITOR", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O nome do perfil e obrigatorio")
        @Size(max = 255, message = "O nome do perfil deve ter no maximo 255 caracteres")
        String nome,

        @Schema(description = "Descricao detalhada das responsabilidades", example = "Perfil com acesso de auditoria e relatorios", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "A descricao do perfil e obrigatoria")
        @Size(max = 255, message = "A descricao do perfil deve ter no maximo 255 caracteres")
        String descricao,

        @Schema(description = "Indicador de perfil ativo", example = "true", defaultValue = "true")
        Boolean ativo,

        @Schema(description = "Lista opcional de UUIDs das telas associadas ao perfil")
        List<UUID> telasIds
) {
}
