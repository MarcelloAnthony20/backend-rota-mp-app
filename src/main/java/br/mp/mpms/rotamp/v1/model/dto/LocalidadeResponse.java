package br.mp.mpms.rotamp.v1.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados cadastrais e de endereço da localidade")
public record LocalidadeResponse(
        @Schema(description = "Identificador único (UUID) da localidade", example = "d1000001-0000-0000-0000-000000000001")
        UUID id,

        @Schema(description = "Identificador único (UUID) do tipo de localidade", example = "b1000001-0000-0000-0000-000000000001")
        UUID tipoLocalidadeId,

        @Schema(description = "Descrição/Nome do tipo de localidade", example = "Promotoria de Justiça")
        String tipoLocalidadeNome,

        @Schema(description = "Identificador único (UUID) da cidade", example = "c1d00001-0000-0000-0000-000000000001")
        UUID cidadeId,

        @Schema(description = "Nome da cidade vinculada", example = "Campo Grande")
        String cidadeNome,

        @Schema(description = "Nome da localidade", example = "Promotoria de Justiça da Cidadania")
        String nome,

        @Schema(description = "Indica se os dados de endereço são sigilosos", example = "false")
        boolean localSigiloso,

        @Schema(description = "Indica se a localidade está ativa", example = "true")
        boolean ativo,

        @Schema(description = "CEP do endereço predial (mascarado se sigiloso)", example = "79002000")
        String cep,

        @Schema(description = "Logradouro do endereço predial (mascarado se sigiloso)", example = "Rua da Paz")
        String logradouro,

        @Schema(description = "Número predial (mascarado se sigiloso)", example = "140")
        String numero,

        @Schema(description = "Bairro do endereço predial (mascarado se sigiloso)", example = "Jardim dos Estados")
        String bairro,

        @Schema(description = "Complemento predial (mascarado se sigiloso)", example = "Edifício Sede")
        String complemento
) {
}
