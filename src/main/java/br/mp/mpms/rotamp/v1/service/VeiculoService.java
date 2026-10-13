package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.BusinessException;
import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.VeiculoDetalhesResponse;
import br.mp.mpms.rotamp.v1.model.dto.VeiculoResponse;
import br.mp.mpms.rotamp.v1.model.entity.Localidade;
import br.mp.mpms.rotamp.v1.model.entity.ModeloVeiculo;
import br.mp.mpms.rotamp.v1.model.entity.Veiculo;
import br.mp.mpms.rotamp.v1.repository.LocalidadeRepository;
import br.mp.mpms.rotamp.v1.repository.ModeloVeiculoRepository;
import br.mp.mpms.rotamp.v1.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ModeloVeiculoRepository modeloVeiculoRepository;
    private final LocalidadeRepository localidadeRepository;

    @Transactional
    public VeiculoResponse cadastrar(CriarVeiculoRequest request) {
        String placaNormalizada = request.placa().trim().toUpperCase();

        if (veiculoRepository.existsByPlacaIgnoreCase(placaNormalizada)) {
            throw new ConflictException("Placa de veiculo ja cadastrada no sistema: " + placaNormalizada);
        }

        ModeloVeiculo modelo = buscarModeloAtivo(request.modeloId());
        Localidade localidade = buscarLocalidadeAtiva(request.localidadeId());

        Veiculo veiculo = Veiculo.builder()
                .placa(placaNormalizada)
                .ano(request.ano())
                .fkModelo(modelo)
                .fkLocalidade(localidade)
                .ativo(request.ativo() != null ? request.ativo() : true)
                .build();

        Veiculo salvo = veiculoRepository.save(veiculo);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public VeiculoResponse buscarPorId(UUID id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public VeiculoDetalhesResponse buscarDetalhesPorId(UUID id) {
        Veiculo veiculo = veiculoRepository.findByIdComDetalhes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado"));
        return toDetalhesResponse(veiculo);
    }

    @Transactional(readOnly = true)
    public Page<VeiculoResponse> listar(Pageable pageable) {
        return listar(pageable, new br.mp.mpms.rotamp.v1.model.dto.VeiculoFiltroRequest(null, null, null, null));
    }

    @Transactional(readOnly = true)
    public Page<VeiculoResponse> listar(Pageable pageable, br.mp.mpms.rotamp.v1.model.dto.VeiculoFiltroRequest filtro) {
        if (filtro.placa() != null && !filtro.placa().isBlank()) {
            return veiculoRepository.findByPlacaContainingIgnoreCase(filtro.placa().trim(), pageable).map(this::toResponse);
        }
        if (filtro.localidadeId() != null) {
            return veiculoRepository.findByFkLocalidadeId(filtro.localidadeId(), pageable).map(this::toResponse);
        }
        if (filtro.modeloId() != null) {
            return veiculoRepository.findByFkModeloId(filtro.modeloId(), pageable).map(this::toResponse);
        }
        if (filtro.ativo() != null) {
            return veiculoRepository.findByAtivo(filtro.ativo(), pageable).map(this::toResponse);
        }
        return veiculoRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public VeiculoResponse alterar(UUID id, AtualizarVeiculoRequest request) {
        Veiculo veiculo = buscarEntidadePorId(id);
        String placaNormalizada = request.placa().trim().toUpperCase();

        if (veiculoRepository.existsByPlacaIgnoreCaseAndIdNot(placaNormalizada, id)) {
            throw new ConflictException("Placa de veiculo ja em uso por outro veiculo: " + placaNormalizada);
        }

        ModeloVeiculo modelo = buscarModeloAtivo(request.modeloId());
        Localidade localidade = buscarLocalidadeAtiva(request.localidadeId());

        veiculo.atualizarDados(placaNormalizada, request.ano(), modelo, localidade);

        Veiculo salvo = veiculoRepository.save(veiculo);
        return toResponse(salvo);
    }

    @Transactional
    public void inativar(UUID id) {
        Veiculo veiculo = buscarEntidadePorId(id);
        veiculo.inativar();
        veiculoRepository.save(veiculo);
    }

    @Transactional
    public VeiculoResponse ativar(UUID id) {
        Veiculo veiculo = buscarEntidadePorId(id);
        veiculo.ativar();
        Veiculo salvo = veiculoRepository.save(veiculo);
        return toResponse(salvo);
    }

    @Transactional
    public VeiculoResponse vincularLocalidade(UUID id, UUID localidadeId) {
        Veiculo veiculo = buscarEntidadePorId(id);
        Localidade localidade = buscarLocalidadeAtiva(localidadeId);

        veiculo.vincularLocalidade(localidade);
        Veiculo salvo = veiculoRepository.save(veiculo);
        return toResponse(salvo);
    }

    private Veiculo buscarEntidadePorId(UUID id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado"));
    }

    private ModeloVeiculo buscarModeloAtivo(UUID modeloId) {
        ModeloVeiculo modelo = modeloVeiculoRepository.findById(modeloId)
                .orElseThrow(() -> new ResourceNotFoundException("Modelo de veiculo nao encontrado"));
        if (!modelo.isAtivo()) {
            throw new BusinessException(
                    "Nao e possivel vincular um modelo de veiculo inativo",
                    HttpStatus.valueOf(422)
            );
        }
        return modelo;
    }

    private Localidade buscarLocalidadeAtiva(UUID localidadeId) {
        Localidade localidade = localidadeRepository.findById(localidadeId)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade de origem nao encontrada"));
        if (!localidade.isAtivo()) {
            throw new BusinessException(
                    "Nao e possivel vincular um veiculo a uma localidade inativa",
                    HttpStatus.valueOf(422)
            );
        }
        return localidade;
    }

    private VeiculoResponse toResponse(Veiculo veiculo) {
        return new VeiculoResponse(
                veiculo.getId(),
                veiculo.getFkLocalidade().getId(),
                veiculo.getFkModelo().getId(),
                veiculo.getPlaca(),
                veiculo.getAno(),
                veiculo.isAtivo()
        );
    }

    private VeiculoDetalhesResponse toDetalhesResponse(Veiculo veiculo) {
        return new VeiculoDetalhesResponse(
                veiculo.getId(),
                veiculo.getFkLocalidade().getId(),
                veiculo.getFkLocalidade().getNome(),
                veiculo.getFkModelo().getId(),
                veiculo.getFkModelo().getNomeMarca(),
                veiculo.getFkModelo().getModelo(),
                veiculo.getFkModelo().getNumPassageiro(),
                veiculo.getPlaca(),
                veiculo.getAno(),
                veiculo.isAtivo()
        );
    }
}
