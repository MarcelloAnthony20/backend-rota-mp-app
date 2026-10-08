package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados do registro fotográfico vinculado ao veículo")
public record FotoVeiculoResponse(
        @Schema(description = "Identificador único (UUID) da foto", example = "f1d00001-0000-0000-0000-000000000001")
        UUID id,

        @Schema(description = "URL ou caminho de armazenamento da imagem", example = "https://cdn.mpms.mp.br/veiculos/toyota-corolla-frente.jpg")
        String foto,

        @Schema(description = "Identificador único (UUID) do veículo vinculado", example = "v1d00001-0000-0000-0000-000000000001")
        UUID veiculoId
) {
}
