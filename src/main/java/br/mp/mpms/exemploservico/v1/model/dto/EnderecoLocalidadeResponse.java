package br.mp.mpms.exemploservico.v1.model.dto;

import java.util.UUID;

public record EnderecoLocalidadeResponse(
        UUID id,
        String logradouro,
        String numero,
        String cep,
        String bairro,
        String complemento,
        UUID cidadeId,
        UUID localidadeId
) {
}
