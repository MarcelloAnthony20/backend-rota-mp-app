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
@Order(50)
@RequiredArgsConstructor
public class MotoristaDataInitializer implements CommandLineRunner {

    private static final String INSERT_MOTORISTA_SQL = """
            INSERT INTO motorista (id, display_nome, fk_localidade, matricula, foto, contato_pessoal, contatos_funcional, status, fk_usuario)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados iniciais (seeds) de motoristas oficiais...");
        carregarMotoristas();
        log.info("Carga de dados iniciais de motoristas concluida com sucesso.");
    }

    private void carregarMotoristas() {
        UUID locCampoGrande = UUID.fromString("d1000001-0000-0000-0000-000000000001");
        UUID locDourados = UUID.fromString("d1000002-0000-0000-0000-000000000002");

        UUID usuarioMotorista1 = UUID.fromString("00000001-0000-0000-0000-000000000001");
        UUID usuarioMotorista2 = UUID.fromString("00000001-0000-0000-0000-000000000002");

        List<MotoristaSeed> seeds = List.of(
                new MotoristaSeed(
                        UUID.fromString("00000005-0000-0000-0000-000000000001"),
                        "Carlos Silva",
                        locCampoGrande,
                        "MOT-001",
                        "https://storage.mpms.mp.br/motoristas/carlos-silva.png",
                        "(67) 99123-4567",
                        "(67) 3318-2001",
                        "DISPONIVEL",
                        usuarioMotorista1
                ),
                new MotoristaSeed(
                        UUID.fromString("00000005-0000-0000-0000-000000000002"),
                        "Joao Pereira",
                        locDourados,
                        "MOT-002",
                        "https://storage.mpms.mp.br/motoristas/joao-pereira.png",
                        "(67) 99234-5678",
                        "(67) 3411-3002",
                        "DISPONIVEL",
                        usuarioMotorista2
                )
        );

        for (MotoristaSeed s : seeds) {
            jdbcTemplate.update(
                    INSERT_MOTORISTA_SQL,
                    s.id(),
                    s.displayNome(),
                    s.fkLocalidade(),
                    s.matricula(),
                    s.foto(),
                    s.contatoPessoal(),
                    s.contatoFuncional(),
                    s.status(),
                    s.fkUsuario()
            );
        }
    }

    private record MotoristaSeed(
            UUID id,
            String displayNome,
            UUID fkLocalidade,
            String matricula,
            String foto,
            String contatoPessoal,
            String contatoFuncional,
            String status,
            UUID fkUsuario
    ) {}
}

