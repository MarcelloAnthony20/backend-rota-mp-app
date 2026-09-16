package br.mp.mpms.exemploservico.v1.model.dto;

import java.util.UUID;

public record PerfilTelaResponse(
        UUID id,
        UUID perfilId,
        UUID telaId
) {
}
