package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.ViagemResponse;
import br.mp.mpms.rotamp.v1.model.entity.Viagem;
import br.mp.mpms.rotamp.v1.repository.ViagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ViagemService {

    private final ViagemRepository viagemRepository;

    @Transactional(readOnly = true)
    public ViagemResponse buscarPorId(UUID id) {
        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem nao encontrada"));
        return toResponse(viagem);
    }

    @Transactional(readOnly = true)
    public Page<ViagemResponse> listar(Pageable pageable) {
        return viagemRepository.findAll(pageable).map(this::toResponse);
    }

    private ViagemResponse toResponse(Viagem viagem) {
        return new ViagemResponse(
                viagem.getId(),
                viagem.getFkMotorista().getId(),
                viagem.getFkSolicitacao().getId(),
                viagem.getFkVeiculo().getId(),
                viagem.getStatus(),
                viagem.getDataInicio(),
                viagem.getDataConclusao()
        );
    }
}