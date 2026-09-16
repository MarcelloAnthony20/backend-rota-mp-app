package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.PerfilResponse;
import br.mp.mpms.exemploservico.v1.model.entity.Perfil;
import br.mp.mpms.exemploservico.v1.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;

    @Transactional(readOnly = true)
    public PerfilResponse buscarPorId(UUID id) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado"));
        return toResponse(perfil);
    }

    @Transactional(readOnly = true)
    public Page<PerfilResponse> listar(Pageable pageable) {
        return perfilRepository.findAll(pageable).map(this::toResponse);
    }

    private PerfilResponse toResponse(Perfil perfil) {
        return new PerfilResponse(
                perfil.getId(),
                perfil.getNome(),
                perfil.getDescricao(),
                perfil.isAtivo()
        );
    }
}