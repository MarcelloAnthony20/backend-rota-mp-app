package br.mp.mpms.exemploservico.v1.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarCidadeRequest(
        @NotBlank(message = "O nome da cidade e obrigatorio")
        @Size(max = 200, message = "O nome da cidade deve ter no maximo 200 caracteres")
        String nome,

        @NotBlank(message = "O codigo da cidade e obrigatorio")
        @Size(max = 7, message = "O codigo da cidade deve ter no maximo 7 caracteres")
        String codigo,

        Boolean ativo
) {
}
