package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados do veículo cadastrado na frota oficial")
public record VeiculoResponse(
        @Schema(description = "Identificador único (UUID) do veículo", example = "v1d00001-0000-0000-0000-000000000001")
        UUID id,

        @Schema(description = "Identificador único (UUID) da localidade de origem do veículo", example = "d1000001-0000-0000-0000-000000000001")
        UUID localidadeId,

        @Schema(description = "Identificador único (UUID) do modelo de veículo vinculado", example = "m1d00001-0000-0000-0000-000000000001")
        UUID modeloId,

        @Schema(description = "Placa identificadora do veículo", example = "RPA1B23")
        String placa,

        @Schema(description = "Ano de fabricação / modelo do veículo", example = "2024")
        Integer ano,

        @Schema(description = "Indica se o veículo está ativo para novas viagens e atendimentos operacionais", example = "true")
        boolean ativo
) {
}
