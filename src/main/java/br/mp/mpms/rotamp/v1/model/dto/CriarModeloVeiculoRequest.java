package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para cadastro de um novo modelo de veículo no catálogo da frota")
public record CriarModeloVeiculoRequest(
        @Schema(description = "Marca do fabricante do veículo", example = "Toyota", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
        @NotBlank(message = "A marca do veiculo e obrigatoria")
        @Size(max = 100, message = "A marca do veiculo deve ter no maximo 100 caracteres")
        String marca,

        @Schema(description = "Modelo ou versão do veículo", example = "Corolla", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
        @NotBlank(message = "O modelo do veiculo e obrigatorio")
        @Size(max = 100, message = "O modelo do veiculo deve ter no maximo 100 caracteres")
        String modelo,

        @Schema(description = "Capacidade de passageiros (incluindo o motorista)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "A capacidade de passageiros e obrigatoria")
        @Positive(message = "A capacidade de passageiros deve ser maior que zero")
        @Min(value = 1, message = "A capacidade de passageiros deve ser de pelo menos 1")
        @Max(value = 100, message = "A capacidade de passageiros deve ser no maximo 100")
        Integer numPassageiro,

        @Schema(description = "URL ou caminho do logo/emblema da marca ou modelo", example = "https://cdn.mpms.mp.br/marcas/toyota.png", maxLength = 500)
        @Size(max = 500, message = "A URL do logo deve ter no maximo 500 caracteres")
        String logo,

        @Schema(description = "Indica se o modelo está ativo para seleção em novos veículos", example = "true", defaultValue = "true")
        Boolean ativo
) {
}

