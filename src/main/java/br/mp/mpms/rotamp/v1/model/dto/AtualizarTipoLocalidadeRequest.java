package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para atualização de um tipo de localidade")
public record AtualizarTipoLocalidadeRequest(
        @Schema(
                description = "Código identificador do tipo de localidade",
                example = "PROJUS",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 50
        )
        @NotBlank(message = "O codigo e obrigatorio")
        @Size(max = 50, message = "O codigo deve ter no maximo 50 caracteres")
        String codigo,

        @Schema(
                description = "Descrição ou nome do tipo de localidade",
                example = "Promotoria de Justiça",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100
        )
        @NotBlank(message = "A descricao do tipo de localidade e obrigatoria")
        @Size(max = 100, message = "A descricao do tipo de localidade deve ter no maximo 100 caracteres")
        String tipoLocalidade,

        @Schema(
                description = "Indica se o tipo de localidade está ativo",
                example = "true",
                defaultValue = "true"
        )
        Boolean ativo
) {
}
