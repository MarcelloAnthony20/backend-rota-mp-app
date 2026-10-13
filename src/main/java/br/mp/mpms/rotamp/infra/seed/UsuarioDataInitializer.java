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
@Order(35)
@RequiredArgsConstructor
public class UsuarioDataInitializer implements CommandLineRunner {

    private static final String INSERT_TEMA_SQL = """
            INSERT INTO tema (id, tema)
            VALUES (?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private static final String INSERT_USUARIO_SQL = """
            INSERT INTO usuario (id, nome, email, mail_nickname, fk_tema, fk_perfil, ativo)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados iniciais (seeds) de temas e usuarios...");
        carregarTemas();
        carregarUsuarios();
        log.info("Carga de dados iniciais de temas e usuarios concluida com sucesso.");
    }

    private void carregarTemas() {
        UUID temaClaro = UUID.fromString("e1000001-0000-0000-0000-000000000001");
        UUID temaEscuro = UUID.fromString("e1000002-0000-0000-0000-000000000002");

        jdbcTemplate.update(INSERT_TEMA_SQL, temaClaro, false);
        jdbcTemplate.update(INSERT_TEMA_SQL, temaEscuro, true);
    }

    private void carregarUsuarios() {
        UUID temaClaro = UUID.fromString("e1000001-0000-0000-0000-000000000001");
        UUID temaEscuro = UUID.fromString("e1000002-0000-0000-0000-000000000002");

        UUID perfilGestor = UUID.fromString("a1000001-0000-0000-0000-000000000002");
        UUID perfilMotorista = UUID.fromString("a1000001-0000-0000-0000-000000000003");
        UUID perfilSolicitante = UUID.fromString("a1000001-0000-0000-0000-000000000004");
        UUID perfilMembro = UUID.fromString("a1000001-0000-0000-0000-000000000005");

        List<UsuarioSeed> seeds = List.of(
                new UsuarioSeed(UUID.fromString("00000001-0000-0000-0000-000000000001"), "Carlos Silva", "carlos.silva@mpms.mp.br", "carlos.silva", temaClaro, perfilMotorista, true),
                new UsuarioSeed(UUID.fromString("00000001-0000-0000-0000-000000000002"), "Joao Pereira", "joao.pereira@mpms.mp.br", "joao.pereira", temaClaro, perfilMotorista, true),
                new UsuarioSeed(UUID.fromString("00000001-0000-0000-0000-000000000003"), "Dr. Marcos Vinicius", "marcos.vinicius@mpms.mp.br", "marcos.vinicius", temaEscuro, perfilMembro, true),
                new UsuarioSeed(UUID.fromString("00000001-0000-0000-0000-000000000004"), "Ana Paula Santos", "ana.santos@mpms.mp.br", "ana.santos", temaClaro, perfilSolicitante, true),
                new UsuarioSeed(UUID.fromString("00000001-0000-0000-0000-000000000005"), "Roberto Gestor", "roberto.frota@mpms.mp.br", "roberto.frota", temaClaro, perfilGestor, true)
        );

        for (UsuarioSeed s : seeds) {
            jdbcTemplate.update(INSERT_USUARIO_SQL, s.id(), s.nome(), s.email(), s.nickname(), s.fkTema(), s.fkPerfil(), s.ativo());
        }
    }

    private record UsuarioSeed(UUID id, String nome, String email, String nickname, UUID fkTema, UUID fkPerfil, boolean ativo) {}
}

