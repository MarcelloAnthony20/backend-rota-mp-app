package br.mp.mpms.rotamp.infra.seed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsuarioDataInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UsuarioDataInitializer initializer;

    private static final UUID TEMA_CLARO = UUID.fromString("e1000001-0000-0000-0000-000000000001");
    private static final UUID TEMA_ESCURO = UUID.fromString("e1000002-0000-0000-0000-000000000002");
    private static final UUID PERFIL_MOTORISTA = UUID.fromString("a1000001-0000-0000-0000-000000000003");
    private static final UUID USUARIO_CARLOS = UUID.fromString("00000001-0000-0000-0000-000000000001");

    @Test
    @DisplayName("deve executar inserts de seeds para temas e usuarios")
    void deve_executar_inserts_quando_inicializar() {
        initializer.run();

        verify(jdbcTemplate).update(
                contains("INSERT INTO tema"),
                eq(TEMA_CLARO), eq(false)
        );
        verify(jdbcTemplate).update(
                contains("INSERT INTO tema"),
                eq(TEMA_ESCURO), eq(true)
        );
        verify(jdbcTemplate).update(
                contains("INSERT INTO usuario"),
                eq(USUARIO_CARLOS), eq("Carlos Silva"), eq("carlos.silva@mpms.mp.br"), eq("carlos.silva"), eq(TEMA_CLARO), eq(PERFIL_MOTORISTA), eq(true)
        );
    }
}

