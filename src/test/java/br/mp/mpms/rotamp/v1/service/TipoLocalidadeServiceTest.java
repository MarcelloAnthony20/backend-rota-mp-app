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
class TipoLocalidadeServiceTest {

    @Mock
    private TipoLocalidadeRepository tipoLocalidadeRepository;

    @InjectMocks
    private TipoLocalidadeService tipoLocalidadeService;

    // --- CADASTRO (CT-TIP-002, CT-TIP-003, CT-TIP-007, CT-TIP-024) ---

    @Test
    @DisplayName("deve cadastrar tipo de localidade com sucesso quando dados validos")
    void deve_cadastrarTipoLocalidadeComSucesso_quando_dadosValidos() {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria de Justiça", true);
        UUID idGerado = UUID.fromString("b1000001-0000-0000-0000-000000000001");

        when(tipoLocalidadeRepository.existsByCodigo("PROJUS")).thenReturn(false);
        when(tipoLocalidadeRepository.save(any(TipoLocalidade.class))).thenAnswer(invocation -> {
            TipoLocalidade t = invocation.getArgument(0);
            t.setId(idGerado);
            return t;
        });

        TipoLocalidadeResponse response = tipoLocalidadeService.cadastrar(request);

        assertThat(response.id()).isEqualTo(idGerado);
        assertThat(response.codigo()).isEqualTo("PROJUS");
        assertThat(response.tipoLocalidade()).isEqualTo("Promotoria de Justiça");
        assertThat(response.ativo()).isTrue();

        ArgumentCaptor<TipoLocalidade> captor = ArgumentCaptor.forClass(TipoLocalidade.class);
        verify(tipoLocalidadeRepository).save(captor.capture());
        assertThat(captor.getValue().getCodigo()).isEqualTo("PROJUS");
        assertThat(captor.getValue().getTipoLocalidade()).isEqualTo("Promotoria de Justiça");
    }

    @Test
    @DisplayName("deve lancar ConflictException quando codigo ja cadastrado ao cadastrar")
    void deve_lancarConflictException_quando_codigoJaCadastradoAoCadastrar() {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("PROJUS", "Promotoria de Justiça", true);
        when(tipoLocalidadeRepository.existsByCodigo("PROJUS")).thenReturn(true);

        assertThatThrownBy(() -> tipoLocalidadeService.cadastrar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Codigo de tipo de localidade ja cadastrado: PROJUS");

        verify(tipoLocalidadeRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve cadastrar tipo de localidade com status inativo quando especificado")
    void deve_cadastrarTipoLocalidadeComStatusInativo_quando_especificado() {
        CriarTipoLocalidadeRequest request = new CriarTipoLocalidadeRequest("POSTO_INATIVO", "Posto Avançado Provisório", false);
        UUID idGerado = UUID.fromString("b1000007-0000-0000-0000-000000000007");

        when(tipoLocalidadeRepository.existsByCodigo("POSTO_INATIVO")).thenReturn(false);
        when(tipoLocalidadeRepository.save(any(TipoLocalidade.class))).thenAnswer(invocation -> {
            TipoLocalidade t = invocation.getArgument(0);
            t.setId(idGerado);
            return t;
        });

        TipoLocalidadeResponse response = tipoLocalidadeService.cadastrar(request);

        assertThat(response.ativo()).isFalse();
    }

    // --- CONSULTA POR ID (CT-TIP-012, CT-TIP-013) ---

    @Test
    @DisplayName("deve retornar tipo de localidade quando buscar por ID existente")
    void deve_retornarTipoLocalidade_quando_buscarPorIdExistente() {
        UUID id = UUID.fromString("b1000001-0000-0000-0000-000000000001");
        TipoLocalidade tipo = TipoLocalidade.builder()
                .id(id)
                .codigo("PROJUS")
                .tipoLocalidade("Promotoria de Justiça")
                .ativo(true)
                .build();
        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipo));

        TipoLocalidadeResponse response = tipoLocalidadeService.buscarPorId(id);

        assertThat(response).isEqualTo(new TipoLocalidadeResponse(id, "PROJUS", "Promotoria de Justiça", true));
        verify(tipoLocalidadeRepository).findById(id);
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando buscar por ID inexistente")
    void deve_lancarResourceNotFoundException_quando_buscarPorIdInexistente() {
        UUID id = UUID.fromString("b1099999-9999-9999-9999-999999999999");
        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoLocalidadeService.buscarPorId(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de localidade nao encontrado");

        verify(tipoLocalidadeRepository).findById(id);
    }

    // --- LISTAGEM (CT-TIP-028) ---

    @Test
    @DisplayName("deve listar tipos de localidade com paginacao quando solicitado")
    void deve_listarTiposLocalidadeComPaginacao_quando_solicitado() {
        Pageable pageable = PageRequest.of(0, 10);
        TipoLocalidade t1 = TipoLocalidade.builder()
                .id(UUID.fromString("b1000001-0000-0000-0000-000000000001"))
                .codigo("PROJUS")
                .tipoLocalidade("Promotoria de Justiça")
                .ativo(true)
                .build();
        TipoLocalidade t2 = TipoLocalidade.builder()
                .id(UUID.fromString("b1000002-0000-0000-0000-000000000002"))
                .codigo("SEDEADM")
                .tipoLocalidade("Sede Administrativa")
                .ativo(false)
                .build();
        when(tipoLocalidadeRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(t1, t2), pageable, 2));

        Page<TipoLocalidadeResponse> result = tipoLocalidadeService.listar(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(TipoLocalidadeResponse::codigo)
                .containsExactly("PROJUS", "SEDEADM");
        assertThat(result.getContent()).extracting(TipoLocalidadeResponse::ativo)
                .containsExactly(true, false);
    }

    // --- ALTERAÇÃO (CT-TIP-015, CT-TIP-016, CT-TIP-031, CT-TIP-032) ---

    @Test
    @DisplayName("deve alterar descricao e codigo do tipo de localidade com sucesso quando dados validos")
    void deve_alterarDescricaoECodigoDoTipoLocalidadeComSucesso_quando_dadosValidos() {
        UUID id = UUID.fromString("b1000001-0000-0000-0000-000000000001");
        TipoLocalidade tipoExistente = TipoLocalidade.builder()
                .id(id)
                .codigo("PROJUS")
                .tipoLocalidade("Promotoria de Justiça")
                .ativo(true)
                .build();

        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("PROJUS_ALT", "Promotoria Integrada", true);

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipoExistente));
        when(tipoLocalidadeRepository.existsByCodigoAndIdNot("PROJUS_ALT", id)).thenReturn(false);
        when(tipoLocalidadeRepository.save(any(TipoLocalidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TipoLocalidadeResponse response = tipoLocalidadeService.alterar(id, request);

        assertThat(response.codigo()).isEqualTo("PROJUS_ALT");
        assertThat(response.tipoLocalidade()).isEqualTo("Promotoria Integrada");
        assertThat(response.ativo()).isTrue();
    }

    @Test
    @DisplayName("deve lancar ConflictException quando codigo ja em uso por outro tipo ao alterar")
    void deve_lancarConflictException_quando_codigoJaEmUsoPorOutroTipoAoAlterar() {
        UUID id = UUID.fromString("b1000001-0000-0000-0000-000000000001");
        TipoLocalidade tipo = TipoLocalidade.builder()
                .id(id)
                .codigo("PROJUS")
                .tipoLocalidade("Promotoria de Justiça")
                .ativo(true)
                .build();

        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("SEDEADM", "Promotoria de Justiça", true);

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipo));
        when(tipoLocalidadeRepository.existsByCodigoAndIdNot("SEDEADM", id)).thenReturn(true);

        assertThatThrownBy(() -> tipoLocalidadeService.alterar(id, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Codigo de tipo de localidade ja em uso por outro registro: SEDEADM");

        verify(tipoLocalidadeRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando alterar tipo de localidade inexistente")
    void deve_lancarResourceNotFoundException_quando_alterarTipoLocalidadeInexistente() {
        UUID id = UUID.fromString("b1099999-9999-9999-9999-999999999999");
        AtualizarTipoLocalidadeRequest request = new AtualizarTipoLocalidadeRequest("FANTASMA", "Tipo Fantasma", true);

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoLocalidadeService.alterar(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de localidade nao encontrado");

        verify(tipoLocalidadeRepository, never()).save(any());
    }

    // --- INATIVAÇÃO (CT-TIP-008, CT-TIP-011, CT-TIP-034) ---

    @Test
    @DisplayName("deve inativar tipo de localidade ativo com sucesso quando solicitado")
    void deve_inativarTipoLocalidadeAtivoComSucesso_quando_solicitado() {
        UUID id = UUID.fromString("b1000001-0000-0000-0000-000000000001");
        TipoLocalidade tipo = TipoLocalidade.builder()
                .id(id)
                .codigo("PROJUS")
                .tipoLocalidade("Promotoria de Justiça")
                .ativo(true)
                .build();

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipo));

        tipoLocalidadeService.inativar(id);

        assertThat(tipo.isAtivo()).isFalse();
        verify(tipoLocalidadeRepository).save(tipo);
    }

    @Test
    @DisplayName("deve manter tipo de localidade inativo de forma idempotente quando inativar tipo ja inativo")
    void deve_manterTipoLocalidadeInativoDeFormaIdempotente_quando_inativarTipoJaInativo() {
        UUID id = UUID.fromString("b1000009-0000-0000-0000-000000000009");
        TipoLocalidade tipo = TipoLocalidade.builder()
                .id(id)
                .codigo("POSTO_DES")
                .tipoLocalidade("Posto Desativado")
                .ativo(false)
                .build();

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipo));

        tipoLocalidadeService.inativar(id);

        assertThat(tipo.isAtivo()).isFalse();
        verify(tipoLocalidadeRepository).save(tipo);
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando inativar tipo de localidade inexistente")
    void deve_lancarResourceNotFoundException_quando_inativarTipoLocalidadeInexistente() {
        UUID id = UUID.fromString("b1099999-9999-9999-9999-999999999999");
        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoLocalidadeService.inativar(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de localidade nao encontrado");

        verify(tipoLocalidadeRepository, never()).save(any());
    }

    // --- ATIVAÇÃO (CT-TIP-009, CT-TIP-010) ---

    @Test
    @DisplayName("deve ativar tipo de localidade inativo com sucesso quando solicitado")
    void deve_ativarTipoLocalidadeInativoComSucesso_quando_solicitado() {
        UUID id = UUID.fromString("b1000009-0000-0000-0000-000000000009");
        TipoLocalidade tipo = TipoLocalidade.builder()
                .id(id)
                .codigo("POSTO_DES")
                .tipoLocalidade("Posto Desativado")
                .ativo(false)
                .build();

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipo));
        when(tipoLocalidadeRepository.save(any(TipoLocalidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TipoLocalidadeResponse response = tipoLocalidadeService.ativar(id);

        assertThat(response.ativo()).isTrue();
        assertThat(tipo.isAtivo()).isTrue();
        verify(tipoLocalidadeRepository).save(tipo);
    }

    @Test
    @DisplayName("deve manter tipo de localidade ativo de forma idempotente quando ativar tipo ja ativo")
    void deve_manterTipoLocalidadeAtivoDeFormaIdempotente_quando_ativarTipoJaAtivo() {
        UUID id = UUID.fromString("b1000001-0000-0000-0000-000000000001");
        TipoLocalidade tipo = TipoLocalidade.builder()
                .id(id)
                .codigo("PROJUS")
                .tipoLocalidade("Promotoria de Justiça")
                .ativo(true)
                .build();

        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.of(tipo));
        when(tipoLocalidadeRepository.save(any(TipoLocalidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TipoLocalidadeResponse response = tipoLocalidadeService.ativar(id);

        assertThat(response.ativo()).isTrue();
        verify(tipoLocalidadeRepository).save(tipo);
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando ativar tipo de localidade inexistente")
    void deve_lancarResourceNotFoundException_quando_ativarTipoLocalidadeInexistente() {
        UUID id = UUID.fromString("b1099999-9999-9999-9999-999999999999");
        when(tipoLocalidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoLocalidadeService.ativar(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Tipo de localidade nao encontrado");

        verify(tipoLocalidadeRepository, never()).save(any());
    }
}