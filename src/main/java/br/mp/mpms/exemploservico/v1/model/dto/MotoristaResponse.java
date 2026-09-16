package br.mp.mpms.exemploservico.v1.model.dto;

import br.mp.mpms.exemploservico.v1.model.entity.StatusMotorista;

import java.util.UUID;

public record MotoristaResponse(
        UUID id,
        String displayNome,
        UUID localidadeId,
        String matricula,
        String foto,
        String contatoFuncional,
        StatusMotorista status,
        UUID usuarioId
) {
}
