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
@Order(60)
@RequiredArgsConstructor
public class SolicitacaoAtendimentoDataInitializer implements CommandLineRunner {

    private static final String INSERT_TIPO_SQL = """
            INSERT INTO tipo (id, nome, descricao, ativo)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private static final String INSERT_SOLICITACAO_SQL = """
            INSERT INTO solicitacao_atendimento (
                id, fk_tipo, fk_usuario, fk_localidade_origem, fk_localidade_destino,
                status, prioridade, num_passageiro, justificativa, data_pedido, data_criacao, data_analise
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados iniciais (seeds) de tipos e solicitacoes de atendimento...");
        carregarTipos();
        carregarSolicitacoes();
        log.info("Carga de dados iniciais de solicitacoes concluida com sucesso.");
    }

    private void carregarTipos() {
        List<TipoSeed> seeds = List.of(
                new TipoSeed(UUID.fromString("00000006-0000-0000-0000-000000000001"), "Audiência Judicial", "Deslocamento oficial para participação em audiências e sessões de julgamento", true),
                new TipoSeed(UUID.fromString("00000006-0000-0000-0000-000000000002"), "Diligência Externa", "Atividades de fiscalização, inspeção e diligências externas ministeriais", true),
                new TipoSeed(UUID.fromString("00000006-0000-0000-0000-000000000003"), "Reunião Administrativa", "Participação em reuniões institucionais e comissões técnicas", true)
        );
        for (TipoSeed s : seeds) {
            jdbcTemplate.update(INSERT_TIPO_SQL, s.id(), s.nome(), s.descricao(), s.ativo());
        }
    }

    private void carregarSolicitacoes() {
        UUID tipoAudiencia = UUID.fromString("00000006-0000-0000-0000-000000000001");
        UUID tipoDiligencia = UUID.fromString("00000006-0000-0000-0000-000000000002");
        UUID tipoReuniao = UUID.fromString("00000006-0000-0000-0000-000000000003");

        UUID usuarioMembro = UUID.fromString("00000001-0000-0000-0000-000000000003");
        UUID usuarioSolicitante = UUID.fromString("00000001-0000-0000-0000-000000000004");

        UUID locCidadania = UUID.fromString("d1000001-0000-0000-0000-000000000001");
        UUID locReservado = UUID.fromString("d1000048-0000-0000-0000-000000000048");

        LocalDateTime agora = LocalDateTime.now();

        List<SolicitacaoSeed> seeds = List.of(
                new SolicitacaoSeed(
                        UUID.fromString("00000007-0000-0000-0000-000000000001"),
                        tipoAudiencia,
                        usuarioMembro,
                        locCidadania,
                        locReservado,
                        "ACEITO",
                        "ALTA",
                        2,
                        "Audiência e oitiva reservada de testemunhas",
                        agora.minusDays(2).withHour(8).withMinute(0),
                        agora.minusDays(5),
                        agora.minusDays(3)
                ),
                new SolicitacaoSeed(
                        UUID.fromString("00000007-0000-0000-0000-000000000002"),
                        tipoDiligencia,
                        usuarioMembro,
                        locReservado,
                        locCidadania,
                        "ACEITO",
                        "ALTA",
                        1,
                        "Inspeção técnica e entrega de relatórios",
                        agora.withHour(7).withMinute(0),
                        agora.minusDays(2),
                        agora.minusDays(1)
                ),
                new SolicitacaoSeed(
                        UUID.fromString("00000007-0000-0000-0000-000000000003"),
                        tipoReuniao,
                        usuarioSolicitante,
                        locCidadania,
                        locReservado,
                        "ACEITO",
                        "REGULAR",
                        3,
                        "Alinhamento setorial reservado",
                        agora.plusDays(1).withHour(8).withMinute(0),
                        agora.minusDays(1),
                        agora
                ),
                new SolicitacaoSeed(
                        UUID.fromString("00000007-0000-0000-0000-000000000004"),
                        tipoDiligencia,
                        usuarioSolicitante,
                        locReservado,
                        locCidadania,
                        "PENDENTE",
                        "REGULAR",
                        2,
                        "Apoio operacional e vistoria preliminar",
                        agora.plusDays(3).withHour(9).withMinute(0),
                        agora,
                        null
                )
        );

        for (SolicitacaoSeed s : seeds) {
            jdbcTemplate.update(
                    INSERT_SOLICITACAO_SQL,
                    s.id(),
                    s.fkTipo(),
                    s.fkUsuario(),
                    s.fkOrigem(),
                    s.fkDestino(),
                    s.status(),
                    s.prioridade(),
                    s.numPassageiro(),
                    s.justificativa(),
                    s.dataPedido(),
                    s.dataCriacao(),
                    s.dataAnalise()
            );
        }
    }

    private record TipoSeed(UUID id, String nome, String descricao, boolean ativo) {}
    private record SolicitacaoSeed(
            UUID id,
            UUID fkTipo,
            UUID fkUsuario,
            UUID fkOrigem,
            UUID fkDestino,
            String status,
            String prioridade,
            int numPassageiro,
            String justificativa,
            LocalDateTime dataPedido,
            LocalDateTime dataCriacao,
            LocalDateTime dataAnalise
    ) {}
}

