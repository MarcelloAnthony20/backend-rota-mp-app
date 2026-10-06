package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.BusinessException;
import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.ConfigurarPermissoesPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.PerfilResponse;
import br.mp.mpms.rotamp.v1.model.dto.TelaResponse;
import br.mp.mpms.rotamp.v1.model.dto.UsuarioResponse;
import br.mp.mpms.rotamp.v1.model.entity.Perfil;
import br.mp.mpms.rotamp.v1.model.entity.PerfilTela;
import br.mp.mpms.rotamp.v1.model.entity.Tela;
import br.mp.mpms.rotamp.v1.model.entity.Usuario;
import br.mp.mpms.rotamp.v1.repository.PerfilRepository;
import br.mp.mpms.rotamp.v1.repository.PerfilTelaRepository;
import br.mp.mpms.rotamp.v1.repository.TelaRepository;
import br.mp.mpms.rotamp.v1.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final PerfilTelaRepository perfilTelaRepository;
    private final TelaRepository telaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PerfilResponse cadastrar(CriarPerfilRequest request) {
        if (perfilRepository.existsByNomeIgnoreCase(request.nome().trim())) {
            throw new ConflictException("Ja existe um perfil cadastrado com este nome");
        }

        Perfil perfil = Perfil.builder()
                .nome(request.nome().trim())
                .descricao(request.descricao().trim())
                .ativo(request.ativo() != null ? request.ativo() : true)
                .build();
        Perfil salvo = perfilRepository.save(perfil);

        if (request.telasIds() != null && !request.telasIds().isEmpty()) {
            salvarVinculosTelas(salvo, request.telasIds());
        }

        return toResponseComTelas(salvo);
    }

    @Transactional
    public PerfilResponse atualizar(UUID id, AtualizarPerfilRequest request) {
        Perfil perfil = buscarEntidadePorId(id);

        if (perfilRepository.existsByNomeIgnoreCaseAndIdNot(request.nome().trim(), id)) {
            throw new ConflictException("Ja existe outro perfil cadastrado com este nome");
        }

        perfil.setNome(request.nome().trim());
        perfil.setDescricao(request.descricao().trim());

        Perfil salvo = perfilRepository.save(perfil);
        return toResponseComTelas(salvo);
    }

    @Transactional
    public PerfilResponse alterarStatus(UUID id, Boolean ativo) {
        Perfil perfil = buscarEntidadePorId(id);

        if (Boolean.FALSE.equals(ativo) && usuarioRepository.existsByFkPerfilIdAndAtivoTrue(id)) {
            throw new ConflictException("Nao e possivel inativar o perfil pois existem usuarios ativos vinculados a ele");
        }

        perfil.setAtivo(ativo);
        Perfil salvo = perfilRepository.save(perfil);
        return toResponseComTelas(salvo);
    }

    @Transactional(readOnly = true)
    public PerfilResponse buscarPorId(UUID id) {
        Perfil perfil = buscarEntidadePorId(id);
        return toResponseComTelas(perfil);
    }

    @Transactional(readOnly = true)
    public Page<PerfilResponse> listar(String nome, Boolean ativo, Pageable pageable) {
        Page<Perfil> page;
        if (nome != null && !nome.isBlank() && ativo != null) {
            page = perfilRepository.findByNomeContainingIgnoreCaseAndAtivo(nome.trim(), ativo, pageable);
        } else if (nome != null && !nome.isBlank()) {
            page = perfilRepository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        } else if (ativo != null) {
            page = perfilRepository.findByAtivo(ativo, pageable);
        } else {
            page = perfilRepository.findAll(pageable);
        }
        return page.map(this::toResponseComTelas);
    }

    @Transactional(readOnly = true)
    public List<TelaResponse> consultarTelasDoPerfil(UUID perfilId) {
        buscarEntidadePorId(perfilId);
        return perfilTelaRepository.findTelasByPerfilId(perfilId).stream()
                .map(this::toTelaResponse)
                .toList();
    }

    @Transactional
    public PerfilResponse configurarPermissoes(UUID perfilId, ConfigurarPermissoesPerfilRequest request) {
        Perfil perfil = buscarEntidadePorId(perfilId);

        perfilTelaRepository.deleteByFkPerfilId(perfilId);
        if (request.telasIds() != null && !request.telasIds().isEmpty()) {
            salvarVinculosTelas(perfil, request.telasIds());
        }

        return toResponseComTelas(perfil);
    }

    @Transactional
    public UsuarioResponse vincularUsuario(UUID perfilId, UUID usuarioId) {
        Perfil perfil = buscarEntidadePorId(perfilId);
        validarPerfilAtivoParaVinculo(perfil);

        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario.getFkPerfil() != null && usuario.getFkPerfil().getId().equals(perfilId)) {
            throw new ConflictException("O usuario ja possui este perfil institucional ativo");
        }

        usuario.setFkPerfil(perfil);
        Usuario salvo = usuarioRepository.save(usuario);
        return toUsuarioResponse(salvo);
    }

    @Transactional
    public UsuarioResponse alterarPerfilUsuario(UUID perfilId, UUID usuarioId) {
        Perfil perfil = buscarEntidadePorId(perfilId);
        validarPerfilAtivoParaVinculo(perfil);

        Usuario usuario = buscarUsuarioPorId(usuarioId);
        usuario.setFkPerfil(perfil);
        Usuario salvo = usuarioRepository.save(usuario);
        return toUsuarioResponse(salvo);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarUsuariosDoPerfil(UUID perfilId, Pageable pageable) {
        buscarEntidadePorId(perfilId);
        return usuarioRepository.findByFkPerfilId(perfilId, pageable).map(this::toUsuarioResponse);
    }

    @Transactional(readOnly = true)
    public List<TelaResponse> listarTelasDisponiveis() {
        return telaRepository.findAllByOrderByNomeAsc().stream()
                .map(this::toTelaResponse)
                .toList();
    }

    private Perfil buscarEntidadePorId(UUID id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado"));
    }

    private Usuario buscarUsuarioPorId(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
    }

    private void validarPerfilAtivoParaVinculo(Perfil perfil) {
        if (!perfil.isAtivo()) {
            throw new BusinessException("Nao e possivel vincular um perfil inativo a um usuario",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private void salvarVinculosTelas(Perfil perfil, List<UUID> telasIds) {
        List<Tela> telas = telaRepository.findByIdIn(telasIds);
        if (telas.size() != telasIds.stream().distinct().count()) {
            throw new ResourceNotFoundException("Uma ou mais telas informadas nao foram encontradas");
        }

        for (Tela tela : telas) {
            PerfilTela pt = PerfilTela.builder()
                    .fkPerfil(perfil)
                    .fkTela(tela)
                    .build();
            perfilTelaRepository.save(pt);
        }
    }

    private PerfilResponse toResponseComTelas(Perfil perfil) {
        List<TelaResponse> telas = perfilTelaRepository.findTelasByPerfilId(perfil.getId()).stream()
                .map(this::toTelaResponse)
                .toList();
        return new PerfilResponse(
                perfil.getId(),
                perfil.getNome(),
                perfil.getDescricao(),
                perfil.isAtivo(),
                telas
        );
    }

    private TelaResponse toTelaResponse(Tela tela) {
        return new TelaResponse(tela.getId(), tela.getNome(), tela.getRota());
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getFkTema() != null ? usuario.getFkTema().getId() : null,
                usuario.getFkPerfil() != null ? usuario.getFkPerfil().getId() : null,
                usuario.getMailNickname(),
                usuario.isAtivo(),
                usuario.getDataCriacao(),
                usuario.getDataAtualizacao()
        );
    }
}