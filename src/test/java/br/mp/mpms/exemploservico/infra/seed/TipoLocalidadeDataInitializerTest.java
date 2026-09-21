package br.mp.mpms.exemploservico.infra.seed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoLocalidadeDataInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TipoLocalidadeDataInitializer initializer;

    private static final UUID ID_PROJUS = UUID.fromString("b1000001-0000-0000-0000-000000000001");
    private static final UUID ID_SEDEADM = UUID.fromString("b1000002-0000-0000-0000-000000000002");
    private static final UUID ID_POSTO_DES = UUID.fromString("b1000009-0000-0000-0000-000000000009");

    @Test
    @DisplayName("deve executar insert nativo para todas as seeds de tipo de localidade")
    void deve_executarInsertNativoParaTodasAsSeeds() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any())).thenReturn(1);

        initializer.run();

        verify(jdbcTemplate).update(
                contains("ON CONFLICT (id) DO NOTHING"),
                eq(ID_PROJUS), eq("PROJUS"), eq("Promotoria de Justiça"), eq(true)
        );
        verify(jdbcTemplate).update(
                contains("ON CONFLICT (id) DO NOTHING"),
                eq(ID_SEDEADM), eq("SEDEADM"), eq("Sede Administrativa"), eq(true)
        );
        verify(jdbcTemplate).update(
                contains("ON CONFLICT (id) DO NOTHING"),
                eq(ID_POSTO_DES), eq("POSTO_DES"), eq("Posto Avançado Desativado"), eq(false)
        );

        verify(jdbcTemplate, times(3)).update(anyString(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("deve garantir idempotencia quando tipos ja existirem no banco")
    void deve_garantirIdempotencia_quandoTiposJaExistirem() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any())).thenReturn(0);

        initializer.run();

        verify(jdbcTemplate, times(3)).update(anyString(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("deve conter comandos SQL de INSERT idempotente com clausula ON CONFLICT")
    void deve_conterComandoSqlDeInsertComClausulaOnConflict() {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        initializer.run();

        verify(jdbcTemplate, times(3)).update(sqlCaptor.capture(), any(), any(), any(), any());

        List<String> sqls = sqlCaptor.getAllValues();
        assertThat(sqls).allSatisfy(sql -> {
            assertThat(sql).containsIgnoringCase("INSERT INTO tipo_localidade");
            assertThat(sql).containsIgnoringCase("id");
            assertThat(sql).containsIgnoringCase("codigo");
            assertThat(sql).containsIgnoringCase("tipo_localidade");
            assertThat(sql).containsIgnoringCase("ativo");
            assertThat(sql).containsIgnoringCase("ON CONFLICT (id) DO NOTHING");
        });
    }
}
