package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Representação de uma tela/módulo do sistema")
public record TelaResponse(
        @Schema(description = "Identificador único (UUID) da tela", example = "t1000001-0000-0000-0000-000000000001")
        UUID id,

        @Schema(description = "Nome amigável da tela", example = "Gestão de Veículos")
        String nome,

        @Schema(description = "Caminho da rota da tela", example = "/veiculos")
        String rota
) {
}
