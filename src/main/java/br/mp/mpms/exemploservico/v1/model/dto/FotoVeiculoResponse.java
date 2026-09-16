package br.mp.mpms.exemploservico.v1.model.dto;

import java.util.UUID;

public record FotoVeiculoResponse(
        UUID id,
        String foto,
        UUID veiculoId
) {
}
