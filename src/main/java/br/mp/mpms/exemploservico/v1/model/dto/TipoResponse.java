package br.mp.mpms.exemploservico.v1.model.dto;

import java.util.UUID;

public record TipoResponse(
        UUID id,
        String nome,
        String descricao,
        boolean ativo
) {
}
