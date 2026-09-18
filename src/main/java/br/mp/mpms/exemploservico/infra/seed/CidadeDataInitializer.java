package br.mp.mpms.exemploservico.infra.seed;

import br.mp.mpms.exemploservico.v1.model.entity.Cidade;
import br.mp.mpms.exemploservico.v1.repository.CidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CidadeDataInitializer implements CommandLineRunner {

    private final CidadeRepository cidadeRepository;

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
        if (cidadeRepository.findById(seed.id()).isPresent() || cidadeRepository.existsByCodigo(seed.codigo())) {
            log.debug("Seed de cidade ja existente (id={}, codigo={}). Ignorando insercao.", seed.id(), seed.codigo());
            return;
        }

        Cidade cidade = Cidade.builder()
                .id(seed.id())
                .nome(seed.nome())
                .codigo(seed.codigo())
                .ativo(seed.ativo())
                .build();

        cidadeRepository.save(cidade);
        log.info("Seed de cidade cadastrada com sucesso: id={}, nome='{}', codigo='{}', ativo={}",
                seed.id(), seed.nome(), seed.codigo(), seed.ativo());
    }

    private record CidadeSeed(UUID id, String nome, String codigo, boolean ativo) {}
}
