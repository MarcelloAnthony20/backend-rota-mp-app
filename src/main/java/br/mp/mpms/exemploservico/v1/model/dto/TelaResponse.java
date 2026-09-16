package br.mp.mpms.exemploservico.v1.model.dto;

import java.util.UUID;

public record TelaResponse(
        UUID id,
        String nome,
        String rota
) {
}
