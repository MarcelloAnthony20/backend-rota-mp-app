package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados detalhados do veículo com informações agregadas de modelo e localidade")
public record VeiculoDetalhesResponse(
        @Schema(description = "Identificador único (UUID) do veículo", example = "v1d00001-0000-0000-0000-000000000001")
        UUID id,

        @Schema(description = "Identificador único (UUID) da localidade de origem", example = "d1000001-0000-0000-0000-000000000001")
        UUID localidadeId,

        @Schema(description = "Nome da localidade de origem", example = "Edifício Sede - PGJ")
        String localidadeNome,

        @Schema(description = "Identificador único (UUID) do modelo de veículo", example = "m1d00001-0000-0000-0000-000000000001")
        UUID modeloId,

        @Schema(description = "Marca do modelo de veículo", example = "Toyota")
        String marca,

        @Schema(description = "Nome do modelo de veículo", example = "Corolla")
        String modelo,

        @Schema(description = "Capacidade de passageiros do veículo", example = "5")
        Integer numPassageiro,

        @Schema(description = "Placa do veículo", example = "RPA1B23")
        String placa,

        @Schema(description = "Ano de fabricação / modelo", example = "2024")
        Integer ano,

        @Schema(description = "Indica se o veículo está ativo no cadastro operacional", example = "true")
        boolean ativo
) {
}

