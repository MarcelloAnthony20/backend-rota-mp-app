package br.mp.mpms.exemploservico.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para cadastro de uma nova cidade no catálogo da frota do MPMS")
public record CriarCidadeRequest(
        @Schema(
                description = "Nome oficial do município",
                example = "Campo Grande",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 200
        )
        @NotBlank(message = "O nome da cidade e obrigatorio")
        @Size(max = 200, message = "O nome da cidade deve ter no maximo 200 caracteres")
        String nome,

        @Schema(
                description = "Código identificador ou código IBGE do município (até 7 dígitos)",
                example = "5002704",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 7
        )
        @NotBlank(message = "O codigo da cidade e obrigatorio")
        @Size(max = 7, message = "O codigo da cidade deve ter no maximo 7 caracteres")
        String codigo,

        @Schema(
                description = "Indica se a cidade está ativa para operações e viagens da frota. Se omitido, o padrão é true (ativo)",
                example = "true",
                defaultValue = "true"
        )
        Boolean ativo
) {
}
