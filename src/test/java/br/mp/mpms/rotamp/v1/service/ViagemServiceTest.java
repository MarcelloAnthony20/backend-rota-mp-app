package br.mp.mpms.rotamp.v1.service;

import br.mp.mpms.rotamp.infra.exception.ResourceNotFoundException;
import br.mp.mpms.rotamp.v1.repository.ViagemRepository;
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
class ViagemServiceTest {

    @Mock
    private ViagemRepository viagemRepository;

    @InjectMocks
    private ViagemService viagemService;

    @Test
    void deveLancarNotFoundQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(viagemRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> viagemService.buscarPorId(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}