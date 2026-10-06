package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(description = "Payload para definicao e atualizacao de permissoes de telas por perfil (HU-02.3)")
public record ConfigurarPermissoesPerfilRequest(
        @Schema(description = "Lista de identificadores UUID das telas habilitadas para o perfil. Enviar lista vazia para revogar todas as permissoes.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "A lista de telas e obrigatoria")
        List<UUID> telasIds
) {
}
