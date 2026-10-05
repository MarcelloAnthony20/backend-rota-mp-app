package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.infra.config.SecurityConfig;
import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.GlobalExceptionHandler;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarTipoLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarTipoLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.TipoLocalidadeResponse;
import br.mp.mpms.rotamp.v1.service.TipoLocalidadeService;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TipoLocalidadeController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class TipoLocalidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TipoLocalidadeService tipoLocalidadeService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static final UUID ID_PADRAO = UUID.fromString("b1000001-0000-0000-0000-000000000001");

    // --- CADASTRO (POST) ---

    @Test
    @DisplayName("deve retornar 201 Created quando cadastrar tipo de localidade com perfil gestor e dados validos")
    void deve_retornar201Created_quando_cadastrarTipoLocalidadeComPerfilGestor() throws Exception {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria de Justiça", true);
        TipoLocalidadeResponse response = new TipoLocalidadeResponse(ID_PADRAO, "PROJUS", "Promotoria de Justiça", true);

        when(tipoLocalidadeService.cadastrar(any(CriarTipoLocalidadeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(ID_PADRAO.toString()))
                .andExpect(jsonPath("$.codigo").value("PROJUS"))
                .andExpect(jsonPath("$.tipoLocalidade").value("Promotoria de Justiça"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar tipo de localidade sem codigo")
    void deve_retornar422UnprocessableEntity_quando_cadastrarTipoLocalidadeSemCodigo() throws Exception {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("", "Promotoria de Justiça", true);

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validacao falhou"))
                .andExpect(jsonPath("$.errors.codigo").exists());
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar tipo de localidade sem descricao")
    void deve_retornar422UnprocessableEntity_quando_cadastrarTipoLocalidadeSemDescricao() throws Exception {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROCURADORIA", "", true);

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validacao falhou"))
                .andExpect(jsonPath("$.errors.tipoLocalidade").exists());
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar tipo com codigo acima de 50 caracteres")
    void deve_retornar422UnprocessableEntity_quando_cadastrarTipoLocalidadeComCodigoAcimaDe50Caracteres() throws Exception {
        String codigoLongo = "C".repeat(51);
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest(codigoLongo, "Promotoria de Justiça", true);

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.codigo").exists());
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar tipo com descricao acima de 100 caracteres")
    void deve_retornar422UnprocessableEntity_quando_cadastrarTipoLocalidadeComDescricaoAcimaDe100Caracteres() throws Exception {
        String descricaoLonga = "D".repeat(101);
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", descricaoLonga, true);

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.tipoLocalidade").exists());
    }

    @Test
    @DisplayName("deve retornar 201 Created quando cadastrar tipo com codigo no limite de 50 caracteres")
    void deve_retornar201Created_quando_cadastrarTipoLocalidadeComCodigoNoLimite50Caracteres() throws Exception {
        String codigo50 = "C".repeat(50);
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest(codigo50, "Promotoria de Justiça", true);
        TipoLocalidadeResponse response = new TipoLocalidadeResponse(ID_PADRAO, codigo50, "Promotoria de Justiça", true);

        when(tipoLocalidadeService.cadastrar(any(CriarTipoLocalidadeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(codigo50));
    }

    @Test
    @DisplayName("deve retornar 409 Conflict quando cadastrar com codigo duplicado")
    void deve_retornar409Conflict_quando_cadastrarComCodigoDuplicado() throws Exception {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria", true);

        when(tipoLocalidadeService.cadastrar(any(CriarTipoLocalidadeRequest.class)))
                .thenThrow(new ConflictException("Codigo de tipo de localidade ja cadastrado: PROJUS"));

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"))
                .andExpect(jsonPath("$.detail").value("Codigo de tipo de localidade ja cadastrado: PROJUS"));
    }

    @Test
    @Disabled("Temporariamente desabilitado enquanto a validacao de token Keycloak e @PreAuthorize estiverem desativadas para testes locais")
    @DisplayName("deve retornar 403 Forbidden quando cadastrar tipo sem perfil gestor")
    void deve_retornar403Forbidden_quando_cadastrarTipoLocalidadeSemPerfilGestor() throws Exception {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria", true);

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Disabled("Temporariamente desabilitado enquanto a validacao de token Keycloak e @PreAuthorize estiverem desativadas para testes locais")
    @DisplayName("deve retornar 401 Unauthorized quando cadastrar tipo sem autenticacao")
    @WithAnonymousUser
    void deve_retornar401Unauthorized_quando_cadastrarTipoLocalidadeSemAutenticacao() throws Exception {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria", true);

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // --- CONSULTA (GET) ---

    @Test
    @DisplayName("deve retornar 200 OK quando buscar tipo por ID existente")
    void deve_retornar200OK_quando_buscarTipoLocalidadePorIdExistente() throws Exception {
        TipoLocalidadeResponse response = new TipoLocalidadeResponse(ID_PADRAO, "PROJUS", "Promotoria de Justiça", true);

        when(tipoLocalidadeService.buscarPorId(ID_PADRAO)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tipos-localidade/{id}", ID_PADRAO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_PADRAO.toString()))
                .andExpect(jsonPath("$.codigo").value("PROJUS"))
                .andExpect(jsonPath("$.tipoLocalidade").value("Promotoria de Justiça"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando buscar tipo por ID inexistente")
    void deve_retornar404NotFound_quando_buscarTipoLocalidadePorIdInexistente() throws Exception {
        UUID idInexistente = UUID.fromString("b1099999-9999-9999-9999-999999999999");
        when(tipoLocalidadeService.buscarPorId(idInexistente))
                .thenThrow(new ResourceNotFoundException("Tipo de localidade nao encontrado"));

        mockMvc.perform(get("/api/v1/tipos-localidade/{id}", idInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"))
                .andExpect(jsonPath("$.detail").value("Tipo de localidade nao encontrado"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar tipos de localidade")
    void deve_retornar200OK_quando_listarTiposLocalidade() throws Exception {
        TipoLocalidadeResponse r1 = new TipoLocalidadeResponse(ID_PADRAO, "PROJUS", "Promotoria de Justiça", true);
        TipoLocalidadeResponse r2 = new TipoLocalidadeResponse(UUID.fromString("b1000002-0000-0000-0000-000000000002"), "SEDEADM", "Sede Administrativa", false);

        when(tipoLocalidadeService.listar(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(r1, r2)));

        mockMvc.perform(get("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @Disabled("Temporariamente desabilitado enquanto a validacao de token Keycloak e @PreAuthorize estiverem desativadas para testes locais")
    @DisplayName("deve retornar 401 Unauthorized quando buscar tipo por ID sem autenticacao")
    @WithAnonymousUser
    void deve_retornar401Unauthorized_quando_buscarTipoLocalidadePorIdSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/v1/tipos-localidade/{id}", ID_PADRAO))
                .andExpect(status().isUnauthorized());
    }

    // --- ALTERAÇÃO (PUT) ---

    @Test
    @DisplayName("deve retornar 200 OK quando alterar tipo de localidade com perfil gestor")
    void deve_retornar200OK_quando_alterarTipoLocalidadeComPerfilGestor() throws Exception {
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("PROJUS_ALT", "Promotoria Alterada");
        TipoLocalidadeResponse response = new TipoLocalidadeResponse(ID_PADRAO, "PROJUS_ALT", "Promotoria Alterada", true);

        when(tipoLocalidadeService.alterar(eq(ID_PADRAO), any(AtualizarTipoLocalidadeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/tipos-localidade/{id}", ID_PADRAO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("PROJUS_ALT"))
                .andExpect(jsonPath("$.tipoLocalidade").value("Promotoria Alterada"));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando alterar tipo de localidade sem codigo")
    void deve_retornar422UnprocessableEntity_quando_alterarTipoLocalidadeSemCodigo() throws Exception {
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("", "Promotoria Alterada");

        mockMvc.perform(put("/api/v1/tipos-localidade/{id}", ID_PADRAO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validacao falhou"))
                .andExpect(jsonPath("$.errors.codigo").exists());
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando alterar tipo com descricao acima de 100 caracteres")
    void deve_retornar422UnprocessableEntity_quando_alterarTipoLocalidadeComDescricaoAcimaDe100Caracteres() throws Exception {
        String descricaoLonga = "D".repeat(101);
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("PROJUS", descricaoLonga);

        mockMvc.perform(put("/api/v1/tipos-localidade/{id}", ID_PADRAO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.tipoLocalidade").exists());
    }

    @Test
    @DisplayName("deve retornar 409 Conflict quando alterar com codigo duplicado")
    void deve_retornar409Conflict_quando_alterarComCodigoDuplicado() throws Exception {
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("SEDEADM", "Promotoria de Justiça");

        when(tipoLocalidadeService.alterar(eq(ID_PADRAO), any(AtualizarTipoLocalidadeRequest.class)))
                .thenThrow(new ConflictException("Codigo de tipo de localidade ja em uso por outro registro: SEDEADM"));

        mockMvc.perform(put("/api/v1/tipos-localidade/{id}", ID_PADRAO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"))
                .andExpect(jsonPath("$.detail").value("Codigo de tipo de localidade ja em uso por outro registro: SEDEADM"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando alterar tipo inexistente")
    void deve_retornar404NotFound_quando_alterarTipoLocalidadeInexistente() throws Exception {
        UUID idInexistente = UUID.fromString("b1099999-9999-9999-9999-999999999999");
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("FANTASMA", "Tipo Fantasma");

        when(tipoLocalidadeService.alterar(eq(idInexistente), any(AtualizarTipoLocalidadeRequest.class)))
                .thenThrow(new ResourceNotFoundException("Tipo de localidade nao encontrado"));

        mockMvc.perform(put("/api/v1/tipos-localidade/{id}", idInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }

    @Test
    @Disabled("Temporariamente desabilitado enquanto a validacao de token Keycloak e @PreAuthorize estiverem desativadas para testes locais")
    @DisplayName("deve retornar 403 Forbidden quando alterar tipo sem perfil gestor")
    void deve_retornar403Forbidden_quando_alterarTipoLocalidadeSemPerfilGestor() throws Exception {
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("PROJUS", "Tentativa");

        mockMvc.perform(put("/api/v1/tipos-localidade/{id}", ID_PADRAO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // --- INATIVAÇÃO / DELEÇÃO (DELETE) ---

    @Test
    @DisplayName("deve retornar 204 No Content sem corpo quando inativar tipo de localidade com perfil gestor")
    void deve_retornar204NoContentSemCorpo_quando_inativarTipoLocalidadeComPerfilGestor() throws Exception {
        doNothing().when(tipoLocalidadeService).inativar(ID_PADRAO);

        mockMvc.perform(delete("/api/v1/tipos-localidade/{id}", ID_PADRAO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(tipoLocalidadeService).inativar(ID_PADRAO);
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando inativar tipo inexistente")
    void deve_retornar404NotFound_quando_inativarTipoLocalidadeInexistente() throws Exception {
        UUID idInexistente = UUID.fromString("b1099999-9999-9999-9999-999999999999");
        doThrow(new ResourceNotFoundException("Tipo de localidade nao encontrado")).when(tipoLocalidadeService).inativar(idInexistente);

        mockMvc.perform(delete("/api/v1/tipos-localidade/{id}", idInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }

    @Test
    @Disabled("Temporariamente desabilitado enquanto a validacao de token Keycloak e @PreAuthorize estiverem desativadas para testes locais")
    @DisplayName("deve retornar 403 Forbidden quando inativar tipo sem perfil gestor")
    void deve_retornar403Forbidden_quando_inativarTipoLocalidadeSemPerfilGestor() throws Exception {
        mockMvc.perform(delete("/api/v1/tipos-localidade/{id}", ID_PADRAO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @Disabled("Temporariamente desabilitado enquanto a validacao de token Keycloak e @PreAuthorize estiverem desativadas para testes locais")
    @DisplayName("deve retornar 401 Unauthorized quando inativar tipo sem autenticacao")
    @WithAnonymousUser
    void deve_retornar401Unauthorized_quando_inativarTipoLocalidadeSemAutenticacao() throws Exception {
        mockMvc.perform(delete("/api/v1/tipos-localidade/{id}", ID_PADRAO)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // --- ATIVAÇÃO (PATCH) ---

    @Test
    @DisplayName("deve retornar 200 OK quando ativar tipo de localidade com perfil gestor")
    void deve_retornar200OK_quando_ativarTipoLocalidadeComPerfilGestor() throws Exception {
        TipoLocalidadeResponse response = new TipoLocalidadeResponse(ID_PADRAO, "PROJUS", "Promotoria de Justiça", true);

        when(tipoLocalidadeService.ativar(ID_PADRAO)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/tipos-localidade/{id}/ativar", ID_PADRAO)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando ativar tipo inexistente")
    void deve_retornar404NotFound_quando_ativarTipoLocalidadeInexistente() throws Exception {
        UUID idInexistente = UUID.fromString("b1099999-9999-9999-9999-999999999999");
        when(tipoLocalidadeService.ativar(idInexistente))
                .thenThrow(new ResourceNotFoundException("Tipo de localidade nao encontrado"));

        mockMvc.perform(patch("/api/v1/tipos-localidade/{id}/ativar", idInexistente)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }
}
