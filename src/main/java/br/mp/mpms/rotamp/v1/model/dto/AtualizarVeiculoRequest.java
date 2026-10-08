package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Requisição para atualização dos dados cadastrais de um veículo da frota")
public record AtualizarVeiculoRequest(
        @Schema(
                description = "Identificador único (UUID) da localidade de origem onde o veículo está alocado",
                example = "d1000001-0000-0000-0000-000000000001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "A localidade de origem e obrigatoria")
        UUID localidadeId,

        @Schema(
                description = "Identificador único (UUID) do modelo de veículo",
                example = "m1d00001-0000-0000-0000-000000000001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "O modelo do veiculo e obrigatorio")
        UUID modeloId,

        @Schema(
                description = "Placa identificadora do veículo (formato padrão brasileiro ou Mercosul)",
                example = "RPA1B23",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 10
        )
        @NotBlank(message = "A placa do veiculo e obrigatoria")
        @Size(max = 10, message = "A placa do veiculo deve ter no maximo 10 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9-]{7,10}$", message = "A placa do veiculo possui formato invalido")
        String placa,

        @Schema(
                description = "Ano de fabricação / modelo do veículo",
                example = "2024",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "O ano de fabricacao e obrigatorio")
        @Min(value = 1950, message = "O ano deve ser a partir de 1950")
        @Max(value = 2100, message = "O ano nao pode ser superior a 2100")
        Integer ano
) {
}

