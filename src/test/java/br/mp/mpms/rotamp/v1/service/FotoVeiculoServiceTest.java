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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FotoVeiculoServiceTest {

    @Mock
    private FotoVeiculoRepository fotoVeiculoRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private FotoVeiculoService fotoVeiculoService;

    @Test
    @DisplayName("deve adicionar foto ao veiculo com sucesso quando upload multipart valido")
    void deve_adicionarFotoAoVeiculoComSucesso_quando_uploadMultipartValido() {
        UUID veiculoId = UUID.randomUUID();
        UUID fotoId = UUID.randomUUID();
        Veiculo veiculo = Veiculo.builder().id(veiculoId).build();
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3});
        String urlGerada = "http://127.0.0.1:10000/devstoreaccount1/fotos-veiculos/veiculos/" + veiculoId + "/foto.jpg";

        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(storageService.armazenarFoto(veiculoId, arquivo)).thenReturn(urlGerada);
        when(fotoVeiculoRepository.save(any(FotoVeiculo.class))).thenAnswer(inv -> {
            FotoVeiculo f = inv.getArgument(0);
            f.setId(fotoId);
            return f;
        });

        FotoVeiculoResponse response = fotoVeiculoService.adicionar(veiculoId, arquivo);

        assertThat(response.id()).isEqualTo(fotoId);
        assertThat(response.foto()).isEqualTo(urlGerada);
        assertThat(response.veiculoId()).isEqualTo(veiculoId);
        verify(storageService).armazenarFoto(veiculoId, arquivo);
    }

    @Test
    @DisplayName("deve adicionar foto ao veiculo com sucesso quando enviado por request URL")
    void deve_adicionarFotoAoVeiculoComSucesso_quando_enviadoPorRequestUrl() {
        UUID veiculoId = UUID.randomUUID();
        UUID fotoId = UUID.randomUUID();
        Veiculo veiculo = Veiculo.builder().id(veiculoId).build();
        CriarFotoVeiculoRequest request = new CriarFotoVeiculoRequest("http://cdn.mpms.mp.br/veiculo1.jpg");

        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(fotoVeiculoRepository.save(any(FotoVeiculo.class))).thenAnswer(inv -> {
            FotoVeiculo f = inv.getArgument(0);
            f.setId(fotoId);
            return f;
        });

        FotoVeiculoResponse response = fotoVeiculoService.adicionar(veiculoId, request);

        assertThat(response.id()).isEqualTo(fotoId);
        assertThat(response.foto()).isEqualTo("http://cdn.mpms.mp.br/veiculo1.jpg");
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando veiculo inexistente ao adicionar foto")
    void deve_lancarResourceNotFoundException_quando_veiculoInexistenteAoAdicionarFoto() {
        UUID veiculoId = UUID.randomUUID();
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3});

        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fotoVeiculoService.adicionar(veiculoId, arquivo))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Veiculo nao encontrado");

        verify(fotoVeiculoRepository, never()).save(any());
        verify(storageService, never()).armazenarFoto(any(), any());
    }

    @Test
    @DisplayName("deve listar fotos do veiculo com sucesso quando veiculo existe")
    void deve_listarFotosDoVeiculoComSucesso_quando_veiculoExiste() {
        UUID veiculoId = UUID.randomUUID();
        Veiculo veiculo = Veiculo.builder().id(veiculoId).build();
        FotoVeiculo f1 = FotoVeiculo.builder().id(UUID.randomUUID()).foto("http://f1.jpg").fkVeiculo(veiculo).build();
        FotoVeiculo f2 = FotoVeiculo.builder().id(UUID.randomUUID()).foto("http://f2.jpg").fkVeiculo(veiculo).build();

        when(veiculoRepository.existsById(veiculoId)).thenReturn(true);
        when(fotoVeiculoRepository.findByFkVeiculoId(veiculoId)).thenReturn(List.of(f1, f2));

        List<FotoVeiculoResponse> result = fotoVeiculoService.listarPorVeiculo(veiculoId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).foto()).isEqualTo("http://f1.jpg");
    }

    @Test
    @DisplayName("deve substituir foto do veiculo no storage e atualizar registro no banco com sucesso")
    void deve_substituirFotoDoVeiculoNoStorageEAtualizarRegistroNoBancoComSucesso() {
        UUID veiculoId = UUID.randomUUID();
        UUID fotoId = UUID.randomUUID();
        Veiculo veiculo = Veiculo.builder().id(veiculoId).build();
        String urlAntiga = "http://127.0.0.1:10000/devstoreaccount1/fotos-veiculos/antiga.jpg";
        String urlNova = "http://127.0.0.1:10000/devstoreaccount1/fotos-veiculos/nova.jpg";
        MockMultipartFile novoArquivo = new MockMultipartFile("arquivo", "nova.jpg", "image/jpeg", new byte[]{4, 5, 6});

        FotoVeiculo fotoExistente = FotoVeiculo.builder().id(fotoId).foto(urlAntiga).fkVeiculo(veiculo).build();

        when(veiculoRepository.existsById(veiculoId)).thenReturn(true);
        when(fotoVeiculoRepository.findByIdAndFkVeiculoId(fotoId, veiculoId)).thenReturn(Optional.of(fotoExistente));
        when(storageService.armazenarFoto(veiculoId, novoArquivo)).thenReturn(urlNova);
        when(fotoVeiculoRepository.save(any(FotoVeiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        FotoVeiculoResponse response = fotoVeiculoService.alterar(veiculoId, fotoId, novoArquivo);

        assertThat(response.foto()).isEqualTo(urlNova);
        verify(storageService).removerFoto(urlAntiga);
    }

    @Test
    @DisplayName("deve remover foto do veiculo no banco e expurgar arquivo do storage com sucesso")
    void deve_removerFotoDoVeiculoNoBancoEExpurgarArquivoDoStorageComSucesso() {
        UUID veiculoId = UUID.randomUUID();
        UUID fotoId = UUID.randomUUID();
        Veiculo veiculo = Veiculo.builder().id(veiculoId).build();
        String url = "http://127.0.0.1:10000/devstoreaccount1/fotos-veiculos/foto1.jpg";
        FotoVeiculo foto = FotoVeiculo.builder().id(fotoId).foto(url).fkVeiculo(veiculo).build();

        when(veiculoRepository.existsById(veiculoId)).thenReturn(true);
        when(fotoVeiculoRepository.findByIdAndFkVeiculoId(fotoId, veiculoId)).thenReturn(Optional.of(foto));

        fotoVeiculoService.remover(veiculoId, fotoId);

        verify(fotoVeiculoRepository).delete(foto);
        verify(storageService).removerFoto(url);
    }

    @Test
    @DisplayName("deve remover foto por fotoId diretamente com sucesso")
    void deve_removerFotoPorFotoIdDiretamenteComSucesso() {
        UUID fotoId = UUID.randomUUID();
        String url = "http://127.0.0.1:10000/devstoreaccount1/fotos-veiculos/foto1.jpg";
        FotoVeiculo foto = FotoVeiculo.builder().id(fotoId).foto(url).build();

        when(fotoVeiculoRepository.findById(fotoId)).thenReturn(Optional.of(foto));

        fotoVeiculoService.remover(fotoId);

        verify(fotoVeiculoRepository).delete(foto);
        verify(storageService).removerFoto(url);
    }

    @Test
    @DisplayName("deve alterar foto por fotoId diretamente com sucesso")
    void deve_alterarFotoPorFotoIdDiretamenteComSucesso() {
        UUID fotoId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        Veiculo veiculo = Veiculo.builder().id(veiculoId).build();
        String urlAntiga = "http://antiga.jpg";
        String urlNova = "http://nova.jpg";
        MockMultipartFile novoArquivo = new MockMultipartFile("arquivo", "nova.jpg", "image/jpeg", new byte[]{1});
        FotoVeiculo foto = FotoVeiculo.builder().id(fotoId).foto(urlAntiga).fkVeiculo(veiculo).build();

        when(fotoVeiculoRepository.findById(fotoId)).thenReturn(Optional.of(foto));
        when(storageService.armazenarFoto(veiculoId, novoArquivo)).thenReturn(urlNova);
        when(fotoVeiculoRepository.save(any(FotoVeiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        FotoVeiculoResponse response = fotoVeiculoService.alterar(fotoId, novoArquivo);

        assertThat(response.foto()).isEqualTo(urlNova);
        verify(storageService).removerFoto(urlAntiga);
    }
}