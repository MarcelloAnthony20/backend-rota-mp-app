package br.mp.mpms.rotamp.infra.seed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilDataInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private PerfilDataInitializer initializer;

    @Test
    @DisplayName("deve executar inserts de seeds para perfis, telas e permissoes")
    void deve_executar_inserts_para_todas_as_seeds() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any())).thenReturn(1);
        when(jdbcTemplate.update(anyString(), any(), any(), any())).thenReturn(1);

        initializer.run();

        verify(jdbcTemplate, atLeastOnce()).update(anyString(), any(), any(), any(), any());
        verify(jdbcTemplate, atLeastOnce()).update(anyString(), any(), any(), any());
    }
}
