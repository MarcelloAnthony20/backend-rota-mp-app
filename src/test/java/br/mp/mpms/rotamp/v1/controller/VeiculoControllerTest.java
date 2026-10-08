package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.infra.config.SecurityConfig;
import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.GlobalExceptionHandler;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.VeiculoDetalhesResponse;
import br.mp.mpms.rotamp.v1.model.dto.VeiculoResponse;
import br.mp.mpms.rotamp.v1.model.dto.VincularLocalidadeVeiculoRequest;
import br.mp.mpms.rotamp.v1.service.VeiculoService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VeiculoController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VeiculoService veiculoService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("deve retornar 201 Created quando cadastrar veiculo valido")
    void deve_retornar201Created_quando_cadastrarVeiculoValido() throws Exception {
        UUID localidadeId = UUID.randomUUID();
        UUID modeloId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        CriarVeiculoRequest request = new CriarVeiculoRequest(localidadeId, modeloId, "RPA1B23", 2024, true);
        VeiculoResponse response = new VeiculoResponse(veiculoId, localidadeId, modeloId, "RPA1B23", 2024, true);

        when(veiculoService.cadastrar(any(CriarVeiculoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/veiculos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(veiculoId.toString()))
                .andExpect(jsonPath("$.placa").value("RPA1B23"))
                .andExpect(jsonPath("$.ano").value(2024));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar veiculo sem placa")
    void deve_retornar422UnprocessableEntity_quando_cadastrarVeiculoSemPlaca() throws Exception {
        CriarVeiculoRequest request = new CriarVeiculoRequest(UUID.randomUUID(), UUID.randomUUID(), "", 2024, true);

        mockMvc.perform(post("/api/v1/veiculos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.placa").exists());
    }

    @Test
    @DisplayName("deve retornar 409 Conflict quando placa ja cadastrada")
    void deve_retornar409Conflict_quando_placaJaCadastrada() throws Exception {
        CriarVeiculoRequest request = new CriarVeiculoRequest(UUID.randomUUID(), UUID.randomUUID(), "RPA1B23", 2024, true);

        when(veiculoService.cadastrar(any(CriarVeiculoRequest.class)))
                .thenThrow(new ConflictException("Placa de veiculo ja cadastrada no sistema: RPA1B23"));

        mockMvc.perform(post("/api/v1/veiculos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando buscar veiculo por ID existente")
    void deve_retornar200OK_quando_buscarVeiculoPorIdExistente() throws Exception {
        UUID id = UUID.randomUUID();
        VeiculoResponse response = new VeiculoResponse(id, UUID.randomUUID(), UUID.randomUUID(), "RPA1B23", 2024, true);

        when(veiculoService.buscarPorId(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/veiculos/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("RPA1B23"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando buscar veiculo por ID inexistente")
    void deve_retornar404NotFound_quando_buscarVeiculoPorIdInexistente() throws Exception {
        UUID id = UUID.randomUUID();
        when(veiculoService.buscarPorId(id)).thenThrow(new ResourceNotFoundException("Veiculo nao encontrado"));

        mockMvc.perform(get("/api/v1/veiculos/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando consultar detalhes do veiculo")
    void deve_retornar200OK_quando_consultarDetalhesDoVeiculo() throws Exception {
        UUID id = UUID.randomUUID();
        VeiculoDetalhesResponse response = new VeiculoDetalhesResponse(
                id, UUID.randomUUID(), "Edifício Sede - PGJ",
                UUID.randomUUID(), "Toyota", "Corolla", 5,
                "RPA1B23", 2024, true
        );

        when(veiculoService.buscarDetalhesPorId(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/veiculos/{id}/detalhes", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.modelo").value("Corolla"))
                .andExpect(jsonPath("$.localidadeNome").value("Edifício Sede - PGJ"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar veiculos da frota")
    void deve_retornar200OK_quando_listarVeiculosDaFrota() throws Exception {
        VeiculoResponse v1 = new VeiculoResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "ABC1234", 2023, true);
        when(veiculoService.listar(any(Pageable.class), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(v1)));

        mockMvc.perform(get("/api/v1/veiculos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].placa").value("ABC1234"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando alterar veiculo existente")
    void deve_retornar200OK_quando_alterarVeiculoExistente() throws Exception {
        UUID id = UUID.randomUUID();
        UUID locId = UUID.randomUUID();
        UUID modId = UUID.randomUUID();
        AtualizarVeiculoRequest request = new AtualizarVeiculoRequest(locId, modId, "NEW5678", 2024);
        VeiculoResponse response = new VeiculoResponse(id, locId, modId, "NEW5678", 2024, true);

        when(veiculoService.alterar(eq(id), any(AtualizarVeiculoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/veiculos/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("NEW5678"));
    }

    @Test
    @DisplayName("deve retornar 204 NoContent sem corpo quando inativar veiculo")
    void deve_retornar204NoContentSemCorpo_quando_inativarVeiculo() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(veiculoService).inativar(id);

        mockMvc.perform(delete("/api/v1/veiculos/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(veiculoService).inativar(id);
    }

    @Test
    @DisplayName("deve retornar 200 OK quando ativar veiculo")
    void deve_retornar200OK_quando_ativarVeiculo() throws Exception {
        UUID id = UUID.randomUUID();
        VeiculoResponse response = new VeiculoResponse(id, UUID.randomUUID(), UUID.randomUUID(), "RPA1B23", 2024, true);

        when(veiculoService.ativar(id)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/veiculos/{id}/ativar", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando vincular localidade ao veiculo")
    void deve_retornar200OK_quando_vincularLocalidadeAoVeiculo() throws Exception {
        UUID id = UUID.randomUUID();
        UUID novaLocId = UUID.randomUUID();
        VincularLocalidadeVeiculoRequest request = new VincularLocalidadeVeiculoRequest(novaLocId);
        VeiculoResponse response = new VeiculoResponse(id, novaLocId, UUID.randomUUID(), "RPA1B23", 2024, true);

        when(veiculoService.vincularLocalidade(eq(id), eq(novaLocId))).thenReturn(response);

        mockMvc.perform(put("/api/v1/veiculos/{id}/localidade", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.localidadeId").value(novaLocId.toString()));
    }
}

