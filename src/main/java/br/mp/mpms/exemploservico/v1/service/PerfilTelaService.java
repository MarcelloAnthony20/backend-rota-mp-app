package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.PerfilTelaResponse;
import br.mp.mpms.exemploservico.v1.model.entity.PerfilTela;
import br.mp.mpms.exemploservico.v1.repository.PerfilTelaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PerfilTelaService {

    private final PerfilTelaRepository perfilTelaRepository;

    @Transactional(readOnly = true)
    public PerfilTelaResponse buscarPorId(UUID id) {
        PerfilTela perfilTela = perfilTelaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vinculo de perfil e tela nao encontrado"));
        return toResponse(perfilTela);
    }

    @Transactional(readOnly = true)
    public Page<PerfilTelaResponse> listar(Pageable pageable) {
        return perfilTelaRepository.findAll(pageable).map(this::toResponse);
    }

    private PerfilTelaResponse toResponse(PerfilTela perfilTela) {
        return new PerfilTelaResponse(
                perfilTela.getId(),
                perfilTela.getFkPerfil().getId(),
                perfilTela.getFkTela().getId()
        );
    }
}