package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarTipoLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.CriarTipoLocalidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.TipoLocalidadeResponse;
import br.mp.mpms.rotamp.v1.model.entity.TipoLocalidade;
import br.mp.mpms.rotamp.v1.repository.TipoLocalidadeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoLocalidadeServiceTest {

    @Mock
    private TipoLocalidadeRepository tipoLocalidadeRepository;

    @InjectMocks
    private TipoLocalidadeService tipoLocalidadeService;

    @Test
    @DisplayName("deve cadastrar tipo de localidade com sucesso quando dados validos")
    void deve_cadastrarTipoLocalidadeComSucesso() {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria de Justiça", true);
        UUID id = UUID.randomUUID();

        when(tipoLocalidadeRepository.existsByCodigo("PROJUS")).thenReturn(false);
        when(tipoLocalidadeRepository.save(any(TipoLocalidade.class))).thenAnswer(inv -> {
            TipoLocalidade t = inv.getArgument(0);
            t.setId(id);
            return t;
        });

        TipoLocalidadeResponse response = tipoLocalidadeService.cadastrar(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.codigo()).isEqualTo("PROJUS");
        assertThat(response.tipoLocalidade()).isEqualTo("Promotoria de Justiça");
        assertThat(response.ativo()).isTrue();
    }

    @Test
    @DisplayName("deve lancar ConflictException 409 quando cadastrar tipo com codigo ja existente")
    void deve_lancarConflict_quandoCadastrarCodigoDuplicado() {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria", true);
        when(tipoLocalidadeRepository.existsByCodigo("PROJUS")).thenReturn(true);

        assertThatThrownBy(() -> tipoLocalidadeService.cadastrar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Codigo de tipo de localidade ja cadastrado");
    }

    @Test
    @DisplayName("deve buscar tipo por ID com sucesso quando existente")
    void deve_buscarPorIdComSucesso() {
        UUID id = UUID.randomUUID();
        TipoLocalidade tipo = TipoLocalidade.builder()
                .id(id)
                .codigo("SEDEADM")
                .tipoLocalidade("Sede Administrativa")
                .ativo(true)
                .build();

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipo));

        TipoLocalidadeResponse response = tipoLocalidadeService.buscarPorId(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.codigo()).isEqualTo("SEDEADM");
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando buscar tipo por ID inexistente")
    void deve_lancarNotFound_quandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoLocalidadeService.buscarPorId(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deve listar tipos de localidade paginados")
    void deve_listarTiposPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        TipoLocalidade t1 = TipoLocalidade.builder().id(UUID.randomUUID()).codigo("T1").tipoLocalidade("Tipo 1").ativo(true).build();
        when(tipoLocalidadeRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(t1)));

        Page<TipoLocalidadeResponse> page = tipoLocalidadeService.listar(pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).codigo()).isEqualTo("T1");
    }

    @Test
    @DisplayName("deve alterar tipo de localidade com sucesso")
    void deve_alterarComSucesso() {
        UUID id = UUID.randomUUID();
        TipoLocalidade existente = TipoLocalidade.builder().id(id).codigo("OLD").tipoLocalidade("Antigo").ativo(true).build();
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("NEW", "Novo", true);

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(existente));
        when(tipoLocalidadeRepository.existsByCodigoAndIdNot("NEW", id)).thenReturn(false);
        when(tipoLocalidadeRepository.save(any(TipoLocalidade.class))).thenAnswer(inv -> inv.getArgument(0));

        TipoLocalidadeResponse response = tipoLocalidadeService.alterar(id, request);

        assertThat(response.codigo()).isEqualTo("NEW");
        assertThat(response.tipoLocalidade()).isEqualTo("Novo");
    }

    @Test
    @DisplayName("deve lancar ConflictException quando alterar para codigo em uso por outro tipo")
    void deve_lancarConflict_quandoAlterarComCodigoEmUso() {
        UUID id = UUID.randomUUID();
        TipoLocalidade existente = TipoLocalidade.builder().id(id).codigo("OLD").tipoLocalidade("Antigo").ativo(true).build();
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("DUPLICATE", "Novo", true);

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(existente));
        when(tipoLocalidadeRepository.existsByCodigoAndIdNot("DUPLICATE", id)).thenReturn(true);

        assertThatThrownBy(() -> tipoLocalidadeService.alterar(id, request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("deve inativar tipo de localidade logicamente (soft delete)")
    void deve_inativarTipoLogicamente() {
        UUID id = UUID.randomUUID();
        TipoLocalidade tipo = TipoLocalidade.builder().id(id).codigo("COD").tipoLocalidade("Desc").ativo(true).build();

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipo));

        tipoLocalidadeService.inativar(id);

        assertThat(tipo.isAtivo()).isFalse();
        verify(tipoLocalidadeRepository).save(tipo);
    }

    @Test
    @DisplayName("deve ativar tipo de localidade logicamente")
    void deve_ativarTipoLogicamente() {
        UUID id = UUID.randomUUID();
        TipoLocalidade tipo = TipoLocalidade.builder().id(id).codigo("COD").tipoLocalidade("Desc").ativo(false).build();

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipo));
        when(tipoLocalidadeRepository.save(any(TipoLocalidade.class))).thenAnswer(inv -> inv.getArgument(0));

        TipoLocalidadeResponse response = tipoLocalidadeService.ativar(id);

        assertThat(response.ativo()).isTrue();
        verify(tipoLocalidadeRepository).save(tipo);
    }
}