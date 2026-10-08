package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para atualização ou substituição de uma fotografia do veículo")
public record AtualizarFotoVeiculoRequest(
        @Schema(
                description = "Nova URL ou caminho de armazenamento da imagem fotográfica",
                example = "https://cdn.mpms.mp.br/veiculos/corolla-2024-lateral.jpg",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 500
        )
        @NotBlank(message = "A URL ou caminho da foto e obrigatorio")
        @Size(max = 500, message = "A URL da foto deve ter no maximo 500 caracteres")
        String foto
) {
}

