package br.mp.mpms.exemploservico.v1.controller;

import br.mp.mpms.exemploservico.infra.config.SecurityConfig;
import br.mp.mpms.exemploservico.infra.exception.ConflictException;
import br.mp.mpms.exemploservico.infra.exception.GlobalExceptionHandler;
import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.model.dto.AtualizarTipoLocalidadeRequest;
import br.mp.mpms.exemploservico.v1.model.dto.CriarTipoLocalidadeRequest;
import br.mp.mpms.exemploservico.v1.model.dto.TipoLocalidadeResponse;
import br.mp.mpms.exemploservico.v1.service.TipoLocalidadeService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    @DisplayName("deve retornar 201 Created quando cadastrar tipo de localidade com dados validos")
    void deve_retornar201Created_quandoCadastrar() throws Exception {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria de Justiça", true);
        UUID id = UUID.fromString("b1000001-0000-0000-0000-000000000001");
        TipoLocalidadeResponse response = new TipoLocalidadeResponse(id, "PROJUS", "Promotoria de Justiça", true);

        when(tipoLocalidadeService.cadastrar(any(CriarTipoLocalidadeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.codigo").value("PROJUS"))
                .andExpect(jsonPath("$.tipoLocalidade").value("Promotoria de Justiça"));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar tipo sem codigo")
    void deve_retornar422_quandoCadastrarSemCodigo() throws Exception {
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
    @DisplayName("deve retornar 409 Conflict quando cadastrar com codigo duplicado")
    void deve_retornar409Conflict_quandoCadastrarCodigoDuplicado() throws Exception {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria", true);

        when(tipoLocalidadeService.cadastrar(any(CriarTipoLocalidadeRequest.class)))
                .thenThrow(new ConflictException("Codigo de tipo de localidade ja cadastrado: PROJUS"));

        mockMvc.perform(post("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando buscar tipo por ID existente")
    void deve_retornar200OK_quandoBuscarPorIdExistente() throws Exception {
        UUID id = UUID.randomUUID();
        TipoLocalidadeResponse response = new TipoLocalidadeResponse(id, "PROJUS", "Promotoria", true);

        when(tipoLocalidadeService.buscarPorId(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tipos-localidade/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("PROJUS"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando buscar tipo por ID inexistente")
    void deve_retornar404_quandoBuscarPorIdInexistente() throws Exception {
        UUID id = UUID.randomUUID();
        when(tipoLocalidadeService.buscarPorId(id))
                .thenThrow(new ResourceNotFoundException("Tipo de localidade nao encontrado"));

        mockMvc.perform(get("/api/v1/tipos-localidade/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar tipos de localidade")
    void deve_retornar200OK_quandoListar() throws Exception {
        TipoLocalidadeResponse r1 = new TipoLocalidadeResponse(UUID.randomUUID(), "T1", "Tipo 1", true);
        when(tipoLocalidadeService.listar(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(r1)));

        mockMvc.perform(get("/api/v1/tipos-localidade")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando alterar tipo de localidade")
    void deve_retornar200OK_quandoAlterar() throws Exception {
        UUID id = UUID.randomUUID();
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("PROJUS2", "Promotoria Alterada", true);
        TipoLocalidadeResponse response = new TipoLocalidadeResponse(id, "PROJUS2", "Promotoria Alterada", true);

        when(tipoLocalidadeService.alterar(eq(id), any(AtualizarTipoLocalidadeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/tipos-localidade/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("PROJUS2"));
    }

    @Test
    @DisplayName("deve retornar 204 No Content sem corpo quando inativar tipo de localidade")
    void deve_retornar204NoContent_quandoInativar() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(tipoLocalidadeService).inativar(id);

        mockMvc.perform(delete("/api/v1/tipos-localidade/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando inativar tipo inexistente")
    void deve_retornar404_quandoInativarInexistente() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Tipo de localidade nao encontrado")).when(tipoLocalidadeService).inativar(id);

        mockMvc.perform(delete("/api/v1/tipos-localidade/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deve retornar 200 OK quando ativar tipo de localidade")
    void deve_retornar200OK_quandoAtivar() throws Exception {
        UUID id = UUID.randomUUID();
        TipoLocalidadeResponse response = new TipoLocalidadeResponse(id, "PROJUS", "Promotoria", true);

        when(tipoLocalidadeService.ativar(id)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/tipos-localidade/{id}/ativar", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }
}
