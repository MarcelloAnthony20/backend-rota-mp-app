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
class LocalidadeDataInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LocalidadeDataInitializer initializer;

    private static final UUID ID_LOC_A = UUID.fromString("d1000001-0000-0000-0000-000000000001");
    private static final UUID ID_LOC_SIGILOSA = UUID.fromString("d1000048-0000-0000-0000-000000000048");
    private static final UUID ID_CIDADE_A = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
    private static final UUID ID_TIPO_A = UUID.fromString("b1000001-0000-0000-0000-000000000001");
    private static final UUID ID_END_A = UUID.fromString("e0d00001-0000-0000-0000-000000000001");
    private static final UUID ID_END_SIGILOSO = UUID.fromString("e0d00048-0000-0000-0000-000000000048");

    @Test
    @DisplayName("deve executar insert nativo para as seeds de localidade e endereco")
    void deve_executarInsertNativoParaSeedsDeLocalidadeEEndereco() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any())).thenReturn(1);
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(1);

        initializer.run();

        // Localidade A
        verify(jdbcTemplate).update(
                contains("INSERT INTO localidade"),
                eq(ID_LOC_A), eq(ID_CIDADE_A), eq(ID_TIPO_A), eq("Promotoria de Justiça da Cidadania"), eq(false), eq(true)
        );

        // Localidade Sigilosa
        verify(jdbcTemplate).update(
                contains("INSERT INTO localidade"),
                eq(ID_LOC_SIGILOSA), eq(ID_CIDADE_A), eq(ID_TIPO_A), eq("Núcleo Especial Reservado"), eq(true), eq(true)
        );

        // Endereço A
        verify(jdbcTemplate).update(
                contains("INSERT INTO endereco_localidade"),
                eq(ID_END_A), eq(ID_LOC_A), eq(ID_CIDADE_A),
                eq("79002000"), eq("Rua da Paz"), eq("140"), eq("Jardim dos Estados"), eq("Edifício Sede")
        );

        // Endereço Sigiloso
        verify(jdbcTemplate).update(
                contains("INSERT INTO endereco_localidade"),
                eq(ID_END_SIGILOSO), eq(ID_LOC_SIGILOSA), eq(ID_CIDADE_A),
                eq("79002000"), eq("Rua da Paz"), eq("500"), eq("Jardim dos Estados"), eq("Anexo Reservado")
        );
    }

    @Test
    @DisplayName("deve garantir idempotencia quando registros ja existirem no banco")
    void deve_garantirIdempotencia_quandoRegistrosJaExistirem() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any())).thenReturn(0);
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(0);

        initializer.run();

        verify(jdbcTemplate, times(2)).update(anyString(), any(), any(), any(), any(), any(), any());
        verify(jdbcTemplate, times(2)).update(anyString(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("deve conter clausula ON CONFLICT (id) DO NOTHING nos scripts de insert")
    void deve_conterClausulaOnConflict() {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        initializer.run();

        verify(jdbcTemplate, times(2)).update(sqlCaptor.capture(), any(), any(), any(), any(), any(), any());
        verify(jdbcTemplate, times(2)).update(sqlCaptor.capture(), any(), any(), any(), any(), any(), any(), any(), any());

        List<String> sqls = sqlCaptor.getAllValues();
        assertThat(sqls).allSatisfy(sql -> {
            assertThat(sql).containsIgnoringCase("ON CONFLICT (id) DO NOTHING");
        });
    }
}
