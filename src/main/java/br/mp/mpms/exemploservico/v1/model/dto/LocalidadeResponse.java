package br.mp.mpms.exemploservico.v1.model.dto;

import java.util.UUID;

public record LocalidadeResponse(
        UUID id,
        UUID tipoLocalidadeId,
        UUID cidadeId,
        String nome,
        boolean localSigiloso,
        boolean ativo
) {
}
