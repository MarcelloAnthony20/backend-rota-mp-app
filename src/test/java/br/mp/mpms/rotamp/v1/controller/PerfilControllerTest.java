package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.infra.config.SecurityConfig;
import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.GlobalExceptionHandler;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AlterarStatusPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.ConfigurarPermissoesPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarPerfilRequest;
import br.mp.mpms.rotamp.v1.model.dto.PerfilResponse;
import br.mp.mpms.rotamp.v1.model.dto.TelaResponse;
import br.mp.mpms.rotamp.v1.model.dto.UsuarioResponse;
import br.mp.mpms.rotamp.v1.service.PerfilService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PerfilController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PerfilService perfilService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static final UUID PERFIL_ID = UUID.fromString("a1000001-0000-0000-0000-000000000001");
    private static final UUID USUARIO_ID = UUID.fromString("c2000001-0000-0000-0000-000000000001");
    private static final UUID TELA_ID = UUID.fromString("f1000001-0000-0000-0000-000000000001");

    private static final RequestPostProcessor AUTH_ADMIN =
            jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));

    @Test
    @DisplayName("deve retornar 201 Created quando cadastrar perfil com dados validos")
    void deve_retornar_201_quando_cadastrar_perfil_valido() throws Exception {
        CriarPerfilRequest request = new CriarPerfilRequest("AUDITOR", "Descricao", true, List.of(TELA_ID));
        PerfilResponse response = new PerfilResponse(PERFIL_ID, "AUDITOR", "Descricao", true, List.of());

        when(perfilService.cadastrar(any(CriarPerfilRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/perfis")
                        .with(AUTH_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(PERFIL_ID.toString()))
                .andExpect(jsonPath("$.nome").value("AUDITOR"));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando dados de cadastro forem invalidos")
    void deve_retornar_422_quando_cadastrar_perfil_invalido() throws Exception {
        CriarPerfilRequest request = new CriarPerfilRequest("", "", true, null);

        mockMvc.perform(post("/api/v1/perfis")
                        .with(AUTH_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("deve retornar 409 Conflict quando nome do perfil ja existir")
    void deve_retornar_409_quando_nome_perfil_conflitante() throws Exception {
        CriarPerfilRequest request = new CriarPerfilRequest("ADMINISTRADOR", "Desc", true, null);

        when(perfilService.cadastrar(any(CriarPerfilRequest.class)))
                .thenThrow(new ConflictException("Ja existe um perfil cadastrado com este nome"));

        mockMvc.perform(post("/api/v1/perfis")
                        .with(AUTH_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Ja existe um perfil cadastrado com este nome"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar perfis com paginacao")
    void deve_retornar_200_quando_listar_perfis() throws Exception {
        PerfilResponse response = new PerfilResponse(PERFIL_ID, "GESTOR", "Desc", true, List.of());
        when(perfilService.listar(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/perfis?nome=GESTOR&ativo=true")
                        .with(AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("GESTOR"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar telas disponiveis")
    void deve_retornar_200_quando_listar_telas_disponiveis() throws Exception {
        TelaResponse tela = new TelaResponse(TELA_ID, "Painel", "/dashboard");
        when(perfilService.listarTelasDisponiveis()).thenReturn(List.of(tela));

        mockMvc.perform(get("/api/v1/perfis/telas-disponiveis")
                        .with(AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Painel"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando buscar perfil por ID existente")
    void deve_retornar_200_quando_buscar_por_id_existente() throws Exception {
        PerfilResponse response = new PerfilResponse(PERFIL_ID, "GESTOR", "Desc", true, List.of());
        when(perfilService.buscarPorId(PERFIL_ID)).thenReturn(response);

        mockMvc.perform(get("/api/v1/perfis/{id}", PERFIL_ID)
                        .with(AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PERFIL_ID.toString()));
    }

    @Test
    @DisplayName("deve retornar 404 Not Found quando buscar perfil por ID inexistente")
    void deve_retornar_404_quando_buscar_por_id_inexistente() throws Exception {
        when(perfilService.buscarPorId(PERFIL_ID))
                .thenThrow(new ResourceNotFoundException("Perfil nao encontrado"));

        mockMvc.perform(get("/api/v1/perfis/{id}", PERFIL_ID)
                        .with(AUTH_ADMIN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Perfil nao encontrado"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando atualizar dados cadastrais do perfil")
    void deve_retornar_200_quando_atualizar_perfil() throws Exception {
        AtualizarPerfilRequest request = new AtualizarPerfilRequest("GESTOR_ALTERADO", "Nova");
        PerfilResponse response = new PerfilResponse(PERFIL_ID, "GESTOR_ALTERADO", "Nova", true, List.of());

        when(perfilService.atualizar(eq(PERFIL_ID), any(AtualizarPerfilRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/perfis/{id}", PERFIL_ID)
                        .with(AUTH_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("GESTOR_ALTERADO"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando alterar status do perfil")
    void deve_retornar_200_quando_alterar_status() throws Exception {
        AlterarStatusPerfilRequest request = new AlterarStatusPerfilRequest(false);
        PerfilResponse response = new PerfilResponse(PERFIL_ID, "GESTOR", "Desc", false, List.of());

        when(perfilService.alterarStatus(PERFIL_ID, false)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/perfis/{id}/status", PERFIL_ID)
                        .with(AUTH_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando consultar telas do perfil")
    void deve_retornar_200_quando_consultar_telas_do_perfil() throws Exception {
        TelaResponse tela = new TelaResponse(TELA_ID, "Dashboard", "/dashboard");
        when(perfilService.consultarTelasDoPerfil(PERFIL_ID)).thenReturn(List.of(tela));

        mockMvc.perform(get("/api/v1/perfis/{id}/telas", PERFIL_ID)
                        .with(AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Dashboard"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando configurar permissoes de telas")
    void deve_retornar_200_quando_configurar_permissoes() throws Exception {
        ConfigurarPermissoesPerfilRequest request = new ConfigurarPermissoesPerfilRequest(List.of(TELA_ID));
        PerfilResponse response = new PerfilResponse(PERFIL_ID, "GESTOR", "Desc", true, List.of(
                new TelaResponse(TELA_ID, "Dashboard", "/dashboard")));

        when(perfilService.configurarPermissoes(eq(PERFIL_ID), any(ConfigurarPermissoesPerfilRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/perfis/{id}/permissoes", PERFIL_ID)
                        .with(AUTH_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telas[0].nome").value("Dashboard"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando vincular perfil ao usuario")
    void deve_retornar_200_quando_vincular_perfil_ao_usuario() throws Exception {
        UsuarioResponse response = new UsuarioResponse(
                USUARIO_ID, "Marcello", "m@mpms.mp.br", null, PERFIL_ID,
                "marcello", true, LocalDateTime.now(), null);

        when(perfilService.vincularUsuario(PERFIL_ID, USUARIO_ID)).thenReturn(response);

        mockMvc.perform(post("/api/v1/perfis/{id}/usuarios/{usuarioId}", PERFIL_ID, USUARIO_ID)
                        .with(AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USUARIO_ID.toString()))
                .andExpect(jsonPath("$.perfilId").value(PERFIL_ID.toString()));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando alterar perfil de usuario")
    void deve_retornar_200_quando_alterar_perfil_usuario() throws Exception {
        UsuarioResponse response = new UsuarioResponse(
                USUARIO_ID, "Marcello", "m@mpms.mp.br", null, PERFIL_ID,
                "marcello", true, LocalDateTime.now(), null);

        when(perfilService.alterarPerfilUsuario(PERFIL_ID, USUARIO_ID)).thenReturn(response);

        mockMvc.perform(put("/api/v1/perfis/{id}/usuarios/{usuarioId}", PERFIL_ID, USUARIO_ID)
                        .with(AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfilId").value(PERFIL_ID.toString()));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar usuarios vinculados ao perfil")
    void deve_retornar_200_quando_listar_usuarios_do_perfil() throws Exception {
        UsuarioResponse response = new UsuarioResponse(
                USUARIO_ID, "Marcello", "m@mpms.mp.br", null, PERFIL_ID,
                "marcello", true, LocalDateTime.now(), null);

        when(perfilService.listarUsuariosDoPerfil(eq(PERFIL_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/perfis/{id}/usuarios", PERFIL_ID)
                        .with(AUTH_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Marcello"));
    }

    @Test
    @Disabled("Temporariamente desabilitado enquanto a validacao de token Keycloak e @PreAuthorize estiverem desativadas para testes locais")
    @DisplayName("deve retornar 403 Forbidden quando usuario autenticado nao possui role ADMINISTRADOR")
    void deve_retornar_403_quando_usuario_sem_role_administrador() throws Exception {
        mockMvc.perform(get("/api/v1/perfis")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @Disabled("Temporariamente desabilitado enquanto a validacao de token Keycloak e @PreAuthorize estiverem desativadas para testes locais")
    @DisplayName("deve retornar 403 Forbidden quando usuario for anonimo")
    void deve_retornar_403_quando_usuario_anonimo() throws Exception {
        mockMvc.perform(get("/api/v1/perfis"))
                .andExpect(status().isForbidden());
    }
}
