package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload para alteracao de status ativo/inativo do perfil")
public record AlterarStatusPerfilRequest(
        @Schema(description = "Novo status do perfil (true para ativo, false para inativo)", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "O indicador de status ativo e obrigatorio")
        Boolean ativo
) {
}
