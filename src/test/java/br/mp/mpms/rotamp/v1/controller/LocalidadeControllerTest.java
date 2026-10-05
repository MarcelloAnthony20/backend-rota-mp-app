package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.infra.config.SecurityConfig;
import br.mp.mpms.rotamp.infra.exception.BusinessException;
import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.GlobalExceptionHandler;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.ClassificarLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.ConfigurarSigiloRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.LocalidadeResponse;
import br.mp.mpms.rotamp.v1.service.LocalidadeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocalidadeController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class LocalidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LocalidadeService localidadeService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static final UUID ID_CIDADE = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
    private static final UUID ID_TIPO = UUID.fromString("b1000001-0000-0000-0000-000000000001");
    private static final UUID ID_LOC = UUID.fromString("d1000001-0000-0000-0000-000000000001");

    // --- CADASTRO (POST) ---

    @Test
    @DisplayName("deve retornar 201 Created quando cadastrar localidade com dados validos")
    void deve_retornar201Created_quandoCadastrar() throws Exception {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria da Cidadania", ID_CIDADE, ID_TIPO,
                "79002-000", "Rua da Paz", "140", "Jardim dos Estados", "Edifício Sede", false, true
        );

        LocalidadeResponse response = new LocalidadeResponse(
                ID_LOC, ID_TIPO, "Promotoria de Justiça", ID_CIDADE, "Campo Grande",
                "Promotoria da Cidadania", false, true,
                "79002000", "Rua da Paz", "140", "Jardim dos Estados", "Edifício Sede"
        );

        when(localidadeService.cadastrar(any(CriarLocalidadeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/localidades")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(ID_LOC.toString()))
                .andExpect(jsonPath("$.nome").value("Promotoria da Cidadania"))
                .andExpect(jsonPath("$.cep").value("79002000"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar localidade sem nome")
    void deve_retornar422_quandoCadastrarSemNome() throws Exception {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "", ID_CIDADE, ID_TIPO, "79002-000", "Rua da Paz", "140", "Centro", null, false, true
        );

        mockMvc.perform(post("/api/v1/localidades")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Validacao falhou"))
                .andExpect(jsonPath("$.errors.nome").exists());
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar com numero predial negativo ou invalido")
    void deve_retornar422_quandoCadastrarNumeroNegativo() throws Exception {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria", ID_CIDADE, ID_TIPO, "79002-000", "Rua da Paz", "-140", "Centro", null, false, true
        );

        mockMvc.perform(post("/api/v1/localidades")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.numero").exists());
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cadastrar com CEP invalido")
    void deve_retornar422_quandoCadastrarCepInvalido() throws Exception {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria", ID_CIDADE, ID_TIPO, "123", "Rua da Paz", "140", "Centro", null, false, true
        );

        mockMvc.perform(post("/api/v1/localidades")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.cep").exists());
    }

    @Test
    @DisplayName("deve retornar 409 Conflict quando cadastrar com combinacao duplicada (RN-6)")
    void deve_retornar409Conflict_quandoCadastrarDuplicadoRN6() throws Exception {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria da Cidadania", ID_CIDADE, ID_TIPO, "79002-000", "Rua da Paz", "140", "Centro", null, false, true
        );

        when(localidadeService.cadastrar(any(CriarLocalidadeRequest.class)))
                .thenThrow(new ConflictException("Ja existe uma localidade cadastrada com este nome para a mesma cidade e tipo"));

        mockMvc.perform(post("/api/v1/localidades")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de dados"));
    }

    @Test
    @DisplayName("deve retornar 422 Unprocessable Entity quando cidade ou tipo estiver inativo")
    void deve_retornar422_quandoCidadeOuTipoInativo() throws Exception {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria de Dourados", ID_CIDADE, ID_TIPO, "79800-000", "Rua Pres Vargas", "450", "Centro", null, false, true
        );

        when(localidadeService.cadastrar(any(CriarLocalidadeRequest.class)))
                .thenThrow(new BusinessException("A cidade selecionada deve estar ativa", HttpStatus.UNPROCESSABLE_ENTITY));

        mockMvc.perform(post("/api/v1/localidades")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("A cidade selecionada deve estar ativa"));
    }

    @Test
    @DisplayName("deve retornar 400 Bad Request quando BusinessException for lancada sem status especifico")
    void deve_retornar400_quandoBusinessExceptionGenerica() throws Exception {
        CriarLocalidadeRequest request = new CriarLocalidadeRequest(
                "Promotoria", ID_CIDADE, ID_TIPO, "79002-000", "Rua da Paz", "140", "Centro", null, false, true
        );

        when(localidadeService.cadastrar(any(CriarLocalidadeRequest.class)))
                .thenThrow(new BusinessException("Regra de negocio violada"));

        mockMvc.perform(post("/api/v1/localidades")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Regra de negocio violada"));
    }

    // --- CONSULTA (GET) ---

    @Test
    @DisplayName("deve retornar 200 OK quando buscar localidade por ID existente")
    void deve_retornar200OK_quandoBuscarPorIdExistente() throws Exception {
        LocalidadeResponse response = new LocalidadeResponse(
                ID_LOC, ID_TIPO, "Promotoria de Justiça", ID_CIDADE, "Campo Grande",
                "Promotoria da Cidadania", false, true,
                "79002000", "Rua da Paz", "140", "Jardim dos Estados", "Edifício Sede"
        );

        when(localidadeService.buscarPorId(ID_LOC)).thenReturn(response);

        mockMvc.perform(get("/api/v1/localidades/{id}", "d1000001-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_LOC.toString()))
                .andExpect(jsonPath("$.nome").value("Promotoria da Cidadania"));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando buscar localidade por ID inexistente")
    void deve_retornar404_quandoBuscarPorIdInexistente() throws Exception {
        when(localidadeService.buscarPorId(ID_LOC))
                .thenThrow(new ResourceNotFoundException("Localidade nao encontrada"));

        mockMvc.perform(get("/api/v1/localidades/{id}", "d1000001-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"));
    }

    @Test
    @DisplayName("deve retornar 200 OK quando listar localidades")
    void deve_retornar200OK_quandoListar() throws Exception {
        LocalidadeResponse loc = new LocalidadeResponse(
                ID_LOC, ID_TIPO, "Promotoria de Justiça", ID_CIDADE, "Campo Grande",
                "Promotoria da Cidadania", false, true,
                "79002000", "Rua da Paz", "140", "Jardim dos Estados", "Edifício Sede"
        );

        when(localidadeService.listar(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(loc)));

        mockMvc.perform(get("/api/v1/localidades")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    // --- ALTERAÇÃO (PUT) ---

    @Test
    @DisplayName("deve retornar 200 OK quando alterar localidade com sucesso")
    void deve_retornar200OK_quandoAlterar() throws Exception {
        AtualizarLocalidadeRequest request = new AtualizarLocalidadeRequest(
                "Promotoria Alterada", ID_CIDADE, ID_TIPO,
                "79004-311", "Rua Ceará", "800", "Santa Fé", "Bloco B", false, true
        );
        LocalidadeResponse response = new LocalidadeResponse(
                ID_LOC, ID_TIPO, "Promotoria de Justiça", ID_CIDADE, "Campo Grande",
                "Promotoria Alterada", false, true,
                "79004311", "Rua Ceará", "800", "Santa Fé", "Bloco B"
        );

        when(localidadeService.alterar(eq(ID_LOC), any(AtualizarLocalidadeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/localidades/{id}", "d1000001-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Promotoria Alterada"))
                .andExpect(jsonPath("$.cep").value("79004311"));
    }

    // --- DELEÇÃO / INATIVAÇÃO (DELETE) ---

    @Test
    @DisplayName("deve retornar 204 No Content sem corpo quando inativar localidade")
    void deve_retornar204NoContent_quandoInativar() throws Exception {
        doNothing().when(localidadeService).inativar(ID_LOC);

        mockMvc.perform(delete("/api/v1/localidades/{id}", "d1000001-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("deve retornar 404 NotFound quando inativar localidade inexistente")
    void deve_retornar404_quandoInativarInexistente() throws Exception {
        doThrow(new ResourceNotFoundException("Localidade nao encontrada")).when(localidadeService).inativar(ID_LOC);

        mockMvc.perform(delete("/api/v1/localidades/{id}", "d1000001-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // --- ATIVAÇÃO (PATCH) ---

    @Test
    @DisplayName("deve retornar 200 OK quando ativar localidade")
    void deve_retornar200OK_quandoAtivar() throws Exception {
        LocalidadeResponse response = new LocalidadeResponse(
                ID_LOC, ID_TIPO, "Promotoria de Justiça", ID_CIDADE, "Campo Grande",
                "Promotoria da Cidadania", false, true,
                "79002000", "Rua da Paz", "140", "Jardim dos Estados", "Edifício Sede"
        );

        when(localidadeService.ativar(ID_LOC)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/localidades/{id}/ativar", "d1000001-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    // --- CLASSIFICAÇÃO (PATCH) ---

    @Test
    @DisplayName("deve retornar 200 OK quando reclassificar localidade")
    void deve_retornar200OK_quandoClassificar() throws Exception {
        ClassificarLocalidadeRequest request = new ClassificarLocalidadeRequest(ID_TIPO);
        LocalidadeResponse response = new LocalidadeResponse(
                ID_LOC, ID_TIPO, "Promotoria de Justiça", ID_CIDADE, "Campo Grande",
                "Promotoria da Cidadania", false, true,
                "79002000", "Rua da Paz", "140", "Jardim dos Estados", "Edifício Sede"
        );

        when(localidadeService.classificar(eq(ID_LOC), any(ClassificarLocalidadeRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/localidades/{id}/classificacao", "d1000001-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoLocalidadeId").value(ID_TIPO.toString()));
    }

    // --- CONFIGURAÇÃO DE SIGILO (PATCH) ---

    @Test
    @DisplayName("deve retornar 200 OK quando configurar sigilo")
    void deve_retornar200OK_quandoConfigurarSigilo() throws Exception {
        ConfigurarSigiloRequest request = new ConfigurarSigiloRequest(true);
        LocalidadeResponse response = new LocalidadeResponse(
                ID_LOC, ID_TIPO, "Promotoria de Justiça", ID_CIDADE, "Campo Grande",
                "Promotoria da Cidadania", true, true,
                "***", "[SIGILOSO]", "***", "[SIGILOSO]", "[SIGILOSO]"
        );

        when(localidadeService.configurarSigilo(eq(ID_LOC), any(ConfigurarSigiloRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/localidades/{id}/sigilo", "d1000001-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.localSigiloso").value(true));
    }

    @Test
    @DisplayName("deve retornar 400 Bad Request quando configurar sigilo com valor nao booleano")
    void deve_retornar400_quandoConfigurarSigiloComValorNaoBooleano() throws Exception {
        String payloadInvalido = "{\"localSigiloso\": \"sim\"}";

        mockMvc.perform(patch("/api/v1/localidades/{id}/sigilo", "d1000001-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GESTOR")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisicao invalida"));
    }
}
