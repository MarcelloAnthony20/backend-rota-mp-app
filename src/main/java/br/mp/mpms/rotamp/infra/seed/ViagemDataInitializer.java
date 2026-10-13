package br.mp.mpms.rotamp.infra.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Order(70)
@RequiredArgsConstructor
public class ViagemDataInitializer implements CommandLineRunner {

    private static final String INSERT_VIAGEM_SQL = """
            INSERT INTO viagem (id, fk_motorista, fk_solicitacao, fk_veiculo, status, data_inicio, data_conclusao)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados iniciais (seeds) de viagens realizadas e programadas...");
        carregarViagens();
        log.info("Carga de dados iniciais de viagens concluida com sucesso.");
    }

    private void carregarViagens() {
        UUID motoristaCarlos = UUID.fromString("00000005-0000-0000-0000-000000000001");
        UUID motoristaJoao = UUID.fromString("00000005-0000-0000-0000-000000000002");

        UUID solicitacaoAudiencia = UUID.fromString("00000007-0000-0000-0000-000000000001");
        UUID solicitacaoDiligencia = UUID.fromString("00000007-0000-0000-0000-000000000002");
        UUID solicitacaoReuniao = UUID.fromString("00000007-0000-0000-0000-000000000003");

        UUID veiculoCorolla = UUID.fromString("00000004-0000-0000-0000-000000000001");
        UUID veiculoHilux = UUID.fromString("00000004-0000-0000-0000-000000000002");
        UUID veiculoSpin = UUID.fromString("00000004-0000-0000-0000-000000000003");

        LocalDateTime agora = LocalDateTime.now();

        List<ViagemSeed> seeds = List.of(
                new ViagemSeed(
                        UUID.fromString("00000008-0000-0000-0000-000000000001"),
                        motoristaCarlos,
                        solicitacaoAudiencia,
                        veiculoCorolla,
                        "CONCLUIDA",
                        agora.minusDays(2).withHour(8).withMinute(0).withSecond(0),
                        agora.minusDays(2).withHour(12).withMinute(30).withSecond(0)
                ),
                new ViagemSeed(
                        UUID.fromString("00000008-0000-0000-0000-000000000002"),
                        motoristaJoao,
                        solicitacaoDiligencia,
                        veiculoHilux,
                        "EM_ROTA",
                        agora.withHour(7).withMinute(0).withSecond(0),
                        null
                ),
                new ViagemSeed(
                        UUID.fromString("00000008-0000-0000-0000-000000000003"),
                        motoristaCarlos,
                        solicitacaoReuniao,
                        veiculoSpin,
                        "AGENDADA",
                        agora.plusDays(1).withHour(8).withMinute(0).withSecond(0),
                        null
                )
        );

        for (ViagemSeed s : seeds) {
            jdbcTemplate.update(
                    INSERT_VIAGEM_SQL,
                    s.id(),
                    s.fkMotorista(),
                    s.fkSolicitacao(),
                    s.fkVeiculo(),
                    s.status(),
                    s.dataInicio(),
                    s.dataConclusao()
            );
        }
    }

    private record ViagemSeed(
            UUID id,
            UUID fkMotorista,
            UUID fkSolicitacao,
            UUID fkVeiculo,
            String status,
            LocalDateTime dataInicio,
            LocalDateTime dataConclusao
    ) {}
}

