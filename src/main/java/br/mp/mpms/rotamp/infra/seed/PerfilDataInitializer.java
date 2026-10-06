package br.mp.mpms.rotamp.infra.seed;

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
@Order(5)
@RequiredArgsConstructor
public class PerfilDataInitializer implements CommandLineRunner {

    private static final String INSERT_PERFIL_SQL = """
            INSERT INTO perfil (id, nome, descricao, ativo)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private static final String INSERT_TELA_SQL = """
            INSERT INTO tela (id, nome, rota)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private static final String INSERT_PERFIL_TELA_SQL = """
            INSERT INTO perfil_tela (id, fk_perfil, fk_tela)
            VALUES (?, ?, ?)
            ON CONFLICT (fk_perfil, fk_tela) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados iniciais (seeds) de perfis, telas e permissoes...");
        carregarPerfis();
        carregarTelas();
        carregarPermissoesIniciais();
        log.info("Carga de dados iniciais de perfis, telas e permissoes concluida.");
    }

    private void carregarPerfis() {
        List<PerfilSeed> seeds = List.of(
                new PerfilSeed(UUID.fromString("a1000001-0000-0000-0000-000000000001"), "ADMINISTRADOR", "Administrador com acesso total e gerenciamento de permissoes", true),
                new PerfilSeed(UUID.fromString("a1000001-0000-0000-0000-000000000002"), "GESTOR", "Gestor de frota e aprovador de solicitacoes", true),
                new PerfilSeed(UUID.fromString("a1000001-0000-0000-0000-000000000003"), "MOTORISTA", "Motorista de viagens e execucao de rotas", true),
                new PerfilSeed(UUID.fromString("a1000001-0000-0000-0000-000000000004"), "SOLICITANTE", "Solicitante de transporte e viagens institucionais", true),
                new PerfilSeed(UUID.fromString("a1000001-0000-0000-0000-000000000005"), "MEMBRO", "Membro institucional com acesso para consultas e requisicoes", true)
        );
        for (PerfilSeed seed : seeds) {
            jdbcTemplate.update(INSERT_PERFIL_SQL, seed.id(), seed.nome(), seed.descricao(), seed.ativo());
        }
    }

    private void carregarTelas() {
        List<TelaSeed> seeds = List.of(
                new TelaSeed(UUID.fromString("f1000001-0000-0000-0000-000000000001"), "Dashboard", "/dashboard"),
                new TelaSeed(UUID.fromString("f1000001-0000-0000-0000-000000000002"), "Solicitações", "/solicitacoes"),
                new TelaSeed(UUID.fromString("f1000001-0000-0000-0000-000000000003"), "Viagens", "/viagens"),
                new TelaSeed(UUID.fromString("f1000001-0000-0000-0000-000000000004"), "Veículos", "/veiculos"),
                new TelaSeed(UUID.fromString("f1000001-0000-0000-0000-000000000005"), "Motoristas", "/motoristas"),
                new TelaSeed(UUID.fromString("f1000001-0000-0000-0000-000000000006"), "Localidades", "/localidades"),
                new TelaSeed(UUID.fromString("f1000001-0000-0000-0000-000000000007"), "Perfis e Permissões", "/perfis"),
                new TelaSeed(UUID.fromString("f1000001-0000-0000-0000-000000000008"), "Usuários", "/usuarios")
        );
        for (TelaSeed seed : seeds) {
            jdbcTemplate.update(INSERT_TELA_SQL, seed.id(), seed.nome(), seed.rota());
        }
    }

    private void carregarPermissoesIniciais() {
        UUID perfilAdmin = UUID.fromString("a1000001-0000-0000-0000-000000000001");
        List<UUID> telasAdmin = List.of(
                UUID.fromString("f1000001-0000-0000-0000-000000000001"),
                UUID.fromString("f1000001-0000-0000-0000-000000000002"),
                UUID.fromString("f1000001-0000-0000-0000-000000000003"),
                UUID.fromString("f1000001-0000-0000-0000-000000000004"),
                UUID.fromString("f1000001-0000-0000-0000-000000000005"),
                UUID.fromString("f1000001-0000-0000-0000-000000000006"),
                UUID.fromString("f1000001-0000-0000-0000-000000000007"),
                UUID.fromString("f1000001-0000-0000-0000-000000000008")
        );
        for (UUID telaId : telasAdmin) {
            jdbcTemplate.update(INSERT_PERFIL_TELA_SQL, UUID.randomUUID(), perfilAdmin, telaId);
        }
    }

    private record PerfilSeed(UUID id, String nome, String descricao, boolean ativo) {}

    private record TelaSeed(UUID id, String nome, String rota) {}
}
