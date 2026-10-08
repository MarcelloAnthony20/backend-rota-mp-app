package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Requisição para anexar uma nova fotografia ao veículo")
public record CriarFotoVeiculoRequest(
        @Schema(
                description = "URL ou caminho de armazenamento da imagem fotográfica",
                example = "https://cdn.mpms.mp.br/veiculos/corolla-2024-frontal.jpg",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 500
        )
        @NotBlank(message = "A URL ou caminho da foto e obrigatorio")
        @Size(max = 500, message = "A URL da foto deve ter no maximo 500 caracteres")
        String foto,

        @Schema(
                description = "Identificador único do veículo vinculado",
                example = "v1d00001-0000-0000-0000-000000000001"
        )
        UUID veiculoId
) {
    public CriarFotoVeiculoRequest(String foto) {
        this(foto, null);
    }
}


