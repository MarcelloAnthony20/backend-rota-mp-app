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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModeloVeiculoServiceTest {

    @Mock
    private ModeloVeiculoRepository modeloVeiculoRepository;

    @Mock
    private MarcaRepository marcaRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private ModeloVeiculoService modeloVeiculoService;

    // --- CADASTRO (HU-07.1) COM MARCA COMO ENTIDADE FRACA ---

    @Test
    @DisplayName("deve cadastrar modelo e criar nova marca quando marca nao existente")
    void deve_cadastrarModeloECriarNovaMarca_quando_marcaNaoExistente() {
        CriarModeloVeiculoRequest request = new CriarModeloVeiculoRequest("Toyota", "Corolla", 5, "http://logo.png", true);
        UUID id = UUID.randomUUID();
        UUID marcaId = UUID.randomUUID();

        when(modeloVeiculoRepository.existsByMarcaNomeIgnoreCaseAndModeloIgnoreCase("Toyota", "Corolla")).thenReturn(false);
        when(marcaRepository.findByNomeIgnoreCase("Toyota")).thenReturn(Optional.empty());
        when(marcaRepository.save(any(Marca.class))).thenAnswer(inv -> {
            Marca m = inv.getArgument(0);
            m.setId(marcaId);
            return m;
        });
        when(modeloVeiculoRepository.save(any(ModeloVeiculo.class))).thenAnswer(inv -> {
            ModeloVeiculo m = inv.getArgument(0);
            m.setId(id);
            return m;
        });

        ModeloVeiculoResponse response = modeloVeiculoService.cadastrar(request);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.marca()).isEqualTo("Toyota");
        assertThat(response.modelo()).isEqualTo("Corolla");
        assertThat(response.numPassageiro()).isEqualTo(5);
        assertThat(response.logo()).isEqualTo("http://logo.png");
        assertThat(response.ativo()).isTrue();

        verify(marcaRepository).save(any(Marca.class));
        verify(modeloVeiculoRepository).save(any(ModeloVeiculo.class));
    }

    @Test
    @DisplayName("deve cadastrar modelo reutilizando marca cadastrada quando marca ja existe")
    void deve_cadastrarModeloReutilizandoMarcaCadastrada_quando_marcaJaExiste() {
        CriarModeloVeiculoRequest request = new CriarModeloVeiculoRequest("Toyota", "Yaris", 5, null, true);
        UUID id = UUID.randomUUID();
        Marca marcaExistente = Marca.builder().id(UUID.randomUUID()).nome("Toyota").logo("http://toyota-logo.png").build();

        when(modeloVeiculoRepository.existsByMarcaNomeIgnoreCaseAndModeloIgnoreCase("Toyota", "Yaris")).thenReturn(false);
        when(marcaRepository.findByNomeIgnoreCase("Toyota")).thenReturn(Optional.of(marcaExistente));
        when(modeloVeiculoRepository.save(any(ModeloVeiculo.class))).thenAnswer(inv -> {
            ModeloVeiculo m = inv.getArgument(0);
            m.setId(id);
            return m;
        });

        ModeloVeiculoResponse response = modeloVeiculoService.cadastrar(request);

        assertThat(response.marca()).isEqualTo("Toyota");
        assertThat(response.logo()).isEqualTo("http://toyota-logo.png");
        verify(marcaRepository, never()).save(any(Marca.class));
    }

    @Test
    @DisplayName("deve cadastrar modelo e atualizar logo da marca existente quando enviado arquivo de logo")
    void deve_cadastrarModeloEAtualizarLogoDaMarcaExistente_quando_enviadoArquivoDeLogo() {
        CriarModeloVeiculoRequest request = new CriarModeloVeiculoRequest("Toyota", "RAV4", 5, null, true);
        MockMultipartFile arquivoLogo = new MockMultipartFile("logo", "toyota-novo.png", "image/png", new byte[]{1, 2});
        Marca marcaExistente = Marca.builder().id(UUID.randomUUID()).nome("Toyota").logo("http://antiga.png").build();
        String urlNova = "http://storage.azure.com/logos/toyota-novo.png";

        when(modeloVeiculoRepository.existsByMarcaNomeIgnoreCaseAndModeloIgnoreCase("Toyota", "RAV4")).thenReturn(false);
        when(marcaRepository.findByNomeIgnoreCase("Toyota")).thenReturn(Optional.of(marcaExistente));
        when(storageService.armazenarLogo("Toyota", arquivoLogo)).thenReturn(urlNova);
        when(marcaRepository.save(any(Marca.class))).thenAnswer(inv -> inv.getArgument(0));
        when(modeloVeiculoRepository.save(any(ModeloVeiculo.class))).thenAnswer(inv -> {
            ModeloVeiculo m = inv.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        ModeloVeiculoResponse response = modeloVeiculoService.cadastrar(request, arquivoLogo);

        assertThat(response.logo()).isEqualTo(urlNova);
        verify(storageService).removerLogo("http://antiga.png");
        verify(marcaRepository).save(marcaExistente);
    }

    @Test
    @DisplayName("deve lancar ConflictException quando modelo ja cadastrado para a marca")
    void deve_lancarConflictException_quando_modeloJaCadastradoParaMarca() {
        CriarModeloVeiculoRequest request = new CriarModeloVeiculoRequest("Toyota", "Corolla", 5, null, true);
        when(modeloVeiculoRepository.existsByMarcaNomeIgnoreCaseAndModeloIgnoreCase("Toyota", "Corolla")).thenReturn(true);

        assertThatThrownBy(() -> modeloVeiculoService.cadastrar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Modelo de veiculo ja cadastrado");

        verify(modeloVeiculoRepository, never()).save(any());
    }

    // --- CONSULTA POR ID ---

    @Test
    @DisplayName("deve retornar modelo de veiculo quando buscar por ID existente")
    void deve_retornarModeloVeiculo_quando_buscarPorIdExistente() {
        UUID id = UUID.randomUUID();
        Marca marca = Marca.builder().id(UUID.randomUUID()).nome("Chevrolet").logo("http://chevy.png").build();
        ModeloVeiculo modelo = ModeloVeiculo.builder()
                .id(id)
                .marca(marca)
                .modelo("Spin")
                .numPassageiro(7)
                .ativo(true)
                .build();

        when(modeloVeiculoRepository.findById(id)).thenReturn(Optional.of(modelo));

        ModeloVeiculoResponse response = modeloVeiculoService.buscarPorId(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.marca()).isEqualTo("Chevrolet");
        assertThat(response.modelo()).isEqualTo("Spin");
        assertThat(response.numPassageiro()).isEqualTo(7);
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando buscar por ID inexistente")
    void deve_lancarResourceNotFoundException_quando_buscarPorIdInexistente() {
        UUID id = UUID.randomUUID();
        when(modeloVeiculoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> modeloVeiculoService.buscarPorId(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Modelo de veiculo nao encontrado");
    }

    // --- LISTAGEM ---

    @Test
    @DisplayName("deve listar modelos de veiculo com paginacao quando solicitado")
    void deve_listarModelosVeiculoComPaginacao_quando_solicitado() {
        Pageable pageable = PageRequest.of(0, 10);
        Marca mToyota = Marca.builder().id(UUID.randomUUID()).nome("Toyota").build();
        Marca mHonda = Marca.builder().id(UUID.randomUUID()).nome("Honda").build();
        ModeloVeiculo m1 = ModeloVeiculo.builder().id(UUID.randomUUID()).marca(mToyota).modelo("Corolla").numPassageiro(5).ativo(true).build();
        ModeloVeiculo m2 = ModeloVeiculo.builder().id(UUID.randomUUID()).marca(mHonda).modelo("Civic").numPassageiro(5).ativo(true).build();

        when(modeloVeiculoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(m1, m2), pageable, 2));

        Page<ModeloVeiculoResponse> result = modeloVeiculoService.listar(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(ModeloVeiculoResponse::modelo).containsExactly("Corolla", "Civic");
    }

    // --- ALTERAÇÃO ---

    @Test
    @DisplayName("deve alterar modelo de veiculo e limpar marca anterior se orfa")
    void deve_alterarModeloVeiculoELimparMarcaAnterior_se_orfa() {
        UUID id = UUID.randomUUID();
        UUID marcaAntigaId = UUID.randomUUID();
        Marca marcaAntiga = Marca.builder().id(marcaAntigaId).nome("Toyota").logo("http://toyota.png").build();
        Marca marcaNova = Marca.builder().id(UUID.randomUUID()).nome("Lexus").build();

        ModeloVeiculo modeloExistente = ModeloVeiculo.builder()
                .id(id)
                .marca(marcaAntiga)
                .modelo("Corolla")
                .numPassageiro(5)
                .ativo(true)
                .build();

        AtualizarModeloVeiculoRequest request = new AtualizarModeloVeiculoRequest("Lexus", "ES300", 5, "http://lexus.png");

        when(modeloVeiculoRepository.findById(id)).thenReturn(Optional.of(modeloExistente));
        when(modeloVeiculoRepository.existsByMarcaNomeIgnoreCaseAndModeloIgnoreCaseAndIdNot("Lexus", "ES300", id)).thenReturn(false);
        when(marcaRepository.findByNomeIgnoreCase("Lexus")).thenReturn(Optional.of(marcaNova));
        when(modeloVeiculoRepository.save(any(ModeloVeiculo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(modeloVeiculoRepository.existsByMarcaId(marcaAntigaId)).thenReturn(false);

        ModeloVeiculoResponse response = modeloVeiculoService.alterar(id, request);

        assertThat(response.marca()).isEqualTo("Lexus");
        assertThat(response.modelo()).isEqualTo("ES300");
        verify(marcaRepository).delete(marcaAntiga);
        verify(storageService).removerLogo("http://toyota.png");
    }

    // --- ATUALIZAR LOGO (MULTIPART / STORAGE) ---

    @Test
    @DisplayName("deve atualizar logotipo da marca do modelo no storage com sucesso")
    void deve_atualizarLogotipoDaMarcaDoModeloNoStorageComSucesso() {
        UUID id = UUID.randomUUID();
        Marca marca = Marca.builder().id(UUID.randomUUID()).nome("Toyota").logo("http://antiga.png").build();
        ModeloVeiculo modelo = ModeloVeiculo.builder().id(id).marca(marca).modelo("Corolla").numPassageiro(5).build();
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "nova.png", "image/png", new byte[]{5, 6});
        String urlNova = "http://storage/nova.png";

        when(modeloVeiculoRepository.findById(id)).thenReturn(Optional.of(modelo));
        when(storageService.armazenarLogo("Toyota", arquivo)).thenReturn(urlNova);

        ModeloVeiculoResponse response = modeloVeiculoService.atualizarLogo(id, arquivo);

        assertThat(response.logo()).isEqualTo(urlNova);
        verify(storageService).removerLogo("http://antiga.png");
        verify(marcaRepository).save(marca);
    }

    // --- INATIVAÇÃO E ATIVAÇÃO ---

    @Test
    @DisplayName("deve inativar modelo de veiculo com sucesso quando solicitado")
    void deve_inativarModeloVeiculoComSucesso_quando_solicitado() {
        UUID id = UUID.randomUUID();
        Marca marca = Marca.builder().id(UUID.randomUUID()).nome("Ford").build();
        ModeloVeiculo modelo = ModeloVeiculo.builder().id(id).marca(marca).modelo("Ka").numPassageiro(4).ativo(true).build();

        when(modeloVeiculoRepository.findById(id)).thenReturn(Optional.of(modelo));

        modeloVeiculoService.inativar(id);

        assertThat(modelo.isAtivo()).isFalse();
        verify(modeloVeiculoRepository).save(modelo);
    }

    @Test
    @DisplayName("deve ativar modelo de veiculo inativo com sucesso quando solicitado")
    void deve_ativarModeloVeiculoInativoComSucesso_quando_solicitado() {
        UUID id = UUID.randomUUID();
        Marca marca = Marca.builder().id(UUID.randomUUID()).nome("Ford").build();
        ModeloVeiculo modelo = ModeloVeiculo.builder().id(id).marca(marca).modelo("Ka").numPassageiro(4).ativo(false).build();

        when(modeloVeiculoRepository.findById(id)).thenReturn(Optional.of(modelo));
        when(modeloVeiculoRepository.save(any(ModeloVeiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        ModeloVeiculoResponse response = modeloVeiculoService.ativar(id);

        assertThat(response.ativo()).isTrue();
        assertThat(modelo.isAtivo()).isTrue();
    }
}