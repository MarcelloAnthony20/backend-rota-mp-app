package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.MotoristaResponse;
import br.mp.mpms.rotamp.v1.model.entity.Motorista;
import br.mp.mpms.rotamp.v1.repository.MotoristaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MotoristaService {

    private final MotoristaRepository motoristaRepository;

    @Transactional(readOnly = true)
    public MotoristaResponse buscarPorId(UUID id) {
        Motorista motorista = motoristaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista nao encontrado"));
        return toResponse(motorista);
    }

    @Transactional(readOnly = true)
    public Page<MotoristaResponse> listar(Pageable pageable) {
        return motoristaRepository.findAll(pageable).map(this::toResponse);
    }

    private MotoristaResponse toResponse(Motorista motorista) {
        return new MotoristaResponse(
                motorista.getId(),
                motorista.getDisplayNome(),
                motorista.getFkLocalidade().getId(),
                motorista.getMatricula(),
                motorista.getFoto(),
                motorista.getContatoFuncional(),
                motorista.getStatus(),
                motorista.getFkUsuario().getId()
        );
    }
}