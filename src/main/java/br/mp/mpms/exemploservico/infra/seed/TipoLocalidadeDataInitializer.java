package br.mp.mpms.exemploservico.infra.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Order(20)
@RequiredArgsConstructor
public class TipoLocalidadeDataInitializer implements CommandLineRunner {

    private static final String INSERT_TIPO_LOCALIDADE_SQL = """
            INSERT INTO tipo_localidade (id, codigo, tipo_localidade, ativo)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados iniciais (seeds) de tipos de localidade...");

        List<TipoLocalidadeSeed> seeds = List.of(
                new TipoLocalidadeSeed(UUID.fromString("t1p00001-0000-0000-0000-000000000001"), "PROJUS", "Promotoria de Justiça", true),
                new TipoLocalidadeSeed(UUID.fromString("t1p00002-0000-0000-0000-000000000002"), "SEDEADM", "Sede Administrativa", true),
                new TipoLocalidadeSeed(UUID.fromString("t1p00009-0000-0000-0000-000000000009"), "POSTO_DES", "Posto Avançado Desativado", false)
        );

        for (TipoLocalidadeSeed seed : seeds) {
            carregarSeNaoExistir(seed);
        }

        log.info("Carga de dados iniciais de tipos de localidade concluida com sucesso.");
    }

    private void carregarSeNaoExistir(TipoLocalidadeSeed seed) {
        int linhasAfetadas = jdbcTemplate.update(
                INSERT_TIPO_LOCALIDADE_SQL,
                seed.id(),
                seed.codigo(),
                seed.tipoLocalidade(),
                seed.ativo()
        );

        if (linhasAfetadas > 0) {
            log.info("Seed de tipo de localidade cadastrada com sucesso: id={}, codigo='{}', tipo='{}', ativo={}",
                    seed.id(), seed.codigo(), seed.tipoLocalidade(), seed.ativo());
        } else {
            log.debug("Seed de tipo de localidade ja existente (id={}, codigo={}). Ignorando insercao.",
                    seed.id(), seed.codigo());
        }
    }

    private record TipoLocalidadeSeed(UUID id, String codigo, String tipoLocalidade, boolean ativo) {}
}
