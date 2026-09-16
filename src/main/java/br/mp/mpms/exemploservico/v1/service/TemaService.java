package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.TemaResponse;
import br.mp.mpms.exemploservico.v1.model.entity.Tema;
import br.mp.mpms.exemploservico.v1.repository.TemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TemaService {

    private final TemaRepository temaRepository;

    @Transactional(readOnly = true)
    public TemaResponse buscarPorId(UUID id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema nao encontrado"));
        return toResponse(tema);
    }

    @Transactional(readOnly = true)
    public Page<TemaResponse> listar(Pageable pageable) {
        return temaRepository.findAll(pageable).map(this::toResponse);
    }

    private TemaResponse toResponse(Tema tema) {
        return new TemaResponse(tema.getId(), tema.isTema());
    }
}