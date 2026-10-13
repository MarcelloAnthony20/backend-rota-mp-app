package br.mp.mpms.rotamp.v1.controller;

import br.mp.mpms.rotamp.infra.config.SecurityConfig;
import br.mp.mpms.rotamp.infra.exception.GlobalExceptionHandler;
import br.mp.mpms.rotamp.infra.security.VisaoPerfilFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExemploController.class)
@ActiveProfiles("dev")
@Import({SecurityConfig.class, VisaoPerfilFilter.class, GlobalExceptionHandler.class})
class ExemploControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("deve permitir acesso a /api/admin/dados quando enviado token mock-admin-token")
    void deve_permitirAcessoAdmin_quando_enviadoTokenMockAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dados")
                        .header("Authorization", "Bearer mock-admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Painel Administrativo Consolidado"))
                .andExpect(jsonPath("$.usuarioSolicitante").value("admin.dev@mpms.mp.br"))
                .andExpect(jsonPath("$.papeis").isArray());
    }

    @Test
    @DisplayName("deve retornar 403 Forbidden em /api/admin/dados quando enviado token mock-user-token")
    void deve_retornarForbiddenEmAdmin_quando_enviadoTokenMockUser() throws Exception {
        mockMvc.perform(get("/api/admin/dados")
                        .header("Authorization", "Bearer mock-user-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deve permitir acesso a /api/user/perfil quando enviado token mock-user-token")
    void deve_permitirAcessoUser_quando_enviadoTokenMockUser() throws Exception {
        mockMvc.perform(get("/api/user/perfil")
                        .header("Authorization", "Bearer mock-user-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user.dev@mpms.mp.br"))
                .andExpect(jsonPath("$.status").value("Ativo"));
    }

    @Test
    @DisplayName("deve retornar 401 Unauthorized quando requisição for realizada sem token")
    void deve_retornarUnauthorized_quando_naoAutenticado() throws Exception {
        mockMvc.perform(get("/api/user/perfil"))
                .andExpect(status().isUnauthorized());
    }
}

