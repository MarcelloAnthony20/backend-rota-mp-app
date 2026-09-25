package br.mp.mpms.rotamp.infra.seed;

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
class CidadeDataInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CidadeDataInitializer cidadeDataInitializer;

    private static final UUID ID_CAMPO_GRANDE = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
    private static final UUID ID_DOURADOS = UUID.fromString("c1d00002-0000-0000-0000-000000000002");
    private static final UUID ID_TRES_LAGOAS = UUID.fromString("c1d00003-0000-0000-0000-000000000003");
    private static final UUID ID_CORUMBA = UUID.fromString("c1d00004-0000-0000-0000-000000000004");

    @Test
    @DisplayName("deve executar insert nativo para todas as seeds com sucesso")
    void deve_executarInsertNativoParaTodasAsSeedsComSucesso() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any())).thenReturn(1);

        cidadeDataInitializer.run();

        verify(jdbcTemplate).update(
                contains("ON CONFLICT (id) DO NOTHING"),
                eq(ID_CAMPO_GRANDE), eq("Campo Grande"), eq("5002704"), eq(true)
        );
        verify(jdbcTemplate).update(
                contains("ON CONFLICT (id) DO NOTHING"),
                eq(ID_DOURADOS), eq("Dourados"), eq("5003702"), eq(true)
        );
        verify(jdbcTemplate).update(
                contains("ON CONFLICT (id) DO NOTHING"),
                eq(ID_TRES_LAGOAS), eq("Três Lagoas"), eq("5008306"), eq(true)
        );
        verify(jdbcTemplate).update(
                contains("ON CONFLICT (id) DO NOTHING"),
                eq(ID_CORUMBA), eq("Corumbá"), eq("5003207"), eq(false)
        );

        verify(jdbcTemplate, times(4)).update(anyString(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("deve garantir idempotencia quando registros ja existirem no banco")
    void deve_garantirIdempotencia_quando_registrosJaExistiremNoBanco() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any())).thenReturn(0);

        cidadeDataInitializer.run();

        verify(jdbcTemplate, times(4)).update(anyString(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("deve conter comandos SQL de INSERT idempotente com clausula ON CONFLICT")
    void deve_conterComandoSqlDeInsertComClausulaOnConflict() {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        cidadeDataInitializer.run();

        verify(jdbcTemplate, times(4)).update(sqlCaptor.capture(), any(), any(), any(), any());

        List<String> sqls = sqlCaptor.getAllValues();
        assertThat(sqls).allSatisfy(sql -> {
            assertThat(sql).containsIgnoringCase("INSERT INTO cidade");
            assertThat(sql).containsIgnoringCase("id");
            assertThat(sql).containsIgnoringCase("nome");
            assertThat(sql).containsIgnoringCase("codigo");
            assertThat(sql).containsIgnoringCase("ativo");
            assertThat(sql).containsIgnoringCase("ON CONFLICT (id) DO NOTHING");
        });
    }
}
