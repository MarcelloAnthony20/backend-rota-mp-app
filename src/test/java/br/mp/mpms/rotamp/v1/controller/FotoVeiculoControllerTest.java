package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.infra.config.SecurityConfig;
import br.mp.mpms.rotamp.infra.exception.GlobalExceptionHandler;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarFotoVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarFotoVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.FotoVeiculoResponse;
import br.mp.mpms.rotamp.v1.service.FotoVeiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FotoVeiculoController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class FotoVeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FotoVeiculoService fotoVeiculoService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("deve retornar 201 Created quando fazer upload multipart da foto para veiculos/foto")
    void deve_retornar201Created_quando_fazerUploadMultipartDaFotoParaVeiculosFoto() throws Exception {
        UUID veiculoId = UUID.randomUUID();
        UUID fotoId = UUID.randomUUID();
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "carro.jpg",
                "image/jpeg",
                "imagem_binaria".getBytes()
        );
        FotoVeiculoResponse response = new FotoVeiculoResponse(fotoId, "http://127.0.0.1:10000/devstoreaccount1/fotos-veiculos/uuid.jpg", veiculoId);

        when(fotoVeiculoService.adicionar(eq(veiculoId), any(MultipartFile.class))).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/veiculos/foto")
                        .file(arquivo)
                        .param("veiculoId", veiculoId.toString())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fotoId.toString()))
                .andExpect(jsonPath("$.foto").value("http://127.0.0.1:10000/devstoreaccount1/fotos-veiculos/uuid.jpg"))
                .andExpect(jsonPath("$.veiculoId").value(veiculoId.toString()));
    }

    @Test
    @DisplayName("deve retornar 201 Created quando adicionar foto via URL em veiculos/foto/url")
    void deve_retornar201Created_quando_adicionarFotoViaURLEmVeiculosFotoUrl() throws Exception {
        UUID veiculoId = UUID.randomUUID();
        UUID fotoId = UUID.randomUUID();
        CriarFotoVeiculoRequest request = new CriarFotoVeiculoRequest("http://cdn.mpms.mp.br/foto1.jpg", veiculoId);
        FotoVeiculoResponse response = new FotoVeiculoResponse(fotoId, "http://cdn.mpms.mp.br/foto1.jpg", veiculoId);

        when(fotoVeiculoService.adicionar(eq(veiculoId), any(CriarFotoVeiculoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/veiculos/foto/url")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fotoId.toString()))
                .andExpect(jsonPath("$.foto").value("http://cdn.mpms.mp.br/foto1.jpg"))
                .andExpect(jsonPath("$.veiculoId").value(veiculoId.toString()));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando adicionar foto sem URL")
    void deve_retornar422UnprocessableEntity_quando_adicionarFotoSemURL() throws Exception {
        UUID veiculoId = UUID.randomUUID();
        CriarFotoVeiculoRequest request = new CriarFotoVeiculoRequest("", veiculoId);

        mockMvc.perform(post("/api/v1/veiculos/foto/url")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errors.foto").exists());
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar fotos em veiculos/foto com filtro de veiculoId")
    void deve_retornar200OK_quando_listarFotosEmVeiculosFotoComFiltro() throws Exception {
        UUID veiculoId = UUID.randomUUID();
        FotoVeiculoResponse f1 = new FotoVeiculoResponse(UUID.randomUUID(), "http://foto1.jpg", veiculoId);
        FotoVeiculoResponse f2 = new FotoVeiculoResponse(UUID.randomUUID(), "http://foto2.jpg", veiculoId);

        when(fotoVeiculoService.listarPorVeiculo(veiculoId)).thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/api/v1/veiculos/foto")
                        .param("veiculoId", veiculoId.toString())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].foto").value("http://foto1.jpg"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando buscar foto por ID em veiculos/foto/{fotoId}")
    void deve_retornar200OK_quando_buscarFotoPorIdEmVeiculosFoto() throws Exception {
        UUID veiculoId = UUID.randomUUID();
        UUID fotoId = UUID.randomUUID();
        FotoVeiculoResponse response = new FotoVeiculoResponse(fotoId, "http://foto1.jpg", veiculoId);

        when(fotoVeiculoService.buscarPorId(fotoId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/veiculos/foto/{fotoId}", fotoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fotoId.toString()))
                .andExpect(jsonPath("$.foto").value("http://foto1.jpg"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando buscar foto inexistente")
    void deve_retornar404NotFound_quando_buscarFotoInexistente() throws Exception {
        UUID fotoId = UUID.randomUUID();

        when(fotoVeiculoService.buscarPorId(fotoId))
                .thenThrow(new ResourceNotFoundException("Foto de veiculo nao encontrada"));

        mockMvc.perform(get("/api/v1/veiculos/foto/{fotoId}", fotoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando substituir foto multipart em veiculos/foto/{fotoId}")
    void deve_retornar200OK_quando_substituirFotoMultipartEmVeiculosFoto() throws Exception {
        UUID veiculoId = UUID.randomUUID();
        UUID fotoId = UUID.randomUUID();
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "carro-novo.png",
                "image/png",
                "conteudo_novo".getBytes()
        );
        FotoVeiculoResponse response = new FotoVeiculoResponse(fotoId, "http://127.0.0.1:10000/devstoreaccount1/fotos-veiculos/novo.png", veiculoId);

        when(fotoVeiculoService.alterar(eq(fotoId), any(MultipartFile.class))).thenReturn(response);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/veiculos/foto/{fotoId}", fotoId)
                        .file(arquivo)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foto").value("http://127.0.0.1:10000/devstoreaccount1/fotos-veiculos/novo.png"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando alterar foto via URL em veiculos/foto/{fotoId}/url")
    void deve_retornar200OK_quando_alterarFotoViaURLEmVeiculosFoto() throws Exception {
        UUID veiculoId = UUID.randomUUID();
        UUID fotoId = UUID.randomUUID();
        AtualizarFotoVeiculoRequest request = new AtualizarFotoVeiculoRequest("http://nova-foto.jpg");
        FotoVeiculoResponse response = new FotoVeiculoResponse(fotoId, "http://nova-foto.jpg", veiculoId);

        when(fotoVeiculoService.alterar(eq(fotoId), any(AtualizarFotoVeiculoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/veiculos/foto/{fotoId}/url", fotoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foto").value("http://nova-foto.jpg"));
    }

    @Test
    @DisplayName("deve retornar 204 NoContent sem corpo quando remover foto em veiculos/foto/{fotoId}")
    void deve_retornar204NoContentSemCorpo_quando_removerFotoEmVeiculosFoto() throws Exception {
        UUID fotoId = UUID.randomUUID();
        doNothing().when(fotoVeiculoService).remover(fotoId);

        mockMvc.perform(delete("/api/v1/veiculos/foto/{fotoId}", fotoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(fotoVeiculoService).remover(fotoId);
    }
}
