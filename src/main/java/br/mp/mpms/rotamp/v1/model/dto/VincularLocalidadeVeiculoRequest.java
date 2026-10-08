package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Requisição para vincular um veículo à sua localidade de origem")
public record VincularLocalidadeVeiculoRequest(
        @Schema(
                description = "Identificador único (UUID) da nova localidade de origem do veículo",
                example = "d1000001-0000-0000-0000-000000000001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "O identificador da localidade e obrigatorio")
        UUID localidadeId
) {
}

