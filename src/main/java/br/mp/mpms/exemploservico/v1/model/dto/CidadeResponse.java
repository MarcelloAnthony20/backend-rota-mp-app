package br.mp.mpms.exemploservico.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados detalhados da cidade cadastrada no catálogo da frota")
public record CidadeResponse(
        @Schema(
                description = "Identificador único universal (UUID) da cidade",
                example = "c1d00001-0000-0000-0000-000000000001"
        )
        UUID id,

        @Schema(
                description = "Nome oficial do município",
                example = "Campo Grande"
        )
        String nome,

        @Schema(
                description = "Código identificador ou código IBGE do município",
                example = "5002704"
        )
        String codigo,

        @Schema(
                description = "Indica se a cidade está ativa para operações e viagens da frota",
                example = "true"
        )
        boolean ativo
) {
}
