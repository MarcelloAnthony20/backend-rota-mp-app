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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PerfilTelaRepository perfilTelaRepository;

    @Mock
    private TelaRepository telaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PerfilService perfilService;

    @Test
    @DisplayName("Deve cadastrar perfil com sucesso quando dados sao validos")
    void deve_cadastrar_perfil_quando_dados_validos() {
        UUID telaId = UUID.randomUUID();
        CriarPerfilRequest request = new CriarPerfilRequest(
                "AUDITOR", "Perfil de auditoria", true, List.of(telaId));
        Perfil perfil = Perfil.builder()
                .id(UUID.randomUUID())
                .nome("AUDITOR")
                .descricao("Perfil de auditoria")
                .ativo(true)
                .build();
        Tela tela = Tela.builder().id(telaId).nome("Auditoria").rota("/auditoria").build();

        when(perfilRepository.existsByNomeIgnoreCase("AUDITOR")).thenReturn(false);
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfil);
        when(telaRepository.findByIdIn(List.of(telaId))).thenReturn(List.of(tela));
        when(perfilTelaRepository.findTelasByPerfilId(perfil.getId())).thenReturn(List.of(tela));

        PerfilResponse response = perfilService.cadastrar(request);

        assertThat(response).isNotNull();
        assertThat(response.nome()).isEqualTo("AUDITOR");
        assertThat(response.telas()).hasSize(1);
    }

    @Test
    @DisplayName("Deve lancar conflito quando nome do perfil ja existe")
    void deve_lancar_conflito_quando_nome_perfil_ja_existe() {
        CriarPerfilRequest request = new CriarPerfilRequest("GESTOR", "Descricao", true, null);
        when(perfilRepository.existsByNomeIgnoreCase("GESTOR")).thenReturn(true);

        assertThatThrownBy(() -> perfilService.cadastrar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ja existe um perfil cadastrado com este nome");
    }

    @Test
    @DisplayName("Deve lancar not found quando tela informada nao existe no cadastro")
    void deve_lancar_not_found_quando_tela_inexistente_no_cadastro() {
        UUID telaInexistente = UUID.randomUUID();
        CriarPerfilRequest request = new CriarPerfilRequest(
                "NOVO", "Desc", true, List.of(telaInexistente));
        Perfil perfil = Perfil.builder().id(UUID.randomUUID()).nome("NOVO").descricao("Desc").ativo(true).build();

        when(perfilRepository.existsByNomeIgnoreCase("NOVO")).thenReturn(false);
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfil);
        when(telaRepository.findByIdIn(List.of(telaInexistente))).thenReturn(List.of());

        assertThatThrownBy(() -> perfilService.cadastrar(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("telas informadas nao foram encontradas");
    }

    @Test
    @DisplayName("Deve buscar perfil por ID com suas telas")
    void deve_buscar_por_id_quando_perfil_existe() {
        UUID id = UUID.randomUUID();
        Perfil perfil = Perfil.builder().id(id).nome("GESTOR").descricao("Desc").ativo(true).build();
        Tela tela = Tela.builder().id(UUID.randomUUID()).nome("Home").rota("/home").build();

        when(perfilRepository.findById(id)).thenReturn(Optional.of(perfil));
        when(perfilTelaRepository.findTelasByPerfilId(id)).thenReturn(List.of(tela));

        PerfilResponse response = perfilService.buscarPorId(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("GESTOR");
        assertThat(response.telas()).hasSize(1);
    }

    @Test
    @DisplayName("Deve lancar not found quando perfil nao existe")
    void deve_lancar_not_found_quando_perfil_inexistente() {
        UUID id = UUID.randomUUID();
        when(perfilRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> perfilService.buscarPorId(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Perfil nao encontrado");
    }

    @Test
    @DisplayName("Deve listar perfis paginados com filtro por nome e status ativo")
    void deve_listar_perfis_quando_solicitado() {
        Pageable pageable = PageRequest.of(0, 10);
        Perfil perfil = Perfil.builder().id(UUID.randomUUID()).nome("GESTOR").descricao("Desc").ativo(true).build();
        when(perfilRepository.findByNomeContainingIgnoreCaseAndAtivo("GESTOR", true, pageable))
                .thenReturn(new PageImpl<>(List.of(perfil)));
        when(perfilTelaRepository.findTelasByPerfilId(perfil.getId())).thenReturn(List.of());

        Page<PerfilResponse> resultado = perfilService.listar("GESTOR", true, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("GESTOR");
    }

    @Test
    @DisplayName("Deve atualizar perfil com sucesso")
    void deve_atualizar_perfil_quando_dados_validos() {
        UUID id = UUID.randomUUID();
        AtualizarPerfilRequest request = new AtualizarPerfilRequest("GESTOR_NOVO", "Nova desc");
        Perfil perfil = Perfil.builder().id(id).nome("GESTOR").descricao("Antiga").ativo(true).build();

        when(perfilRepository.findById(id)).thenReturn(Optional.of(perfil));
        when(perfilRepository.existsByNomeIgnoreCaseAndIdNot("GESTOR_NOVO", id)).thenReturn(false);
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfil);
        when(perfilTelaRepository.findTelasByPerfilId(id)).thenReturn(List.of());

        PerfilResponse response = perfilService.atualizar(id, request);

        assertThat(response.nome()).isEqualTo("GESTOR_NOVO");
        assertThat(response.descricao()).isEqualTo("Nova desc");
    }

    @Test
    @DisplayName("Deve lancar conflito ao atualizar com nome duplicado")
    void deve_lancar_conflito_quando_atualizar_com_nome_duplicado() {
        UUID id = UUID.randomUUID();
        AtualizarPerfilRequest request = new AtualizarPerfilRequest("ADMIN", "Desc");
        Perfil perfil = Perfil.builder().id(id).nome("GESTOR").descricao("Desc").ativo(true).build();

        when(perfilRepository.findById(id)).thenReturn(Optional.of(perfil));
        when(perfilRepository.existsByNomeIgnoreCaseAndIdNot("ADMIN", id)).thenReturn(true);

        assertThatThrownBy(() -> perfilService.atualizar(id, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ja existe outro perfil cadastrado com este nome");
    }

    @Test
    @DisplayName("Deve alterar status do perfil com sucesso")
    void deve_alterar_status_quando_valido() {
        UUID id = UUID.randomUUID();
        Perfil perfil = Perfil.builder().id(id).nome("TESTE").descricao("Desc").ativo(false).build();

        when(perfilRepository.findById(id)).thenReturn(Optional.of(perfil));
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfil);
        when(perfilTelaRepository.findTelasByPerfilId(id)).thenReturn(List.of());

        PerfilResponse response = perfilService.alterarStatus(id, true);

        assertThat(response.ativo()).isTrue();
    }

    @Test
    @DisplayName("Deve lancar conflito ao inativar perfil com usuarios ativos vinculados")
    void deve_lancar_conflito_quando_inativar_perfil_com_usuarios_ativos() {
        UUID id = UUID.randomUUID();
        Perfil perfil = Perfil.builder().id(id).nome("GESTOR").descricao("Desc").ativo(true).build();

        when(perfilRepository.findById(id)).thenReturn(Optional.of(perfil));
        when(usuarioRepository.existsByFkPerfilIdAndAtivoTrue(id)).thenReturn(true);

        assertThatThrownBy(() -> perfilService.alterarStatus(id, false))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("existem usuarios ativos vinculados");
    }

    @Test
    @DisplayName("Deve configurar permissoes de telas do perfil")
    void deve_configurar_permissoes_quando_telas_validas() {
        UUID perfilId = UUID.randomUUID();
        UUID telaId = UUID.randomUUID();
        ConfigurarPermissoesPerfilRequest request = new ConfigurarPermissoesPerfilRequest(List.of(telaId));
        Perfil perfil = Perfil.builder().id(perfilId).nome("OPERADOR").descricao("Desc").ativo(true).build();
        Tela tela = Tela.builder().id(telaId).nome("Viagens").rota("/viagens").build();

        when(perfilRepository.findById(perfilId)).thenReturn(Optional.of(perfil));
        when(telaRepository.findByIdIn(List.of(telaId))).thenReturn(List.of(tela));
        when(perfilTelaRepository.findTelasByPerfilId(perfilId)).thenReturn(List.of(tela));

        PerfilResponse response = perfilService.configurarPermissoes(perfilId, request);

        assertThat(response.telas()).hasSize(1);
        verify(perfilTelaRepository).deleteByFkPerfilId(perfilId);
        verify(perfilTelaRepository).save(any(PerfilTela.class));
    }

    @Test
    @DisplayName("Deve lancar erro ao configurar permissoes com tela inexistente")
    void deve_lancar_not_found_quando_configurar_permissoes_com_tela_inexistente() {
        UUID perfilId = UUID.randomUUID();
        UUID telaId = UUID.randomUUID();
        ConfigurarPermissoesPerfilRequest request = new ConfigurarPermissoesPerfilRequest(List.of(telaId));
        Perfil perfil = Perfil.builder().id(perfilId).nome("OPERADOR").descricao("Desc").ativo(true).build();

        when(perfilRepository.findById(perfilId)).thenReturn(Optional.of(perfil));
        when(telaRepository.findByIdIn(List.of(telaId))).thenReturn(List.of());

        assertThatThrownBy(() -> perfilService.configurarPermissoes(perfilId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("telas informadas nao foram encontradas");
    }

    @Test
    @DisplayName("Deve vincular perfil ao usuario com sucesso")
    void deve_vincular_usuario_ao_perfil_quando_dados_validos() {
        UUID perfilId = UUID.randomUUID();
        UUID perfilAntigoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        Perfil perfil = Perfil.builder().id(perfilId).nome("GESTOR").descricao("Desc").ativo(true).build();
        Perfil perfilAntigo = Perfil.builder().id(perfilAntigoId).nome("SOLICITANTE").descricao("Desc").ativo(true).build();
        Usuario usuario = Usuario.builder().id(usuarioId).nome("Marcello").email("m@mpms.mp.br").fkPerfil(perfilAntigo).build();

        when(perfilRepository.findById(perfilId)).thenReturn(Optional.of(perfil));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = perfilService.vincularUsuario(perfilId, usuarioId);

        assertThat(response.id()).isEqualTo(usuarioId);
        assertThat(usuario.getFkPerfil().getId()).isEqualTo(perfilId);
    }

    @Test
    @DisplayName("Deve lancar conflito ao vincular quando usuario ja possui o perfil ativo")
    void deve_lancar_conflito_quando_usuario_ja_possui_o_perfil_ativo() {
        UUID perfilId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        Perfil perfil = Perfil.builder().id(perfilId).nome("GESTOR").descricao("Desc").ativo(true).build();
        Usuario usuario = Usuario.builder().id(usuarioId).nome("Marcello").email("m@mpms.mp.br").fkPerfil(perfil).build();

        when(perfilRepository.findById(perfilId)).thenReturn(Optional.of(perfil));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> perfilService.vincularUsuario(perfilId, usuarioId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("O usuario ja possui este perfil institucional ativo");
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar vincular perfil inativo ao usuario")
    void deve_lancar_unprocessable_entity_quando_vincular_perfil_inativo() {
        UUID perfilId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        Perfil perfil = Perfil.builder().id(perfilId).nome("GESTOR").descricao("Desc").ativo(false).build();

        when(perfilRepository.findById(perfilId)).thenReturn(Optional.of(perfil));

        assertThatThrownBy(() -> perfilService.vincularUsuario(perfilId, usuarioId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Nao e possivel vincular um perfil inativo");
    }

    @Test
    @DisplayName("Deve alterar perfil de usuario com sucesso")
    void deve_alterar_perfil_usuario_quando_valido() {
        UUID perfilId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        Perfil perfil = Perfil.builder().id(perfilId).nome("ADMINISTRADOR").descricao("Desc").ativo(true).build();
        Usuario usuario = Usuario.builder().id(usuarioId).nome("Marcello").email("m@mpms.mp.br").build();

        when(perfilRepository.findById(perfilId)).thenReturn(Optional.of(perfil));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = perfilService.alterarPerfilUsuario(perfilId, usuarioId);

        assertThat(response.id()).isEqualTo(usuarioId);
        assertThat(usuario.getFkPerfil().getId()).isEqualTo(perfilId);
    }

    @Test
    @DisplayName("Deve listar usuarios vinculados ao perfil")
    void deve_listar_usuarios_do_perfil_quando_existe() {
        UUID perfilId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Perfil perfil = Perfil.builder().id(perfilId).nome("GESTOR").descricao("Desc").ativo(true).build();
        Usuario usuario = Usuario.builder().id(UUID.randomUUID()).nome("Marcello").email("m@mpms.mp.br").fkPerfil(perfil).build();

        when(perfilRepository.findById(perfilId)).thenReturn(Optional.of(perfil));
        when(usuarioRepository.findByFkPerfilId(perfilId, pageable)).thenReturn(new PageImpl<>(List.of(usuario)));

        Page<UsuarioResponse> resultado = perfilService.listarUsuariosDoPerfil(perfilId, pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Marcello");
    }

    @Test
    @DisplayName("Deve listar telas disponiveis no sistema")
    void deve_listar_telas_disponiveis_quando_solicitado() {
        Tela tela = Tela.builder().id(UUID.randomUUID()).nome("Veiculos").rota("/veiculos").build();
        when(telaRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(tela));

        List<TelaResponse> telas = perfilService.listarTelasDisponiveis();

        assertThat(telas).hasSize(1);
        assertThat(telas.get(0).nome()).isEqualTo("Veiculos");
    }
}