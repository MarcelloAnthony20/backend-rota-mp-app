package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.LocalidadeResponse;
import br.mp.mpms.exemploservico.v1.model.entity.Localidade;
import br.mp.mpms.exemploservico.v1.repository.LocalidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalidadeService {

    private final LocalidadeRepository localidadeRepository;

    @Transactional(readOnly = true)
    public LocalidadeResponse buscarPorId(UUID id) {
        Localidade localidade = localidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade nao encontrada"));
        return toResponse(localidade);
    }

    @Transactional(readOnly = true)
    public Page<LocalidadeResponse> listar(Pageable pageable) {
        return localidadeRepository.findAll(pageable).map(this::toResponse);
    }

    private LocalidadeResponse toResponse(Localidade localidade) {
        return new LocalidadeResponse(
                localidade.getId(),
                localidade.getFkTipoLocalidade().getId(),
                localidade.getFkCidade().getId(),
                localidade.getNome(),
                localidade.isLocalSigiloso(),
                localidade.isAtivo()
        );
    }
}