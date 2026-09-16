package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.SolicitacaoAtendimentoResponse;
import br.mp.mpms.exemploservico.v1.model.entity.SolicitacaoAtendimento;
import br.mp.mpms.exemploservico.v1.repository.SolicitacaoAtendimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolicitacaoAtendimentoService {

    private final SolicitacaoAtendimentoRepository solicitacaoAtendimentoRepository;

    @Transactional(readOnly = true)
    public SolicitacaoAtendimentoResponse buscarPorId(UUID id) {
        SolicitacaoAtendimento solicitacao = solicitacaoAtendimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitacao de atendimento nao encontrada"));
        return toResponse(solicitacao);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoAtendimentoResponse> listar(Pageable pageable) {
        return solicitacaoAtendimentoRepository.findAll(pageable).map(this::toResponse);
    }

    private SolicitacaoAtendimentoResponse toResponse(SolicitacaoAtendimento solicitacao) {
        return new SolicitacaoAtendimentoResponse(
                solicitacao.getId(),
                solicitacao.getFkTipo().getId(),
                solicitacao.getFkUsuario().getId(),
                solicitacao.getFkLocalidadeOrigem().getId(),
                solicitacao.getFkLocalidadeDestino().getId(),
                solicitacao.getDataCriacao(),
                solicitacao.getStatus(),
                solicitacao.getPrioridade(),
                solicitacao.getDataAnalise(),
                solicitacao.getNumPassageiro(),
                solicitacao.getJustificativa(),
                solicitacao.getDataPedido()
        );
    }
}