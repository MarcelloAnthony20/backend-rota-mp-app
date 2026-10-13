package br.mp.mpms.rotamp.infra.security;

import br.mp.mpms.rotamp.v1.model.entity.Perfil;
import br.mp.mpms.rotamp.v1.repository.PerfilRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisaoPerfilFilterTest {

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private org.springframework.beans.factory.ObjectProvider<PerfilRepository> perfilRepositoryProvider;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private VisaoPerfilFilter visaoPerfilFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        org.mockito.Mockito.lenient().when(perfilRepositoryProvider.getIfAvailable()).thenReturn(perfilRepository);
    }


    @Test
    @DisplayName("deve seguir cadeia sem alteracao quando header X-Visao-Perfil nao for enviado")
    void deve_seguir_cadeia_quando_header_ausente() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        visaoPerfilFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("deve rejeitar com 403 Forbidden se usuario nao autenticado tentar enviar header")
    void deve_ignorar_se_usuario_nao_autenticado() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(VisaoPerfilFilter.HEADER_VISAO_PERFIL, "GESTOR");
        MockHttpServletResponse response = new MockHttpServletResponse();

        visaoPerfilFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("deve rejeitar com 403 Forbidden se usuario comum (nao-admin) tentar alternar visao")
    void deve_rejeitar_403_quando_usuario_comum_tenta_alternar_visao() throws ServletException, IOException {
        Authentication userAuth = new UsernamePasswordAuthenticationToken(
                "motorista@mpms.mp.br",
                "credentials",
                List.of(new SimpleGrantedAuthority("ROLE_MOTORISTA"))
        );
        SecurityContextHolder.getContext().setAuthentication(userAuth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(VisaoPerfilFilter.HEADER_VISAO_PERFIL, "GESTOR");
        MockHttpServletResponse response = new MockHttpServletResponse();

        visaoPerfilFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("Apenas administradores podem alternar modo de visão");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("deve alternar autoridades com sucesso quando administrador solicita perfil valido")
    void deve_alternar_autoridades_quando_admin_solicita_perfil_valido() throws ServletException, IOException {
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                "admin@mpms.mp.br",
                "credentials",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
        );
        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        Perfil perfilGestor = Perfil.builder()
                .id(UUID.randomUUID())
                .nome("GESTOR")
                .descricao("Gestor de transportes")
                .ativo(true)
                .build();

        when(perfilRepository.findByNomeIgnoreCase("GESTOR")).thenReturn(Optional.of(perfilGestor));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(VisaoPerfilFilter.HEADER_VISAO_PERFIL, "GESTOR");
        MockHttpServletResponse response = new MockHttpServletResponse();

        visaoPerfilFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication novaAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(novaAuth).isNotNull();
        assertThat(novaAuth.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_GESTOR", "ROLE_VISAO_SIMULADA");
    }

    @Test
    @DisplayName("deve rejeitar com 400 Bad Request quando perfil solicitado nao existir ou estiver inativo")
    void deve_rejeitar_400_quando_perfil_inexistente() throws ServletException, IOException {
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                "admin@mpms.mp.br",
                "credentials",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
        );
        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        when(perfilRepository.findByNomeIgnoreCase("PERFIL_INEXISTENTE")).thenReturn(Optional.empty());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(VisaoPerfilFilter.HEADER_VISAO_PERFIL, "PERFIL_INEXISTENTE");
        MockHttpServletResponse response = new MockHttpServletResponse();

        visaoPerfilFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentAsString()).contains("não encontrado ou inativo");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("deve manter permissao original se administrador enviar X-Visao-Perfil: ADMINISTRADOR")
    void deve_manter_original_se_visao_solicitada_for_administrador() throws ServletException, IOException {
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                "admin@mpms.mp.br",
                "credentials",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
        );
        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(VisaoPerfilFilter.HEADER_VISAO_PERFIL, "ADMINISTRADOR");
        MockHttpServletResponse response = new MockHttpServletResponse();

        visaoPerfilFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication atual = SecurityContextHolder.getContext().getAuthentication();
        assertThat(atual.getAuthorities())
                .extracting("authority")
                .contains("ROLE_ADMINISTRADOR");
    }
}

