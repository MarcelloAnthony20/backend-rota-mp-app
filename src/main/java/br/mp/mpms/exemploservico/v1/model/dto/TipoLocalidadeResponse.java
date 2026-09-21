package br.mp.mpms.exemploservico.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados detalhados do tipo de localidade")
public record TipoLocalidadeResponse(
        @Schema(description = "Identificador único (UUID) do tipo de localidade", example = "b1000001-0000-0000-0000-000000000001")
        UUID id,

        @Schema(description = "Código do tipo de localidade", example = "PROJUS")
        String codigo,

        @Schema(description = "Descrição do tipo de localidade", example = "Promotoria de Justiça")
        String tipoLocalidade,

        @Schema(description = "Indica se o tipo de localidade está ativo", example = "true")
        boolean ativo
) {
}
