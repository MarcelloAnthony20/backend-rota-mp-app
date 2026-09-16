package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.CidadeResponse;
import br.mp.mpms.exemploservico.v1.model.entity.Cidade;
import br.mp.mpms.exemploservico.v1.repository.CidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CidadeService {

    private final CidadeRepository cidadeRepository;

    @Transactional(readOnly = true)
    public CidadeResponse buscarPorId(UUID id) {
        Cidade cidade = cidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cidade nao encontrada"));
        return toResponse(cidade);
    }

    @Transactional(readOnly = true)
    public Page<CidadeResponse> listar(Pageable pageable) {
        return cidadeRepository.findAll(pageable).map(this::toResponse);
    }

    private CidadeResponse toResponse(Cidade cidade) {
        return new CidadeResponse(
                cidade.getId(),
                cidade.getNome(),
                cidade.getCodigo(),
                cidade.isAtivo()
        );
    }
}