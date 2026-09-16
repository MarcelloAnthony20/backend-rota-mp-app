package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.TipoResponse;
import br.mp.mpms.exemploservico.v1.model.entity.Tipo;
import br.mp.mpms.exemploservico.v1.repository.TipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TipoService {

    private final TipoRepository tipoRepository;

    @Transactional(readOnly = true)
    public TipoResponse buscarPorId(UUID id) {
        Tipo tipo = tipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo nao encontrado"));
        return toResponse(tipo);
    }

    @Transactional(readOnly = true)
    public Page<TipoResponse> listar(Pageable pageable) {
        return tipoRepository.findAll(pageable).map(this::toResponse);
    }

    private TipoResponse toResponse(Tipo tipo) {
        return new TipoResponse(
                tipo.getId(),
                tipo.getNome(),
                tipo.getDescricao(),
                tipo.isAtivo()
        );
    }
}