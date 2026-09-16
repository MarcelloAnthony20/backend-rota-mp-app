package br.mp.mpms.exemploservico.v1.model.dto;

import java.util.UUID;

public record TipoLocalidadeResponse(
        UUID id,
        String codigo,
        String tipoLocalidade,
        boolean ativo
) {
}
