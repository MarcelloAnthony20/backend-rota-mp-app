package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ConflictException;
import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.model.dto.AtualizarCidadeRequest;
import br.mp.mpms.rotamp.v1.model.dto.CidadeResponse;
import br.mp.mpms.rotamp.v1.model.dto.CriarCidadeRequest;
import br.mp.mpms.rotamp.v1.model.entity.Cidade;
import br.mp.mpms.rotamp.v1.repository.CidadeRepository;
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
class CidadeServiceTest {

    @Mock
    private CidadeRepository cidadeRepository;

    @InjectMocks
    private CidadeService cidadeService;

    // --- CADASTRO (CT-CID-002, CT-CID-003, CT-CID-007, CT-CID-025) ---

    @Test
    @DisplayName("deve cadastrar cidade com sucesso quando dados validos")
    void deve_cadastrarCidadeComSucesso_quando_dadosValidos() {
        CriarCidadeRequest request = new CriarCidadeRequest("Campo Grande", "5002704", true);
        UUID idGerado = UUID.fromString("c1d00001-0000-0000-0000-000000000001");

        when(cidadeRepository.existsByCodigo("5002704")).thenReturn(false);
        when(cidadeRepository.save(any(Cidade.class))).thenAnswer(invocation -> {
            Cidade c = invocation.getArgument(0);
            c.setId(idGerado);
            return c;
        });

        CidadeResponse response = cidadeService.cadastrar(request);

        assertThat(response.id()).isEqualTo(idGerado);
        assertThat(response.nome()).isEqualTo("Campo Grande");
        assertThat(response.codigo()).isEqualTo("5002704");
        assertThat(response.ativo()).isTrue();

        ArgumentCaptor<Cidade> captor = ArgumentCaptor.forClass(Cidade.class);
        verify(cidadeRepository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Campo Grande");
        assertThat(captor.getValue().getCodigo()).isEqualTo("5002704");
    }

    @Test
    @DisplayName("deve lancar ConflictException quando codigo ja cadastrado ao cadastrar")
    void deve_lancarConflictException_quando_codigoJaCadastradoAoCadastrar() {
        CriarCidadeRequest request = new CriarCidadeRequest("Campo Grande", "5002704", true);
        when(cidadeRepository.existsByCodigo("5002704")).thenReturn(true);

        assertThatThrownBy(() -> cidadeService.cadastrar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Codigo de cidade ja cadastrado: 5002704");

        verify(cidadeRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve cadastrar cidade com status inativo quando especificado")
    void deve_cadastrarCidadeComStatusInativo_quando_especificado() {
        CriarCidadeRequest request = new CriarCidadeRequest("Corumba", "5003207", false);
        UUID idGerado = UUID.fromString("c1d00007-0000-0000-0000-000000000007");

        when(cidadeRepository.existsByCodigo("5003207")).thenReturn(false);
        when(cidadeRepository.save(any(Cidade.class))).thenAnswer(invocation -> {
            Cidade c = invocation.getArgument(0);
            c.setId(idGerado);
            return c;
        });

        CidadeResponse response = cidadeService.cadastrar(request);

        assertThat(response.ativo()).isFalse();
    }

    // --- CONSULTA POR ID (CT-CID-012, CT-CID-013) ---

    @Test
    @DisplayName("deve retornar cidade quando buscar por ID existente")
    void deve_retornarCidade_quando_buscarPorIdExistente() {
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        Cidade cidade = Cidade.builder()
                .id(id)
                .nome("Campo Grande")
                .codigo("5002704")
                .ativo(true)
                .build();
        when(cidadeRepository.findById(id)).thenReturn(Optional.of(cidade));

        CidadeResponse response = cidadeService.buscarPorId(id);

        assertThat(response).isEqualTo(new CidadeResponse(id, "Campo Grande", "5002704", true));
        verify(cidadeRepository).findById(id);
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando buscar por ID inexistente")
    void deve_lancarResourceNotFoundException_quando_buscarPorIdInexistente() {
        UUID id = UUID.fromString("c1d99999-9999-9999-9999-999999999999");
        when(cidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cidadeService.buscarPorId(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cidade nao encontrada");

        verify(cidadeRepository).findById(id);
    }

    // --- BUSCA POR NOME (CT-CID-014, CT-CID-015) ---

    @Test
    @DisplayName("deve retornar cidades quando buscar por nome com correspondencia")
    void deve_retornarCidades_quando_buscarPorNomeComCorrespondencia() {
        Pageable pageable = PageRequest.of(0, 10);
        Cidade cidade = Cidade.builder()
                .id(UUID.fromString("c1d00001-0000-0000-0000-000000000001"))
                .nome("Campo Grande")
                .codigo("5002704")
                .ativo(true)
                .build();
        when(cidadeRepository.findByNomeContainingIgnoreCase("Campo", pageable))
                .thenReturn(new PageImpl<>(List.of(cidade), pageable, 1));

        Page<CidadeResponse> result = cidadeService.buscarPorNome("Campo", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).nome()).isEqualTo("Campo Grande");
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando buscar por nome sem correspondencia")
    void deve_lancarResourceNotFoundException_quando_buscarPorNomeSemCorrespondencia() {
        Pageable pageable = PageRequest.of(0, 10);
        when(cidadeRepository.findByNomeContainingIgnoreCase("InexistenteXYZ", pageable))
                .thenReturn(Page.empty(pageable));

        assertThatThrownBy(() -> cidadeService.buscarPorNome("InexistenteXYZ", pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Nenhuma cidade encontrada para o criterio informado");
    }

    // --- LISTAGEM (CT-CID-029) ---

    @Test
    @DisplayName("deve listar cidades com paginacao quando solicitado")
    void deve_listarCidadesComPaginacao_quando_solicitado() {
        Pageable pageable = PageRequest.of(0, 10);
        Cidade cidadeA = Cidade.builder()
                .id(UUID.fromString("c1d00001-0000-0000-0000-000000000001"))
                .nome("Campo Grande Alterada")
                .codigo("5002704")
                .ativo(true)
                .build();
        Cidade cidadeB = Cidade.builder()
                .id(UUID.fromString("c1d00002-0000-0000-0000-000000000002"))
                .nome("Dourados")
                .codigo("5003702")
                .ativo(false)
                .build();
        when(cidadeRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(cidadeA, cidadeB), pageable, 2));

        Page<CidadeResponse> result = cidadeService.listar(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(CidadeResponse::nome)
                .containsExactly("Campo Grande Alterada", "Dourados");
        assertThat(result.getContent()).extracting(CidadeResponse::ativo)
                .containsExactly(true, false);
    }

    // --- ALTERAÇÃO (CT-CID-016, CT-CID-017, CT-CID-032, CT-CID-033) ---

    @Test
    @DisplayName("deve alterar nome e codigo da cidade com sucesso quando dados validos")
    void deve_alterarNomeECodigoDaCidadeComSucesso_quando_dadosValidos() {
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        Cidade cidadeExistente = Cidade.builder()
                .id(id)
                .nome("Campo Grande")
                .codigo("5002704")
                .ativo(true)
                .build();

        AtualizarCidadeRequest request = new AtualizarCidadeRequest("Campo Grande Alterada", "5002704", true);

        when(cidadeRepository.findById(id)).thenReturn(Optional.of(cidadeExistente));
        when(cidadeRepository.existsByCodigoAndIdNot("5002704", id)).thenReturn(false);
        when(cidadeRepository.save(any(Cidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CidadeResponse response = cidadeService.alterar(id, request);

        assertThat(response.nome()).isEqualTo("Campo Grande Alterada");
        assertThat(response.codigo()).isEqualTo("5002704");
        assertThat(response.ativo()).isTrue();
    }

    @Test
    @DisplayName("deve lancar ConflictException quando codigo ja em uso por outra cidade ao alterar")
    void deve_lancarConflictException_quando_codigoJaEmUsoPorOutraCidadeAoAlterar() {
        UUID id = UUID.fromString("c1d00007-0000-0000-0000-000000000007");
        Cidade cidade = Cidade.builder()
                .id(id)
                .nome("Corumba")
                .codigo("5003207")
                .ativo(true)
                .build();

        AtualizarCidadeRequest request = new AtualizarCidadeRequest("Corumba", "5002704", true);

        when(cidadeRepository.findById(id)).thenReturn(Optional.of(cidade));
        when(cidadeRepository.existsByCodigoAndIdNot("5002704", id)).thenReturn(true);

        assertThatThrownBy(() -> cidadeService.alterar(id, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Codigo de cidade ja em uso por outra cidade: 5002704");

        verify(cidadeRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando alterar cidade inexistente")
    void deve_lancarResourceNotFoundException_quando_alterarCidadeInexistente() {
        UUID id = UUID.fromString("c1d99999-9999-9999-9999-999999999999");
        AtualizarCidadeRequest request = new AtualizarCidadeRequest("Fantasma", "5009999", true);

        when(cidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cidadeService.alterar(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cidade nao encontrada");

        verify(cidadeRepository, never()).save(any());
    }

    // --- INATIVAÇÃO (CT-CID-008, CT-CID-011, CT-CID-022, CT-CID-035) ---

    @Test
    @DisplayName("deve inativar cidade ativa com sucesso quando solicitado")
    void deve_inativarCidadeAtivaComSucesso_quando_solicitado() {
        UUID id = UUID.fromString("c1d00002-0000-0000-0000-000000000002");
        Cidade cidade = Cidade.builder()
                .id(id)
                .nome("Dourados")
                .codigo("5003702")
                .ativo(true)
                .build();

        when(cidadeRepository.findById(id)).thenReturn(Optional.of(cidade));

        cidadeService.inativar(id);

        assertThat(cidade.isAtivo()).isFalse();
        verify(cidadeRepository).save(cidade);
    }

    @Test
    @DisplayName("deve manter cidade inativa de forma idempotente quando inativar cidade ja inativa")
    void deve_manterCidadeInativaDeFormaIdempotente_quando_inativarCidadeJaInativa() {
        UUID id = UUID.fromString("c1d00002-0000-0000-0000-000000000002");
        Cidade cidade = Cidade.builder()
                .id(id)
                .nome("Dourados")
                .codigo("5003702")
                .ativo(false)
                .build();

        when(cidadeRepository.findById(id)).thenReturn(Optional.of(cidade));

        cidadeService.inativar(id);

        assertThat(cidade.isAtivo()).isFalse();
        verify(cidadeRepository).save(cidade);
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando inativar cidade inexistente")
    void deve_lancarResourceNotFoundException_quando_inativarCidadeInexistente() {
        UUID id = UUID.fromString("c1d99999-9999-9999-9999-999999999999");
        when(cidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cidadeService.inativar(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cidade nao encontrada");

        verify(cidadeRepository, never()).save(any());
    }

    // --- ATIVAÇÃO (CT-CID-009, CT-CID-010) ---

    @Test
    @DisplayName("deve ativar cidade inativa com sucesso quando solicitado")
    void deve_ativarCidadeInativaComSucesso_quando_solicitado() {
        UUID id = UUID.fromString("c1d00007-0000-0000-0000-000000000007");
        Cidade cidade = Cidade.builder()
                .id(id)
                .nome("Corumba")
                .codigo("5003207")
                .ativo(false)
                .build();

        when(cidadeRepository.findById(id)).thenReturn(Optional.of(cidade));
        when(cidadeRepository.save(any(Cidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CidadeResponse response = cidadeService.ativar(id);

        assertThat(response.ativo()).isTrue();
        assertThat(cidade.isAtivo()).isTrue();
        verify(cidadeRepository).save(cidade);
    }

    @Test
    @DisplayName("deve manter cidade ativa de forma idempotente quando ativar cidade ja ativa")
    void deve_manterCidadeAtivaDeFormaIdempotente_quando_ativarCidadeJaAtiva() {
        UUID id = UUID.fromString("c1d00001-0000-0000-0000-000000000001");
        Cidade cidade = Cidade.builder()
                .id(id)
                .nome("Campo Grande")
                .codigo("5002704")
                .ativo(true)
                .build();

        when(cidadeRepository.findById(id)).thenReturn(Optional.of(cidade));
        when(cidadeRepository.save(any(Cidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CidadeResponse response = cidadeService.ativar(id);

        assertThat(response.ativo()).isTrue();
        verify(cidadeRepository).save(cidade);
    }

    @Test
    @DisplayName("deve lancar ResourceNotFoundException quando ativar cidade inexistente")
    void deve_lancarResourceNotFoundException_quando_ativarCidadeInexistente() {
        UUID id = UUID.fromString("c1d99999-9999-9999-9999-999999999999");
        when(cidadeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cidadeService.ativar(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cidade nao encontrada");

        verify(cidadeRepository, never()).save(any());
    }
}