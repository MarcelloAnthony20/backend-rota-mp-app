package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados do modelo de veículo cadastrado")
public record ModeloVeiculoResponse(
        @Schema(description = "Identificador único (UUID) do modelo de veículo", example = "m1d00001-0000-0000-0000-000000000001")
        UUID id,

        @Schema(description = "Marca do fabricante do veículo", example = "Toyota")
        String marca,

        @Schema(description = "Modelo ou versão do veículo", example = "Corolla")
        String modelo,

        @Schema(description = "Capacidade de passageiros (incluindo o motorista)", example = "5")
        Integer numPassageiro,

        @Schema(description = "URL ou caminho do logo/emblema da marca ou modelo", example = "https://cdn.mpms.mp.br/marcas/toyota.png")
        String logo,

        @Schema(description = "Indica se o modelo está ativo para seleção na frota", example = "true")
        boolean ativo
) {
}
