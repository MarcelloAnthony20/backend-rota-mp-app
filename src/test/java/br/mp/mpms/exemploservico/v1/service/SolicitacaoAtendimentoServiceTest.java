package br.mp.mpms.exemploservico.v1.service;

import br.mp.mpms.exemploservico.infra.exception.ResourceNotFoundException;
import br.mp.mpms.exemploservico.v1.repository.SolicitacaoAtendimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitacaoAtendimentoServiceTest {

    @Mock
    private SolicitacaoAtendimentoRepository solicitacaoAtendimentoRepository;

    @InjectMocks
    private SolicitacaoAtendimentoService solicitacaoAtendimentoService;

    @Test
    void deveLancarNotFoundQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(solicitacaoAtendimentoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> solicitacaoAtendimentoService.buscarPorId(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}