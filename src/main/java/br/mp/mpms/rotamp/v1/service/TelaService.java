package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.TelaResponse;
import br.mp.mpms.rotamp.v1.model.entity.Tela;
import br.mp.mpms.rotamp.v1.repository.TelaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelaService {

    private final TelaRepository telaRepository;

    @Transactional(readOnly = true)
    public TelaResponse buscarPorId(UUID id) {
        Tela tela = telaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tela nao encontrada"));
        return toResponse(tela);
    }

    @Transactional(readOnly = true)
    public Page<TelaResponse> listar(Pageable pageable) {
        return telaRepository.findAll(pageable).map(this::toResponse);
    }

    private TelaResponse toResponse(Tela tela) {
        return new TelaResponse(tela.getId(), tela.getNome(), tela.getRota());
    }
}