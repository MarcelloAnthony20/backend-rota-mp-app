package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Requisição para reclassificação do tipo de uma localidade")
public record ClassificarLocalidadeRequest(
        @Schema(
                description = "Novo identificador único (UUID) do tipo de localidade",
                example = "t1p00001-0000-0000-0000-000000000001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "O ID do tipo de localidade e obrigatorio")
        UUID tipoLocalidadeId
) {
}
