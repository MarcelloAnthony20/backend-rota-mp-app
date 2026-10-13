package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.BusinessException;
import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarVeiculoRequest;
import br.mp.mpms.rotamp.v1.model.dto.VeiculoDetalhesResponse;
import br.mp.mpms.rotamp.v1.model.dto.VeiculoResponse;
import br.mp.mpms.rotamp.v1.model.entity.Localidade;
import br.mp.mpms.rotamp.v1.model.entity.Marca;
import br.mp.mpms.rotamp.v1.model.entity.ModeloVeiculo;
import br.mp.mpms.rotamp.v1.model.entity.Veiculo;
import br.mp.mpms.rotamp.v1.repository.LocalidadeRepository;
import br.mp.mpms.rotamp.v1.repository.ModeloVeiculoRepository;
import br.mp.mpms.rotamp.v1.repository.VeiculoRepository;
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
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ModeloVeiculoRepository modeloVeiculoRepository;

    @Mock
    private LocalidadeRepository localidadeRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    // --- CADASTRO (HU-04.1) ---

    @Test
    @DisplayName("deve cadastrar veiculo com sucesso quando dados validos")
    void deve_cadastrarVeiculoComSucesso_quando_dadosValidos() {
        UUID localidadeId = UUID.randomUUID();
        UUID modeloId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        CriarVeiculoRequest request = new CriarVeiculoRequest(localidadeId, modeloId, "RPA1B23", 2024, true);

        Marca marca = Marca.builder().nome("Toyota").build();
        ModeloVeiculo modelo = ModeloVeiculo.builder().id(modeloId).marca(marca).modelo("Corolla").numPassageiro(5).ativo(true).build();
        Localidade localidade = Localidade.builder().id(localidadeId).nome("Sede PGJ").ativo(true).build();

        when(veiculoRepository.existsByPlacaIgnoreCase("RPA1B23")).thenReturn(false);
        when(modeloVeiculoRepository.findById(modeloId)).thenReturn(Optional.of(modelo));
        when(localidadeRepository.findById(localidadeId)).thenReturn(Optional.of(localidade));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(inv -> {
            Veiculo v = inv.getArgument(0);
            org.springframework.test.util.ReflectionTestUtils.setField(v, "id", veiculoId);
            return v;
        });

        VeiculoResponse response = veiculoService.cadastrar(request);

        assertThat(response.id()).isEqualTo(veiculoId);
        assertThat(response.placa()).isEqualTo("RPA1B23");
        assertThat(response.ano()).isEqualTo(2024);
        assertThat(response.ativo()).isTrue();

        ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
        verify(veiculoRepository).save(captor.capture());
        assertThat(captor.getValue().getPlaca()).isEqualTo("RPA1B23");
    }

    @Test
    @DisplayName("deve lancar ConflictException quando placa ja cadastrada ao cadastrar")
    void deve_lancarConflictException_quando_placaJaCadastradaAoCadastrar() {
        CriarVeiculoRequest request = new CriarVeiculoRequest(UUID.randomUUID(), UUID.randomUUID(), "RPA1B23", 2024, true);
        when(veiculoRepository.existsByPlacaIgnoreCase("RPA1B23")).thenReturn(true);

        assertThatThrownBy(() -> veiculoService.cadastrar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Placa de veiculo ja cadastrada");

        verify(veiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando modelo inexistente ao cadastrar")
    void deve_lancarResourceNotFoundException_quando_modeloInexistenteAoCadastrar() {
        UUID modeloId = UUID.randomUUID();
        CriarVeiculoRequest request = new CriarVeiculoRequest(UUID.randomUUID(), modeloId, "RPA1B23", 2024, true);

        when(veiculoRepository.existsByPlacaIgnoreCase("RPA1B23")).thenReturn(false);
        when(modeloVeiculoRepository.findById(modeloId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.cadastrar(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Modelo de veiculo nao encontrado");
    }

    @Test
    @DisplayName("deve lancar BusinessException quando modelo inativo ao cadastrar")
    void deve_lancarBusinessException_quando_modeloInativoAoCadastrar() {
        UUID modeloId = UUID.randomUUID();
        CriarVeiculoRequest request = new CriarVeiculoRequest(UUID.randomUUID(), modeloId, "RPA1B23", 2024, true);
        ModeloVeiculo modeloInativo = ModeloVeiculo.builder().id(modeloId).ativo(false).build();

        when(veiculoRepository.existsByPlacaIgnoreCase("RPA1B23")).thenReturn(false);
        when(modeloVeiculoRepository.findById(modeloId)).thenReturn(Optional.of(modeloInativo));

        assertThatThrownBy(() -> veiculoService.cadastrar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Nao e possivel vincular um modelo de veiculo inativo");
    }

    @Test
    @DisplayName("deve lancar BusinessException quando localidade inativa ao cadastrar")
    void deve_lancarBusinessException_quando_localidadeInativaAoCadastrar() {
        UUID localidadeId = UUID.randomUUID();
        UUID modeloId = UUID.randomUUID();
        CriarVeiculoRequest request = new CriarVeiculoRequest(localidadeId, modeloId, "RPA1B23", 2024, true);

        ModeloVeiculo modelo = ModeloVeiculo.builder().id(modeloId).ativo(true).build();
        Localidade localidadeInativa = Localidade.builder().id(localidadeId).ativo(false).build();

        when(veiculoRepository.existsByPlacaIgnoreCase("RPA1B23")).thenReturn(false);
        when(modeloVeiculoRepository.findById(modeloId)).thenReturn(Optional.of(modelo));
        when(localidadeRepository.findById(localidadeId)).thenReturn(Optional.of(localidadeInativa));

        assertThatThrownBy(() -> veiculoService.cadastrar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Nao e possivel vincular um veiculo a uma localidade inativa");
    }

    // --- CONSULTA POR ID E DETALHES (HU-04.6) ---

    @Test
    @DisplayName("deve retornar veiculo quando buscar por ID existente")
    void deve_retornarVeiculo_quando_buscarPorIdExistente() {
        UUID id = UUID.randomUUID();
        Marca marca = Marca.builder().nome("Toyota").build();
        Localidade localidade = Localidade.builder().id(UUID.randomUUID()).nome("Sede PGJ").build();
        ModeloVeiculo modelo = ModeloVeiculo.builder().id(UUID.randomUUID()).marca(marca).modelo("Corolla").build();
        Veiculo veiculo = Veiculo.builder().id(id).placa("RPA1B23").ano(2024).fkLocalidade(localidade).fkModelo(modelo).ativo(true).build();

        when(veiculoRepository.findById(id)).thenReturn(Optional.of(veiculo));

        VeiculoResponse response = veiculoService.buscarPorId(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.placa()).isEqualTo("RPA1B23");
    }

    @Test
    @DisplayName("deve retornar detalhes completos do veiculo quando buscar detalhes por ID existente")
    void deve_retornarDetalhesCompletosDoVeiculo_quando_buscarDetalhesPorIdExistente() {
        UUID id = UUID.randomUUID();
        Marca marca = Marca.builder().nome("Toyota").build();
        Localidade localidade = Localidade.builder().id(UUID.randomUUID()).nome("Edifício Sede - PGJ").build();
        ModeloVeiculo modelo = ModeloVeiculo.builder().id(UUID.randomUUID()).marca(marca).modelo("Corolla").numPassageiro(5).build();
        Veiculo veiculo = Veiculo.builder().id(id).placa("RPA1B23").ano(2024).fkLocalidade(localidade).fkModelo(modelo).ativo(true).build();

        when(veiculoRepository.findByIdComDetalhes(id)).thenReturn(Optional.of(veiculo));

        VeiculoDetalhesResponse response = veiculoService.buscarDetalhesPorId(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.marca()).isEqualTo("Toyota");
        assertThat(response.modelo()).isEqualTo("Corolla");
        assertThat(response.localidadeNome()).isEqualTo("Edifício Sede - PGJ");
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando buscar por ID inexistente")
    void deve_lancarResourceNotFoundException_quando_buscarPorIdInexistente() {
        UUID id = UUID.randomUUID();
        when(veiculoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> veiculoService.buscarPorId(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Veiculo nao encontrado");
    }

    // --- LISTAGEM (HU-04.2) ---

    @Test
    @DisplayName("deve listar veiculos com paginacao quando solicitado")
    void deve_listarVeiculosComPaginacao_quando_solicitado() {
        Pageable pageable = PageRequest.of(0, 10);
        Localidade loc = Localidade.builder().id(UUID.randomUUID()).build();
        ModeloVeiculo mod = ModeloVeiculo.builder().id(UUID.randomUUID()).build();
        Veiculo v1 = Veiculo.builder().id(UUID.randomUUID()).placa("ABC1234").ano(2023).fkLocalidade(loc).fkModelo(mod).ativo(true).build();
        Veiculo v2 = Veiculo.builder().id(UUID.randomUUID()).placa("XYZ5678").ano(2024).fkLocalidade(loc).fkModelo(mod).ativo(false).build();

        when(veiculoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(v1, v2), pageable, 2));

        Page<VeiculoResponse> result = veiculoService.listar(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(v -> v.placa()).containsExactly("ABC1234", "XYZ5678");
    }

    // --- ALTERAÇÃO (HU-04.3) ---

    @Test
    @DisplayName("deve alterar veiculo com sucesso quando dados validos")
    void deve_alterarVeiculoComSucesso_quando_dadosValidos() {
        UUID id = UUID.randomUUID();
        UUID novoModeloId = UUID.randomUUID();
        UUID novaLocalidadeId = UUID.randomUUID();

        Localidade locOriginal = Localidade.builder().id(UUID.randomUUID()).ativo(true).build();
        ModeloVeiculo modOriginal = ModeloVeiculo.builder().id(UUID.randomUUID()).ativo(true).build();
        Veiculo veiculoExistente = Veiculo.builder().id(id).placa("OLD1234").ano(2020).fkLocalidade(locOriginal).fkModelo(modOriginal).ativo(true).build();

        ModeloVeiculo novoModelo = ModeloVeiculo.builder().id(novoModeloId).ativo(true).build();
        Localidade novaLocalidade = Localidade.builder().id(novaLocalidadeId).ativo(true).build();

        AtualizarVeiculoRequest request = new AtualizarVeiculoRequest(novaLocalidadeId, novoModeloId, "NEW5678", 2024);

        when(veiculoRepository.findById(id)).thenReturn(Optional.of(veiculoExistente));
        when(veiculoRepository.existsByPlacaIgnoreCaseAndIdNot("NEW5678", id)).thenReturn(false);
        when(modeloVeiculoRepository.findById(novoModeloId)).thenReturn(Optional.of(novoModelo));
        when(localidadeRepository.findById(novaLocalidadeId)).thenReturn(Optional.of(novaLocalidade));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        VeiculoResponse response = veiculoService.alterar(id, request);

        assertThat(response.placa()).isEqualTo("NEW5678");
        assertThat(response.ano()).isEqualTo(2024);
        assertThat(response.modeloId()).isEqualTo(novoModeloId);
        assertThat(response.localidadeId()).isEqualTo(novaLocalidadeId);
    }

    @Test
    @DisplayName("deve lancar ConflictException quando alterar para placa ja existente em outro veiculo")
    void deve_lancarConflictException_quando_alterarParaPlacaJaExistenteEmOutroVeiculo() {
        UUID id = UUID.randomUUID();
        Veiculo veiculoExistente = Veiculo.builder().id(id).placa("OLD1234").build();
        AtualizarVeiculoRequest request = new AtualizarVeiculoRequest(UUID.randomUUID(), UUID.randomUUID(), "EMUSO12", 2024);

        when(veiculoRepository.findById(id)).thenReturn(Optional.of(veiculoExistente));
        when(veiculoRepository.existsByPlacaIgnoreCaseAndIdNot("EMUSO12", id)).thenReturn(true);

        assertThatThrownBy(() -> veiculoService.alterar(id, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Placa de veiculo ja em uso");

        verify(veiculoRepository, never()).save(any());
    }

    // --- INATIVAÇÃO E ATIVAÇÃO (HU-04.4) ---

    @Test
    @DisplayName("deve inativar veiculo com sucesso quando solicitado")
    void deve_inativarVeiculoComSucesso_quando_solicitado() {
        UUID id = UUID.randomUUID();
        Veiculo veiculo = Veiculo.builder().id(id).ativo(true).build();

        when(veiculoRepository.findById(id)).thenReturn(Optional.of(veiculo));

        veiculoService.inativar(id);

        assertThat(veiculo.isAtivo()).isFalse();
        verify(veiculoRepository).save(veiculo);
    }

    @Test
    @DisplayName("deve ativar veiculo com sucesso quando modelo e localidade ativos")
    void deve_ativarVeiculoComSucesso_quando_modeloELocalidadeAtivos() {
        UUID id = UUID.randomUUID();
        Localidade loc = Localidade.builder().id(UUID.randomUUID()).ativo(true).build();
        ModeloVeiculo mod = ModeloVeiculo.builder().id(UUID.randomUUID()).ativo(true).build();
        Veiculo veiculo = Veiculo.builder().id(id).placa("RPA1B23").ano(2024).fkLocalidade(loc).fkModelo(mod).ativo(false).build();

        when(veiculoRepository.findById(id)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        VeiculoResponse response = veiculoService.ativar(id);

        assertThat(response.ativo()).isTrue();
        assertThat(veiculo.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("deve lancar BusinessException quando ativar veiculo cujo modelo esta inativo")
    void deve_lancarBusinessException_quando_ativarVeiculoCujoModeloEstaInativo() {
        UUID id = UUID.randomUUID();
        Localidade loc = Localidade.builder().id(UUID.randomUUID()).ativo(true).build();
        ModeloVeiculo modInativo = ModeloVeiculo.builder().id(UUID.randomUUID()).ativo(false).build();
        Veiculo veiculo = Veiculo.builder().id(id).fkLocalidade(loc).fkModelo(modInativo).ativo(false).build();

        when(veiculoRepository.findById(id)).thenReturn(Optional.of(veiculo));

        assertThatThrownBy(() -> veiculoService.ativar(id))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("modelo vinculado esta inativo");
    }

    // --- VINCULAR LOCALIDADE (HU-04.5) ---

    @Test
    @DisplayName("deve vincular veiculo a nova localidade com sucesso quando localidade ativa")
    void deve_vincularVeiculoANovaLocalidadeComSucesso_quando_localidadeAtiva() {
        UUID veiculoId = UUID.randomUUID();
        UUID novaLocId = UUID.randomUUID();

        Localidade locAntiga = Localidade.builder().id(UUID.randomUUID()).ativo(true).build();
        ModeloVeiculo mod = ModeloVeiculo.builder().id(UUID.randomUUID()).ativo(true).build();
        Veiculo veiculo = Veiculo.builder().id(veiculoId).placa("RPA1B23").ano(2024).fkLocalidade(locAntiga).fkModelo(mod).ativo(true).build();

        Localidade novaLoc = Localidade.builder().id(novaLocId).ativo(true).build();

        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(localidadeRepository.findById(novaLocId)).thenReturn(Optional.of(novaLoc));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        VeiculoResponse response = veiculoService.vincularLocalidade(veiculoId, novaLocId);

        assertThat(response.localidadeId()).isEqualTo(novaLocId);
        verify(veiculoRepository).save(veiculo);
    }
}