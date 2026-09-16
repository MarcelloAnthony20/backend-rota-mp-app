package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.TipoLocalidadeResponse;
import br.mp.mpms.exemploservico.v1.model.entity.TipoLocalidade;
import br.mp.mpms.exemploservico.v1.repository.TipoLocalidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TipoLocalidadeService {

    private final TipoLocalidadeRepository tipoLocalidadeRepository;

    @Transactional(readOnly = true)
    public TipoLocalidadeResponse buscarPorId(UUID id) {
        TipoLocalidade tipoLocalidade = tipoLocalidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de localidade nao encontrado"));
        return toResponse(tipoLocalidade);
    }

    @Transactional(readOnly = true)
    public Page<TipoLocalidadeResponse> listar(Pageable pageable) {
        return tipoLocalidadeRepository.findAll(pageable).map(this::toResponse);
    }

    private TipoLocalidadeResponse toResponse(TipoLocalidade tipoLocalidade) {
        return new TipoLocalidadeResponse(
                tipoLocalidade.getId(),
                tipoLocalidade.getCodigo(),
                tipoLocalidade.getTipoLocalidade(),
                tipoLocalidade.isAtivo()
        );
    }
}