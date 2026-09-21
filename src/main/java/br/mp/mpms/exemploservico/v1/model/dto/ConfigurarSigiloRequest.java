package br.mp.mpms.exemploservico.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Requisição para configuração de sigilo de uma localidade")
public record ConfigurarSigiloRequest(
        @Schema(
                description = "Nova definição de sigilo da localidade",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "A indicacao de local sigiloso e obrigatoria")
        Boolean localSigiloso
) {
}
