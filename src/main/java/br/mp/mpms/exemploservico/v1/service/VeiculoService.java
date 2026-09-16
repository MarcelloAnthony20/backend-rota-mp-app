package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.VeiculoResponse;
import br.mp.mpms.exemploservico.v1.model.entity.Veiculo;
import br.mp.mpms.exemploservico.v1.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Transactional(readOnly = true)
    public VeiculoResponse buscarPorId(UUID id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado"));
        return toResponse(veiculo);
    }

    @Transactional(readOnly = true)
    public Page<VeiculoResponse> listar(Pageable pageable) {
        return veiculoRepository.findAll(pageable).map(this::toResponse);
    }

    private VeiculoResponse toResponse(Veiculo veiculo) {
        return new VeiculoResponse(
                veiculo.getId(),
                veiculo.getFkLocalidade().getId(),
                veiculo.getFkModelo().getId(),
                veiculo.getPlaca(),
                veiculo.getAno(),
                veiculo.isAtivo()
        );
    }
}