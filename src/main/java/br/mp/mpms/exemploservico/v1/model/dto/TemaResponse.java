package br.mp.mpms.exemploservico.v1.model.dto;

import java.util.UUID;

public record TemaResponse(
        UUID id,
        boolean tema
) {
}
