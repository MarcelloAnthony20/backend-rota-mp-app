package br.mp.mpms.exemploservico.v1.model.dto;

import br.mp.mpms.exemploservico.v1.model.entity.StatusViagem;

import java.time.LocalDateTime;
import java.util.UUID;

public record ViagemResponse(
        UUID id,
        UUID motoristaId,
        UUID solicitacaoId,
        UUID veiculoId,
        StatusViagem status,
        LocalDateTime dataInicio,
        LocalDateTime dataConclusao
) {
}
