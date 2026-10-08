package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.infra.storage.StorageService;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarModeloVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarModeloVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.ModeloVeiculoResponse;
import br.mp.mpms.rotamp.v1.model.entity.Marca;
import br.mp.mpms.rotamp.v1.model.entity.ModeloVeiculo;
import br.mp.mpms.rotamp.v1.repository.MarcaRepository;
import br.mp.mpms.rotamp.v1.repository.ModeloVeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

/**
 * Serviço de regras de negócio para modelos de veículos (HU-07)
 * e gestão da entidade fraca Marca vinculada.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModeloVeiculoService {

    private final ModeloVeiculoRepository modeloVeiculoRepository;
    private final MarcaRepository marcaRepository;
    private final StorageService storageService;

    @Transactional
    public ModeloVeiculoResponse cadastrar(CriarModeloVeiculoRequest request) {
        return cadastrar(request, null);
    }

    @Transactional
    public ModeloVeiculoResponse cadastrar(CriarModeloVeiculoRequest request, MultipartFile arquivoLogo) {
        String nomeMarca = request.marca().trim();
        String nomeModelo = request.modelo().trim();

        if (modeloVeiculoRepository.existsByMarcaNomeIgnoreCaseAndModeloIgnoreCase(nomeMarca, nomeModelo)) {
            throw new ConflictException("Modelo de veiculo ja cadastrado para esta marca e modelo");
        }

        Marca marca = obterOuCriarMarca(nomeMarca, request.logo(), arquivoLogo);

        ModeloVeiculo modelo = ModeloVeiculo.builder()
                .marca(marca)
                .modelo(nomeModelo)
                .numPassageiro(request.numPassageiro())
                .ativo(request.ativo() == null ? true : request.ativo())
                .build();

        ModeloVeiculo salvo = modeloVeiculoRepository.save(modelo);
        log.info("Modelo de veículo cadastrado com sucesso: ID={}, Modelo={}, Marca={}",
                salvo.getId(), salvo.getModelo(), marca.getNome());

        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public ModeloVeiculoResponse buscarPorId(UUID id) {
        ModeloVeiculo modelo = modeloVeiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de veiculo nao encontrado"));
        return toResponse(modelo);
    }

    @Transactional(readOnly = true)
    public Page<ModeloVeiculoResponse> listar(Pageable pageable) {
        return modeloVeiculoRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ModeloVeiculoResponse> listar(Pageable pageable, Boolean ativo, String termo) {
        if (ativo != null && StringUtils.hasText(termo)) {
            return modeloVeiculoRepository.findByAtivoAndTermo(ativo, termo.trim(), pageable).map(this::toResponse);
        } else if (ativo != null) {
            return modeloVeiculoRepository.findByAtivo(ativo, pageable).map(this::toResponse);
        } else if (StringUtils.hasText(termo)) {
            return modeloVeiculoRepository.findByTermo(termo.trim(), pageable).map(this::toResponse);
        }
        return modeloVeiculoRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public ModeloVeiculoResponse alterar(UUID id, AtualizarModeloVeiculoRequest request) {
        ModeloVeiculo modeloExistente = modeloVeiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de veiculo nao encontrado"));

        String nomeMarcaNova = request.marca().trim();
        String nomeModeloNovo = request.modelo().trim();

        if (modeloVeiculoRepository.existsByMarcaNomeIgnoreCaseAndModeloIgnoreCaseAndIdNot(nomeMarcaNova, nomeModeloNovo, id)) {
            throw new ConflictException("Ja existe outro modelo com este nome para a marca informada");
        }

        Marca marcaAntiga = modeloExistente.getMarca();
        Marca marcaNova = obterOuCriarMarca(nomeMarcaNova, request.logo(), null);

        modeloExistente.setMarca(marcaNova);
        modeloExistente.setModelo(nomeModeloNovo);
        modeloExistente.setNumPassageiro(request.numPassageiro());

        ModeloVeiculo salvo = modeloVeiculoRepository.save(modeloExistente);

        if (marcaAntiga != null && !marcaAntiga.getId().equals(marcaNova.getId())) {
            limparMarcaSeOrfa(marcaAntiga);
        }

        return toResponse(salvo);
    }

    @Transactional
    public void inativar(UUID id) {
        ModeloVeiculo modelo = modeloVeiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de veiculo nao encontrado"));
        modelo.setAtivo(false);
        modeloVeiculoRepository.save(modelo);
    }

    @Transactional
    public ModeloVeiculoResponse ativar(UUID id) {
        ModeloVeiculo modelo = modeloVeiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de veiculo nao encontrado"));
        modelo.setAtivo(true);
        return toResponse(modeloVeiculoRepository.save(modelo));
    }

    @Transactional
    public ModeloVeiculoResponse atualizarLogo(UUID id, MultipartFile arquivo) {
        ModeloVeiculo modelo = modeloVeiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de veiculo nao encontrado"));

        Marca marca = modelo.getMarca();
        String logoAntiga = marca.getLogo();

        String urlNova = storageService.armazenarLogo(marca.getNome(), arquivo);
        marca.setLogo(urlNova);
        marcaRepository.save(marca);

        if (StringUtils.hasText(logoAntiga)) {
            storageService.removerLogo(logoAntiga);
        }

        return toResponse(modelo);
    }

    @Transactional
    public ModeloVeiculoResponse atualizarLogoPorUrl(UUID id, String urlLogo) {
        ModeloVeiculo modelo = modeloVeiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de veiculo nao encontrado"));

        Marca marca = modelo.getMarca();
        String logoAntiga = marca.getLogo();

        marca.setLogo(urlLogo);
        marcaRepository.save(marca);

        if (StringUtils.hasText(logoAntiga) && !logoAntiga.equalsIgnoreCase(urlLogo)) {
            storageService.removerLogo(logoAntiga);
        }

        return toResponse(modelo);
    }

    private Marca obterOuCriarMarca(String nomeMarca, String urlLogo, MultipartFile arquivoLogo) {
        Optional<Marca> marcaOpt = marcaRepository.findByNomeIgnoreCase(nomeMarca);
        if (marcaOpt.isPresent()) {
            Marca existente = marcaOpt.get();
            if (arquivoLogo != null && !arquivoLogo.isEmpty()) {
                String logoAntiga = existente.getLogo();
                String novaUrl = storageService.armazenarLogo(existente.getNome(), arquivoLogo);
                existente.setLogo(novaUrl);
                marcaRepository.save(existente);
                if (StringUtils.hasText(logoAntiga)) {
                    storageService.removerLogo(logoAntiga);
                }
            } else if (StringUtils.hasText(urlLogo)) {
                existente.setLogo(urlLogo);
                marcaRepository.save(existente);
            }
            return existente;
        }

        String logoDefinida = urlLogo;
        if (arquivoLogo != null && !arquivoLogo.isEmpty()) {
            logoDefinida = storageService.armazenarLogo(nomeMarca, arquivoLogo);
        }

        Marca nova = Marca.builder()
                .nome(nomeMarca)
                .logo(logoDefinida)
                .build();
        return marcaRepository.save(nova);
    }

    private void limparMarcaSeOrfa(Marca marca) {
        if (marca != null && marca.getId() != null) {
            boolean temOutrosModelos = modeloVeiculoRepository.existsByMarcaId(marca.getId());
            if (!temOutrosModelos) {
                if (StringUtils.hasText(marca.getLogo())) {
                    storageService.removerLogo(marca.getLogo());
                }
                marcaRepository.delete(marca);
                log.info("Marca órfã '{}' (ID={}) removida com sucesso.", marca.getNome(), marca.getId());
            }
        }
    }

    private ModeloVeiculoResponse toResponse(ModeloVeiculo modelo) {
        String nomeMarca = modelo.getMarca() != null ? modelo.getMarca().getNome() : null;
        String logoMarca = modelo.getMarca() != null ? modelo.getMarca().getLogo() : null;

        return new ModeloVeiculoResponse(
                modelo.getId(),
                nomeMarca,
                modelo.getModelo(),
                modelo.getNumPassageiro(),
                logoMarca,
                modelo.isAtivo()
        );
    }
}