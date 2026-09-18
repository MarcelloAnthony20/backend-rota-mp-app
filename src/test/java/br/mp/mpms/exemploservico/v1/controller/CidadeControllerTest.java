package br.mp.mpms.exemploservico.v1.controller;

import br.mp.mpms.exemploservico.infra.config.SecurityConfig;
import br.mp.mpms.exemploservico.infra.exception.ConflictException;
import br.mp.mpms.exemploservico.infra.exception.GlobalExceptionHandler;
import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.AtualizarCidadeRequest;
import br.mp.mpms.exemploservico.v1.model.dto.CidadeResponse;
import br.mp.mpms.exemploservico.v1.model.dto.CriarCidadeRequest;
import br.mp.mpms.exemploservico.v1.service.CidadeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CidadeController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class CidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CidadeService cidadeService;

    // --- CADASTRO (POST) ---

    @Test
    @DisplayName("deve retornar 201 Created quando cadastrar cidade com perfil gestor e dados validos")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar201Created_quando_cadastrarCidadeComPerfilGestor() throws Exception {
        CriarCidadeRequest request = new CriarCidadeRequest("Campo Grande", "5002704", true);
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        CidadeResponse response = new CidadeResponse(id, "Campo Grande", "5002704", true);

        when(cidadeService.cadastrar(any(CriarCidadeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cidades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Campo Grande"))
                .andExpect(jsonPath("$.codigo").value("5002704"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar cidade sem nome")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar422UnprocessableEntity_quando_cadastrarCidadeSemNome() throws Exception {
        CriarCidadeRequest request = new CriarCidadeRequest("", "5002704", true);

        mockMvc.perform(post("/api/v1/cidades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validacao falhou"))
                .andExpect(jsonPath("$.errors.nome").exists());
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar cidade com codigo acima de 7 caracteres")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar422UnprocessableEntity_quando_cadastrarCidadeComCodigoAcimaDe7Caracteres() throws Exception {
        CriarCidadeRequest request = new CriarCidadeRequest("Tres Lagoas", "50027041", true);

        mockMvc.perform(post("/api/v1/cidades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validacao falhou"))
                .andExpect(jsonPath("$.errors.codigo").exists());
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar cidade com nome acima de 200 caracteres")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar422UnprocessableEntity_quando_cadastrarCidadeComNomeAcimaDe200Caracteres() throws Exception {
        String nomeLongo = "A".repeat(201);
        CriarCidadeRequest request = new CriarCidadeRequest(nomeLongo, "5008306", true);

        mockMvc.perform(post("/api/v1/cidades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.nome").exists());
    }

    @Test
    @DisplayName("deve retornar 409 Conflict quando cadastrar com codigo duplicado")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar409Conflict_quando_cadastrarComCodigoDuplicado() throws Exception {
        CriarCidadeRequest request = new CriarCidadeRequest("Nova Cidade", "5002704", true);

        when(cidadeService.cadastrar(any(CriarCidadeRequest.class)))
                .thenThrow(new ConflictException("Codigo de cidade ja cadastrado: 5002704"));

        mockMvc.perform(post("/api/v1/cidades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"))
                .andExpect(jsonPath("$.detail").value("Codigo de cidade ja cadastrado: 5002704"));
    }

    @Test
    @DisplayName("deve retornar 403 Forbidden quando cadastrar cidade sem perfil gestor")
    @WithMockUser(roles = "OPERADOR")
    void deve_retornar403Forbidden_quando_cadastrarCidadeSemPerfilGestor() throws Exception {
        CriarCidadeRequest request = new CriarCidadeRequest("Aquidauana", "5001102", true);

        mockMvc.perform(post("/api/v1/cidades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deve retornar 401 Unauthorized quando cadastrar cidade sem autenticacao")
    @WithAnonymousUser
    void deve_retornar401Unauthorized_quando_cadastrarCidadeSemAutenticacao() throws Exception {
        CriarCidadeRequest request = new CriarCidadeRequest("Aquidauana", "5001102", true);

        mockMvc.perform(post("/api/v1/cidades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // --- CONSULTA (GET) ---

    @Test
    @DisplayName("deve retornar 200 OK quando buscar cidade por ID existente")
    @WithMockUser
    void deve_retornar200OK_quando_buscarCidadePorIdExistente() throws Exception {
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        CidadeResponse response = new CidadeResponse(id, "Campo Grande", "5002704", true);

        when(cidadeService.buscarPorId(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/cidades/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Campo Grande"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando buscar cidade por ID inexistente")
    @WithMockUser
    void deve_retornar404NotFound_quando_buscarCidadePorIdInexistente() throws Exception {
        UUID id = UUID.fromString("c1d99999-9999-9999-9999-999999999999");

        when(cidadeService.buscarPorId(id))
                .thenThrow(new ResourceNotFoundException("Cidade nao encontrada"));

        mockMvc.perform(get("/api/v1/cidades/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"))
                .andExpect(jsonPath("$.detail").value("Cidade nao encontrada"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando buscar por nome sem correspondencia")
    @WithMockUser
    void deve_retornar404NotFound_quando_buscarPorNomeSemCorrespondencia() throws Exception {
        when(cidadeService.buscarPorNome(eq("InexistenteXYZ"), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Nenhuma cidade encontrada para o criterio informado"));

        mockMvc.perform(get("/api/v1/cidades").param("nome", "InexistenteXYZ"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando buscar por nome com correspondencia")
    @WithMockUser
    void deve_retornar200OK_quando_buscarPorNomeComCorrespondencia() throws Exception {
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        CidadeResponse response = new CidadeResponse(id, "Campo Grande", "5002704", true);

        when(cidadeService.buscarPorNome(eq("Campo"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/cidades").param("nome", "Campo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Campo Grande"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar cidades")
    @WithMockUser
    void deve_retornar200OK_quando_listarCidades() throws Exception {
        CidadeResponse c1 = new CidadeResponse(UUID.randomUUID(), "Campo Grande Alterada", "5002704", true);
        CidadeResponse c2 = new CidadeResponse(UUID.randomUUID(), "Dourados", "5003702", false);

        when(cidadeService.listar(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(c1, c2)));

        mockMvc.perform(get("/api/v1/cidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    // --- ALTERAÇÃO (PUT) ---

    @Test
    @DisplayName("deve retornar 200 OK quando alterar cidade com perfil gestor")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar200OK_quando_alterarCidadeComPerfilGestor() throws Exception {
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        AtualizarCidadeRequest request = new AtualizarCidadeRequest("Campo Grande Alterada", "5002704", true);
        CidadeResponse response = new CidadeResponse(id, "Campo Grande Alterada", "5002704", true);

        when(cidadeService.alterar(eq(id), any(AtualizarCidadeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/cidades/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Campo Grande Alterada"));
    }

    @Test
    @DisplayName("deve retornar 409 Conflict quando alterar com codigo duplicado")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar409Conflict_quando_alterarComCodigoDuplicado() throws Exception {
        UUID id = UUID.fromString("c1d00007-0000-0000-0000-000000000007");
        AtualizarCidadeRequest request = new AtualizarCidadeRequest("Corumba", "5002704", true);

        when(cidadeService.alterar(eq(id), any(AtualizarCidadeRequest.class)))
                .thenThrow(new ConflictException("Codigo de cidade ja em uso por outra cidade: 5002704"));

        mockMvc.perform(put("/api/v1/cidades/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando alterar cidade inexistente")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar404NotFound_quando_alterarCidadeInexistente() throws Exception {
        UUID id = UUID.fromString("c1d99999-9999-9999-9999-999999999999");
        AtualizarCidadeRequest request = new AtualizarCidadeRequest("Fantasma", "5009999", true);

        when(cidadeService.alterar(eq(id), any(AtualizarCidadeRequest.class)))
                .thenThrow(new ResourceNotFoundException("Cidade nao encontrada"));

        mockMvc.perform(put("/api/v1/cidades/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deve retornar 403 Forbidden quando alterar cidade sem perfil gestor")
    @WithMockUser(roles = "OPERADOR")
    void deve_retornar403Forbidden_quando_alterarCidadeSemPerfilGestor() throws Exception {
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        AtualizarCidadeRequest request = new AtualizarCidadeRequest("Tentativa Indevida", "5002704", true);

        mockMvc.perform(put("/api/v1/cidades/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // --- INATIVAÇÃO / DELEÇÃO (DELETE) ---

    @Test
    @DisplayName("deve retornar 204 No Content sem corpo quando inativar cidade com perfil gestor")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar204NoContentSemCorpo_quando_inativarCidadeComPerfilGestor() throws Exception {
        UUID id = UUID.fromString("c1d00002-0000-0000-0000-000000000002");
        doNothing().when(cidadeService).inativar(id);

        mockMvc.perform(delete("/api/v1/cidades/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(cidadeService).inativar(id);
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando inativar cidade inexistente")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar404NotFound_quando_inativarCidadeInexistente() throws Exception {
        UUID id = UUID.fromString("c1d99999-9999-9999-9999-999999999999");
        doThrow(new ResourceNotFoundException("Cidade nao encontrada")).when(cidadeService).inativar(id);

        mockMvc.perform(delete("/api/v1/cidades/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }

    @Test
    @DisplayName("deve retornar 403 Forbidden quando inativar cidade sem perfil gestor")
    @WithMockUser(roles = "OPERADOR")
    void deve_retornar403Forbidden_quando_inativarCidadeSemPerfilGestor() throws Exception {
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");

        mockMvc.perform(delete("/api/v1/cidades/{id}", id)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deve retornar 401 Unauthorized quando inativar cidade sem autenticacao")
    @WithAnonymousUser
    void deve_retornar401Unauthorized_quando_inativarCidadeSemAutenticacao() throws Exception {
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");

        mockMvc.perform(delete("/api/v1/cidades/{id}", id)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // --- ATIVAÇÃO (PATCH) ---

    @Test
    @DisplayName("deve retornar 200 OK quando ativar cidade com perfil gestor")
    @WithMockUser(roles = "GESTOR")
    void deve_retornar200OK_quando_ativarCidade() throws Exception {
        UUID id = UUID.fromString("c1d00007-0000-0000-0000-000000000007");
        CidadeResponse response = new CidadeResponse(id, "Corumba", "5003207", true);

        when(cidadeService.ativar(id)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/cidades/{id}/ativar", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }
}
