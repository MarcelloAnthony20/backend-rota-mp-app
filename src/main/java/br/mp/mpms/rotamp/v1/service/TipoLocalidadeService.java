package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarTipoLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarTipoLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.TipoLocalidadeResponse;
import br.mp.mpms.rotamp.v1.model.entity.TipoLocalidade;
import br.mp.mpms.rotamp.v1.repository.TipoLocalidadeRepository;
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

    @Transactional
    public TipoLocalidadeResponse cadastrar(CriarTipoLocalidadeRequest request) {
        if (tipoLocalidadeRepository.existsByCodigo(request.codigo())) {
            throw new ConflictException("Codigo de tipo de localidade ja cadastrado: " + request.codigo());
        }

        TipoLocalidade tipo = TipoLocalidade.builder()
                .codigo(request.codigo())
                .tipoLocalidade(request.tipoLocalidade())
                .ativo(request.ativo() != null ? request.ativo() : true)
                .build();

        TipoLocalidade salvo = tipoLocalidadeRepository.save(tipo);
        return toResponse(salvo);
    }

    @Transactional
    public TipoLocalidadeResponse alterar(UUID id, AtualizarTipoLocalidadeRequest request) {
        TipoLocalidade tipo = buscarEntidadePorId(id);

        if (tipoLocalidadeRepository.existsByCodigoAndIdNot(request.codigo(), id)) {
            throw new ConflictException("Codigo de tipo de localidade ja em uso por outro registro: " + request.codigo());
        }

        tipo.setCodigo(request.codigo());
        tipo.setTipoLocalidade(request.tipoLocalidade());

        TipoLocalidade salvo = tipoLocalidadeRepository.save(tipo);
        return toResponse(salvo);
    }

    @Transactional
    public void inativar(UUID id) {
        TipoLocalidade tipo = buscarEntidadePorId(id);
        tipo.setAtivo(false);
        tipoLocalidadeRepository.save(tipo);
    }

    @Transactional
    public TipoLocalidadeResponse ativar(UUID id) {
        TipoLocalidade tipo = buscarEntidadePorId(id);
        tipo.setAtivo(true);
        TipoLocalidade salvo = tipoLocalidadeRepository.save(tipo);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public TipoLocalidadeResponse buscarPorId(UUID id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<TipoLocalidadeResponse> listar(Pageable pageable) {
        return tipoLocalidadeRepository.findAll(pageable).map(this::toResponse);
    }

    private TipoLocalidade buscarEntidadePorId(UUID id) {
        return tipoLocalidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de localidade nao encontrado"));
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