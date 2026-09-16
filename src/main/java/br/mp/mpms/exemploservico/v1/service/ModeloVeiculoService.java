package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.ModeloVeiculoResponse;
import br.mp.mpms.exemploservico.v1.model.entity.ModeloVeiculo;
import br.mp.mpms.exemploservico.v1.repository.ModeloVeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModeloVeiculoService {

    private final ModeloVeiculoRepository modeloVeiculoRepository;

    @Transactional(readOnly = true)
    public ModeloVeiculoResponse buscarPorId(UUID id) {
        ModeloVeiculo modelo = modeloVeiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de veiculo nao encontrado"));
        return toResponse(modelo);
    }

    @Transactional(readOnly = true)
    public Page<ModeloVeiculoResponse> listar(Pageable pageable) {
        return modeloVeiculoRepository.findAll(pageable).map(this::toResponse);
    }

    private ModeloVeiculoResponse toResponse(ModeloVeiculo modelo) {
        return new ModeloVeiculoResponse(
                modelo.getId(),
                modelo.getMarca(),
                modelo.getModelo(),
                modelo.getNumPassageiro(),
                modelo.getLogo(),
                modelo.isAtivo()
        );
    }
}