package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Requisição para alteração dos dados cadastrais e de endereço de uma localidade. O status de ativação é gerido exclusivamente via DELETE /{id} (inativação) e PATCH /{id}/ativar (reativação).")
public record AtualizarLocalidadeRequest(
        @Schema(
                description = "Nome da localidade",
                example = "Promotoria de Justiça da Cidadania",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 200
        )
        @NotBlank(message = "O nome da localidade e obrigatorio")
        @Size(max = 200, message = "O nome da localidade deve ter no maximo 200 caracteres")
        String nome,

        @Schema(
                description = "Identificador (UUID) da cidade à qual a localidade pertence",
                example = "c1d00001-0000-0000-0000-000000000001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "O ID da cidade e obrigatorio")
        UUID cidadeId,

        @Schema(
                description = "Identificador (UUID) do tipo de localidade",
                example = "b1000001-0000-0000-0000-000000000001",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "O ID do tipo de localidade e obrigatorio")
        UUID tipoLocalidadeId,

        @Schema(
                description = "CEP do endereço da localidade (formato 99999-999 ou 99999999)",
                example = "79002-000"
        )
        @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP em formato invalido")
        String cep,

        @Schema(
                description = "Logradouro do endereço predial",
                example = "Rua da Paz",
                maxLength = 200
        )
        @Size(max = 200, message = "O logradouro deve ter no maximo 200 caracteres")
        String logradouro,

        @Schema(
                description = "Número predial (inteiro positivo maior que zero)",
                example = "140",
                maxLength = 20
        )
        @Size(max = 20, message = "O numero deve ter no maximo 20 caracteres")
        @Pattern(regexp = "^[1-9]\\d*$", message = "O numero predial deve ser um inteiro positivo maior que zero")
        String numero,

        @Schema(
                description = "Bairro do endereço predial",
                example = "Jardim dos Estados",
                maxLength = 100
        )
        @Size(max = 100, message = "O bairro deve ter no maximo 100 caracteres")
        String bairro,

        @Schema(
                description = "Complemento do endereço predial (opcional)",
                example = "Edifício Sede",
                maxLength = 200
        )
        @Size(max = 200, message = "O complemento deve ter no maximo 200 caracteres")
        String complemento,

        @Schema(
                description = "Indica se a localidade é sigilosa (confidencial)",
                example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "A indicacao de local sigiloso e obrigatoria")
        Boolean localSigiloso
) {
}
