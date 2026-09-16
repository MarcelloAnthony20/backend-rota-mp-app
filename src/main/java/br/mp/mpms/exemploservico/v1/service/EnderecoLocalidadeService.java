package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.EnderecoLocalidadeResponse;
import br.mp.mpms.exemploservico.v1.model.entity.EnderecoLocalidade;
import br.mp.mpms.exemploservico.v1.repository.EnderecoLocalidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnderecoLocalidadeService {

    private final EnderecoLocalidadeRepository enderecoLocalidadeRepository;

    @Transactional(readOnly = true)
    public EnderecoLocalidadeResponse buscarPorId(UUID id) {
        EnderecoLocalidade endereco = enderecoLocalidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco de localidade nao encontrado"));
        return toResponse(endereco);
    }

    @Transactional(readOnly = true)
    public Page<EnderecoLocalidadeResponse> listar(Pageable pageable) {
        return enderecoLocalidadeRepository.findAll(pageable).map(this::toResponse);
    }

    private EnderecoLocalidadeResponse toResponse(EnderecoLocalidade endereco) {
        return new EnderecoLocalidadeResponse(
                endereco.getId(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getCep(),
                endereco.getBairro(),
                endereco.getComplemento(),
                endereco.getFkCidade().getId(),
                endereco.getFkLocalidade().getId()
        );
    }
}