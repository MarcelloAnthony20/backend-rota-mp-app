package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.infra.storage.StorageService;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarFotoVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarFotoVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.FotoVeiculoResponse;
import br.mp.mpms.rotamp.v1.model.entity.FotoVeiculo;
import br.mp.mpms.rotamp.v1.model.entity.Veiculo;
import br.mp.mpms.rotamp.v1.repository.FotoVeiculoRepository;
import br.mp.mpms.rotamp.v1.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FotoVeiculoService {

    private final FotoVeiculoRepository fotoVeiculoRepository;
    private final VeiculoRepository veiculoRepository;
    private final StorageService storageService;

    @Transactional
    public FotoVeiculoResponse adicionar(UUID veiculoId, MultipartFile arquivo) {
        Veiculo veiculo = buscarVeiculoPorId(veiculoId);

        String url = storageService.armazenarFoto(veiculoId, arquivo);

        FotoVeiculo foto = FotoVeiculo.builder()
                .foto(url)
                .fkVeiculo(veiculo)
                .build();

        FotoVeiculo salva = fotoVeiculoRepository.save(foto);
        return toResponse(salva);
    }

    @Transactional
    public FotoVeiculoResponse adicionar(UUID veiculoId, CriarFotoVeiculoRequest request) {
        Veiculo veiculo = buscarVeiculoPorId(veiculoId);

        FotoVeiculo foto = FotoVeiculo.builder()
                .foto(request.foto().trim())
                .fkVeiculo(veiculo)
                .build();

        FotoVeiculo salva = fotoVeiculoRepository.save(foto);
        return toResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<FotoVeiculoResponse> listarPorVeiculo(UUID veiculoId) {
        garantirVeiculoExiste(veiculoId);
        return fotoVeiculoRepository.findByFkVeiculoId(veiculoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<FotoVeiculoResponse> listarPorVeiculo(UUID veiculoId, Pageable pageable) {
        garantirVeiculoExiste(veiculoId);
        return fotoVeiculoRepository.findByFkVeiculoId(veiculoId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public FotoVeiculoResponse buscarPorIdEVeiculo(UUID veiculoId, UUID fotoId) {
        garantirVeiculoExiste(veiculoId);
        FotoVeiculo foto = fotoVeiculoRepository.findByIdAndFkVeiculoId(fotoId, veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto de veiculo nao encontrada para o veiculo informado"));
        return toResponse(foto);
    }

    @Transactional
    public FotoVeiculoResponse alterar(UUID veiculoId, UUID fotoId, MultipartFile novoArquivo) {
        garantirVeiculoExiste(veiculoId);
        FotoVeiculo foto = fotoVeiculoRepository.findByIdAndFkVeiculoId(fotoId, veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto de veiculo nao encontrada para o veiculo informado"));

        String fotoAntiga = foto.getFoto();
        String novaUrl = storageService.armazenarFoto(veiculoId, novoArquivo);

        foto.setFoto(novaUrl);
        FotoVeiculo salva = fotoVeiculoRepository.save(foto);

        storageService.removerFoto(fotoAntiga);
        return toResponse(salva);
    }

    @Transactional
    public FotoVeiculoResponse alterar(UUID veiculoId, UUID fotoId, AtualizarFotoVeiculoRequest request) {
        garantirVeiculoExiste(veiculoId);
        FotoVeiculo foto = fotoVeiculoRepository.findByIdAndFkVeiculoId(fotoId, veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto de veiculo nao encontrada para o veiculo informado"));

        foto.setFoto(request.foto().trim());
        FotoVeiculo salva = fotoVeiculoRepository.save(foto);
        return toResponse(salva);
    }

    @Transactional
    public void remover(UUID veiculoId, UUID fotoId) {
        garantirVeiculoExiste(veiculoId);
        FotoVeiculo foto = fotoVeiculoRepository.findByIdAndFkVeiculoId(fotoId, veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto de veiculo nao encontrada para o veiculo informado"));

        String urlParaRemover = foto.getFoto();
        fotoVeiculoRepository.delete(foto);
        storageService.removerFoto(urlParaRemover);
    }

    @Transactional
    public void remover(UUID fotoId) {
        FotoVeiculo foto = fotoVeiculoRepository.findById(fotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto de veiculo nao encontrada"));

        String urlParaRemover = foto.getFoto();
        fotoVeiculoRepository.delete(foto);
        storageService.removerFoto(urlParaRemover);
    }

    @Transactional
    public FotoVeiculoResponse alterar(UUID fotoId, MultipartFile novoArquivo) {
        FotoVeiculo foto = fotoVeiculoRepository.findById(fotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto de veiculo nao encontrada"));

        String fotoAntiga = foto.getFoto();
        String novaUrl = storageService.armazenarFoto(foto.getFkVeiculo().getId(), novoArquivo);

        foto.setFoto(novaUrl);
        FotoVeiculo salva = fotoVeiculoRepository.save(foto);

        storageService.removerFoto(fotoAntiga);
        return toResponse(salva);
    }

    @Transactional
    public FotoVeiculoResponse alterar(UUID fotoId, AtualizarFotoVeiculoRequest request) {
        FotoVeiculo foto = fotoVeiculoRepository.findById(fotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto de veiculo nao encontrada"));

        foto.setFoto(request.foto().trim());
        FotoVeiculo salva = fotoVeiculoRepository.save(foto);
        return toResponse(salva);
    }

    @Transactional(readOnly = true)
    public FotoVeiculoResponse buscarPorId(UUID id) {
        FotoVeiculo foto = fotoVeiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto de veiculo nao encontrada"));
        return toResponse(foto);
    }

    @Transactional(readOnly = true)
    public Page<FotoVeiculoResponse> listar(Pageable pageable) {
        return fotoVeiculoRepository.findAll(pageable).map(this::toResponse);
    }

    private Veiculo buscarVeiculoPorId(UUID veiculoId) {
        return veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado"));
    }

    private void garantirVeiculoExiste(UUID veiculoId) {
        if (!veiculoRepository.existsById(veiculoId)) {
            throw new ResourceNotFoundException("Veiculo nao encontrado");
        }
    }

    private FotoVeiculoResponse toResponse(FotoVeiculo foto) {
        return new FotoVeiculoResponse(
                foto.getId(),
                foto.getFoto(),
                foto.getFkVeiculo().getId()
        );
    }
}