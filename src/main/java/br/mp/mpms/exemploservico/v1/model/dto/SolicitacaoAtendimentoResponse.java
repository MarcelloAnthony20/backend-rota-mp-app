package br.mp.mpms.exemploservico.v1.model.dto;

import br.mp.mpms.exemploservico.v1.model.entity.StatusSolicitacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record SolicitacaoAtendimentoResponse(
        UUID id,
        UUID tipoId,
        UUID usuarioId,
        UUID localidadeOrigemId,
        UUID localidadeDestinoId,
        LocalDateTime dataCriacao,
        StatusSolicitacao status,
        String prioridade,
        LocalDateTime dataAnalise,
        Integer numPassageiro,
        String justificativa,
        LocalDateTime dataPedido
) {
}
