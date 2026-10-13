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
@Order(40)
@RequiredArgsConstructor
public class VeiculoDataInitializer implements CommandLineRunner {

    private static final String INSERT_MARCA_SQL = """
            INSERT INTO marca (id, nome, logo)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private static final String INSERT_MODELO_SQL = """
            INSERT INTO modelo_veiculo (id, fk_marca, marca, modelo, num_passageiro, ativo)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private static final String INSERT_VEICULO_SQL = """
            INSERT INTO veiculo (id, fk_modelo, fk_localidade, placa, ano, ativo)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados iniciais (seeds) de marcas, modelos e veiculos...");
        carregarMarcas();
        carregarModelos();
        carregarVeiculos();
        log.info("Carga de dados iniciais de veiculos concluida com sucesso.");
    }

    private void carregarMarcas() {
        List<MarcaSeed> seeds = List.of(
                new MarcaSeed(UUID.fromString("00000002-0000-0000-0000-000000000001"), "Toyota", "https://storage.mpms.mp.br/marcas/toyota.png"),
                new MarcaSeed(UUID.fromString("00000002-0000-0000-0000-000000000002"), "Chevrolet", "https://storage.mpms.mp.br/marcas/chevrolet.png"),
                new MarcaSeed(UUID.fromString("00000002-0000-0000-0000-000000000003"), "Renault", "https://storage.mpms.mp.br/marcas/renault.png")
        );
        for (MarcaSeed s : seeds) {
            jdbcTemplate.update(INSERT_MARCA_SQL, s.id(), s.nome(), s.logo());
        }
    }

    private void carregarModelos() {
        UUID marcaToyota = UUID.fromString("00000002-0000-0000-0000-000000000001");
        UUID marcaChevrolet = UUID.fromString("00000002-0000-0000-0000-000000000002");
        UUID marcaRenault = UUID.fromString("00000002-0000-0000-0000-000000000003");

        List<ModeloSeed> seeds = List.of(
                new ModeloSeed(UUID.fromString("00000003-0000-0000-0000-000000000001"), marcaToyota, "Toyota", "Corolla Sedan", 5, true),
                new ModeloSeed(UUID.fromString("00000003-0000-0000-0000-000000000002"), marcaToyota, "Toyota", "Hilux 4x4", 5, true),
                new ModeloSeed(UUID.fromString("00000003-0000-0000-0000-000000000003"), marcaChevrolet, "Chevrolet", "Spin Minivan", 7, true),
                new ModeloSeed(UUID.fromString("00000003-0000-0000-0000-000000000004"), marcaRenault, "Renault", "Duster SUV", 5, true)
        );
        for (ModeloSeed s : seeds) {
            jdbcTemplate.update(INSERT_MODELO_SQL, s.id(), s.fkMarca(), s.marca(), s.modelo(), s.numPassageiro(), s.ativo());
        }
    }

    private void carregarVeiculos() {
        UUID modeloCorolla = UUID.fromString("00000003-0000-0000-0000-000000000001");
        UUID modeloHilux = UUID.fromString("00000003-0000-0000-0000-000000000002");
        UUID modeloSpin = UUID.fromString("00000003-0000-0000-0000-000000000003");

        UUID locCampoGrande = UUID.fromString("d1000001-0000-0000-0000-000000000001");
        UUID locDourados = UUID.fromString("d1000002-0000-0000-0000-000000000002");
        UUID locTresLagoas = UUID.fromString("d1000005-0000-0000-0000-000000000005");

        List<VeiculoSeed> seeds = List.of(
                new VeiculoSeed(UUID.fromString("00000004-0000-0000-0000-000000000001"), modeloCorolla, locCampoGrande, "RPA1A23", 2024, true),
                new VeiculoSeed(UUID.fromString("00000004-0000-0000-0000-000000000002"), modeloHilux, locDourados, "RPB2B34", 2023, true),
                new VeiculoSeed(UUID.fromString("00000004-0000-0000-0000-000000000003"), modeloSpin, locTresLagoas, "RPC3C45", 2024, true)
        );
        for (VeiculoSeed s : seeds) {
            jdbcTemplate.update(INSERT_VEICULO_SQL, s.id(), s.fkModelo(), s.fkLocalidade(), s.placa(), s.ano(), s.ativo());
        }
    }

    private record MarcaSeed(UUID id, String nome, String logo) {}
    private record ModeloSeed(UUID id, UUID fkMarca, String marca, String modelo, int numPassageiro, boolean ativo) {}
    private record VeiculoSeed(UUID id, UUID fkModelo, UUID fkLocalidade, String placa, int ano, boolean ativo) {}
}

