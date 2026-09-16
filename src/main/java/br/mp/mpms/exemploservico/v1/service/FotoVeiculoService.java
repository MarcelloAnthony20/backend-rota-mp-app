package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.FotoVeiculoResponse;
import br.mp.mpms.exemploservico.v1.model.entity.FotoVeiculo;
import br.mp.mpms.exemploservico.v1.repository.FotoVeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FotoVeiculoService {

    private final FotoVeiculoRepository fotoVeiculoRepository;

    @Transactional(readOnly = true)
    public FotoVeiculoResponse buscarPorId(UUID id) {
        FotoVeiculo foto = fotoVeiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto de veiculo nao encontrada"));
        return toResponse(foto);
    }

    @Transactional(readOnly = true)
    public Page<FotoVeiculoResponse> listar(Pageable pageable) {
        return fotoVeiculoRepository.findAll(pageable).map(this::toResponse);
    }

    private FotoVeiculoResponse toResponse(FotoVeiculo foto) {
        return new FotoVeiculoResponse(
                foto.getId(),
                foto.getFoto(),
                foto.getFkVeiculo().getId()
        );
    }
}