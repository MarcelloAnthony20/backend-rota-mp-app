package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarCidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.CidadeResponse;
import br.mp.mpms.rotamp.v1.model.dto.CriarCidadeRequest;
import br.mp.mpms.rotamp.v1.model.entity.Cidade;
import br.mp.mpms.rotamp.v1.repository.CidadeRepository;
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

    @Transactional
    public CidadeResponse cadastrar(CriarCidadeRequest request) {
        if (cidadeRepository.existsByCodigo(request.codigo())) {
            throw new ConflictException("Codigo de cidade ja cadastrado: " + request.codigo());
        }

        Cidade cidade = Cidade.builder()
                .nome(request.nome())
                .codigo(request.codigo())
                .ativo(request.ativo() != null ? request.ativo() : true)
                .build();

        Cidade salva = cidadeRepository.save(cidade);
        return toResponse(salva);
    }

    @Transactional
    public CidadeResponse alterar(UUID id, AtualizarCidadeRequest request) {
        Cidade cidade = buscarEntidadePorId(id);

        if (cidadeRepository.existsByCodigoAndIdNot(request.codigo(), id)) {
            throw new ConflictException("Codigo de cidade ja em uso por outra cidade: " + request.codigo());
        }

        cidade.atualizarDados(request.nome(), request.codigo());

        Cidade salva = cidadeRepository.save(cidade);
        return toResponse(salva);
    }

    @Transactional
    public void inativar(UUID id) {
        Cidade cidade = buscarEntidadePorId(id);
        cidade.inativar();
        cidadeRepository.save(cidade);
    }

    @Transactional
    public CidadeResponse ativar(UUID id) {
        Cidade cidade = buscarEntidadePorId(id);
        cidade.ativar();
        Cidade salva = cidadeRepository.save(cidade);
        return toResponse(salva);
    }

    @Transactional(readOnly = true)
    public CidadeResponse buscarPorId(UUID id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<CidadeResponse> buscarPorNome(String nome, Pageable pageable) {
        Page<Cidade> cidades = cidadeRepository.findByNomeContainingIgnoreCase(nome, pageable);
        if (cidades.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma cidade encontrada para o criterio informado");
        }
        return cidades.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CidadeResponse> listar(Pageable pageable) {
        return cidadeRepository.findAll(pageable).map(this::toResponse);
    }

    private Cidade buscarEntidadePorId(UUID id) {
        return cidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cidade nao encontrada"));
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