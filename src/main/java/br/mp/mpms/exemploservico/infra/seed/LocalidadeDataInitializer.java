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
@Order(30)
@RequiredArgsConstructor
public class LocalidadeDataInitializer implements CommandLineRunner {

    private static final String INSERT_LOCALIDADE_SQL = """
            INSERT INTO localidade (id, fk_cidade, fk_tipo_localidade, nome, local_sigiloso, ativo)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private static final String INSERT_ENDERECO_SQL = """
            INSERT INTO endereco_localidade (id, fk_localidade, fk_cidade, cep, logradouro, numero, bairro, complemento)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados iniciais (seeds) de localidades e enderecos...");

        UUID cidadeA = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        UUID tipoA = UUID.fromString("b1000001-0000-0000-0000-000000000001");

        // Localidade A (Padrao / Nao sigilosa)
        LocalidadeSeed locA = new LocalidadeSeed(
                UUID.fromString("d1000001-0000-0000-0000-000000000001"),
                cidadeA,
                tipoA,
                "Promotoria de Justiça da Cidadania",
                false,
                true
        );
        EnderecoSeed endA = new EnderecoSeed(
                UUID.fromString("e0d00001-0000-0000-0000-000000000001"),
                locA.id(),
                cidadeA,
                "79002000",
                "Rua da Paz",
                "140",
                "Jardim dos Estados",
                "Edifício Sede"
        );

        // Localidade Sigilosa
        LocalidadeSeed locSigilosa = new LocalidadeSeed(
                UUID.fromString("d1000048-0000-0000-0000-000000000048"),
                cidadeA,
                tipoA,
                "Núcleo Especial Reservado",
                true,
                true
        );
        EnderecoSeed endSigiloso = new EnderecoSeed(
                UUID.fromString("e0d00048-0000-0000-0000-000000000048"),
                locSigilosa.id(),
                cidadeA,
                "79002000",
                "Rua da Paz",
                "500",
                "Jardim dos Estados",
                "Anexo Reservado"
        );

        carregarLocalidadeSeNaoExistir(locA);
        carregarEnderecoSeNaoExistir(endA);

        carregarLocalidadeSeNaoExistir(locSigilosa);
        carregarEnderecoSeNaoExistir(endSigiloso);

        log.info("Carga de dados iniciais de localidades e enderecos concluida com sucesso.");
    }

    private void carregarLocalidadeSeNaoExistir(LocalidadeSeed seed) {
        int linhas = jdbcTemplate.update(
                INSERT_LOCALIDADE_SQL,
                seed.id(),
                seed.cidadeId(),
                seed.tipoLocalidadeId(),
                seed.nome(),
                seed.localSigiloso(),
                seed.ativo()
        );
        if (linhas > 0) {
            log.info("Seed de localidade cadastrada com sucesso: id={}, nome='{}', sigiloso={}",
                    seed.id(), seed.nome(), seed.localSigiloso());
        } else {
            log.debug("Seed de localidade ja existente (id={}). Ignorando insercao.", seed.id());
        }
    }

    private void carregarEnderecoSeNaoExistir(EnderecoSeed seed) {
        int linhas = jdbcTemplate.update(
                INSERT_ENDERECO_SQL,
                seed.id(),
                seed.localidadeId(),
                seed.cidadeId(),
                seed.cep(),
                seed.logradouro(),
                seed.numero(),
                seed.bairro(),
                seed.complemento()
        );
        if (linhas > 0) {
            log.info("Seed de endereco cadastrada com sucesso: id={}, localidadeId={}, cep='{}'",
                    seed.id(), seed.localidadeId(), seed.cep());
        } else {
            log.debug("Seed de endereco ja existente (id={}). Ignorando insercao.", seed.id());
        }
    }

    private record LocalidadeSeed(
            UUID id,
            UUID cidadeId,
            UUID tipoLocalidadeId,
            String nome,
            boolean localSigiloso,
            boolean ativo
    ) {}

    private record EnderecoSeed(
            UUID id,
            UUID localidadeId,
            UUID cidadeId,
            String cep,
            String logradouro,
            String numero,
            String bairro,
            String complemento
    ) {}
}
