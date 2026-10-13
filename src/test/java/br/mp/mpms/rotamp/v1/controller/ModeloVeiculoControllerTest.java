package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.infra.config.SecurityConfig;
import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.GlobalExceptionHandler;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarModeloVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarModeloVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.ModeloVeiculoResponse;
import br.mp.mpms.rotamp.v1.service.ModeloVeiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModeloVeiculoController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class ModeloVeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModeloVeiculoService modeloVeiculoService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("deve retornar 201 Created quando cadastrar modelo de veiculo valido")
    void deve_retornar201Created_quando_cadastrarModeloVeiculoValido() throws Exception {
        CriarModeloVeiculoRequest request = new CriarModeloVeiculoRequest("Toyota", "Corolla", 5, "http://logo.png", true);
        UUID id = UUID.randomUUID();
        ModeloVeiculoResponse response = new ModeloVeiculoResponse(id, "Toyota", "Corolla", 5, "http://logo.png", true);

        when(modeloVeiculoService.cadastrar(any(CriarModeloVeiculoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/veiculos/modelo")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.modelo").value("Corolla"));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar sem marca")
    void deve_retornar422UnprocessableEntity_quando_cadastrarSemMarca() throws Exception {
        CriarModeloVeiculoRequest request = new CriarModeloVeiculoRequest("", "Corolla", 5, null, true);

        mockMvc.perform(post("/api/v1/veiculos/modelo")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.marca").exists());
    }

    @Test
    @DisplayName("deve retornar 409 Conflict quando modelo ja existente")
    void deve_retornar409Conflict_quando_modeloJaExistente() throws Exception {
        CriarModeloVeiculoRequest request = new CriarModeloVeiculoRequest("Toyota", "Corolla", 5, null, true);

        when(modeloVeiculoService.cadastrar(any(CriarModeloVeiculoRequest.class)))
                .thenThrow(new ConflictException("Modelo de veiculo ja cadastrado para esta marca e modelo"));

        mockMvc.perform(post("/api/v1/veiculos/modelo")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando buscar modelo por ID existente")
    void deve_retornar200OK_quando_buscarModeloPorIdExistente() throws Exception {
        UUID id = UUID.randomUUID();
        ModeloVeiculoResponse response = new ModeloVeiculoResponse(id, "Honda", "Civic", 5, null, true);

        when(modeloVeiculoService.buscarPorId(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/veiculos/modelo/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Civic"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando buscar por ID inexistente")
    void deve_retornar404NotFound_quando_buscarPorIdInexistente() throws Exception {
        UUID id = UUID.randomUUID();
        when(modeloVeiculoService.buscarPorId(id))
                .thenThrow(new ResourceNotFoundException("Modelo de veiculo nao encontrado"));

        mockMvc.perform(get("/api/v1/veiculos/modelo/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar modelos de veiculo")
    void deve_retornar200OK_quando_listarModelosVeiculo() throws Exception {
        ModeloVeiculoResponse m1 = new ModeloVeiculoResponse(UUID.randomUUID(), "Toyota", "Corolla", 5, null, true);
        when(modeloVeiculoService.listar(any(Pageable.class), any(), any()))
                .thenReturn(new PageImpl<>(List.of(m1)));

        mockMvc.perform(get("/api/v1/veiculos/modelo")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].marca").value("Toyota"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando alterar modelo existente")
    void deve_retornar200OK_quando_alterarModeloExistente() throws Exception {
        UUID id = UUID.randomUUID();
        AtualizarModeloVeiculoRequest request = new AtualizarModeloVeiculoRequest("Toyota", "Corolla Altis", 5, "http://logo.png");
        ModeloVeiculoResponse response = new ModeloVeiculoResponse(id, "Toyota", "Corolla Altis", 5, "http://logo.png", true);

        when(modeloVeiculoService.alterar(eq(id), any(AtualizarModeloVeiculoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/veiculos/modelo/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Corolla Altis"));
    }

    @Test
    @DisplayName("deve retornar 204 NoContent sem corpo quando inativar modelo")
    void deve_retornar204NoContentSemCorpo_quando_inativarModelo() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(modeloVeiculoService).inativar(id);

        mockMvc.perform(delete("/api/v1/veiculos/modelo/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(modeloVeiculoService).inativar(id);
    }

    @Test
    @DisplayName("deve retornar 200 OK quando ativar modelo")
    void deve_retornar200OK_quando_ativarModelo() throws Exception {
        UUID id = UUID.randomUUID();
        ModeloVeiculoResponse response = new ModeloVeiculoResponse(id, "Toyota", "Corolla", 5, null, true);

        when(modeloVeiculoService.ativar(id)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/veiculos/modelo/{id}/ativar", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando atualizar logotipo da marca do modelo via multipart")
    void deve_retornar200OK_quando_atualizarLogotipoDaMarcaDoModeloViaMultipart() throws Exception {
        UUID id = UUID.randomUUID();
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "logo.png",
                "image/png",
                "logo_binaria".getBytes()
        );
        ModeloVeiculoResponse response = new ModeloVeiculoResponse(id, "Toyota", "Corolla", 5, "http://storage/logo.png", true);

        when(modeloVeiculoService.atualizarLogo(eq(id), any(MultipartFile.class))).thenReturn(response);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/veiculos/modelo/{id}/logo", id)
                        .file(arquivo)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logo").value("http://storage/logo.png"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando atualizar logotipo da marca do modelo via URL")
    void deve_retornar200OK_quando_atualizarLogotipoDaMarcaDoModeloViaUrl() throws Exception {
        UUID id = UUID.randomUUID();
        ModeloVeiculoResponse response = new ModeloVeiculoResponse(id, "Toyota", "Corolla", 5, "http://cdn/logo.png", true);

        when(modeloVeiculoService.atualizarLogoPorUrl(eq(id), eq("http://cdn/logo.png"))).thenReturn(response);

        mockMvc.perform(put("/api/v1/veiculos/modelo/{id}/logo/url", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("http://cdn/logo.png"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logo").value("http://cdn/logo.png"));
    }
}


