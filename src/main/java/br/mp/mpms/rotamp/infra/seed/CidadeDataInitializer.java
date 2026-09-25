package br.mp.mpms.rotamp.infra.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import org.springframework.core.annotation.Order;
import java.util.UUID;

@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class CidadeDataInitializer implements CommandLineRunner {

    private static final String INSERT_CIDADE_SQL = """
            INSERT INTO cidade (id, nome, codigo, ativo)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados iniciais (seeds) de cidades...");

        List<CidadeSeed> seeds = List.of(
                new CidadeSeed(UUID.fromString("c1d00001-0000-0000-0000-000000000001"), "Campo Grande", "5002704", true),
                new CidadeSeed(UUID.fromString("c1d00002-0000-0000-0000-000000000002"), "Dourados", "5003702", true),
                new CidadeSeed(UUID.fromString("c1d00003-0000-0000-0000-000000000003"), "Três Lagoas", "5008306", true),
                new CidadeSeed(UUID.fromString("c1d00004-0000-0000-0000-000000000004"), "Corumbá", "5003207", false)
        );

        for (CidadeSeed seed : seeds) {
            carregarSeNaoExistir(seed);
        }

        log.info("Carga de dados iniciais de cidades concluida com sucesso.");
    }

    private void carregarSeNaoExistir(CidadeSeed seed) {
        int linhasAfetadas = jdbcTemplate.update(
                INSERT_CIDADE_SQL,
                seed.id(),
                seed.nome(),
                seed.codigo(),
                seed.ativo()
        );

        if (linhasAfetadas > 0) {
            log.info("Seed de cidade cadastrada com sucesso: id={}, nome='{}', codigo='{}', ativo={}",
                    seed.id(), seed.nome(), seed.codigo(), seed.ativo());
        } else {
            log.debug("Seed de cidade ja existente (id={}, codigo={}). Ignorando insercao.",
                    seed.id(), seed.codigo());
        }
    }

    private record CidadeSeed(UUID id, String nome, String codigo, boolean ativo) {}
}
